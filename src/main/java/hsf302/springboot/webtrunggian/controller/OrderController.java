package hsf302.springboot.webtrunggian.controller;

import hsf302.springboot.webtrunggian.dto.MySalesRow;
import hsf302.springboot.webtrunggian.entity.Order;
import hsf302.springboot.webtrunggian.entity.Listing;
import hsf302.springboot.webtrunggian.entity.User;
import hsf302.springboot.webtrunggian.entity.enums.OrderStatus;
import hsf302.springboot.webtrunggian.service.OrderService;
import hsf302.springboot.webtrunggian.repository.ListingRepository;
import hsf302.springboot.webtrunggian.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final ListingRepository listingRepository;

    private static final BigDecimal FEE_RATE = new BigDecimal("0.05");

    // ---------------------------------------------------------
    // BUYER: Trang xác nhận đặt hàng
    // ---------------------------------------------------------
    @GetMapping("/place/{listingId}")
    public String showPlaceOrder(@PathVariable Integer listingId,
                                 @ModelAttribute("currentUser") User currentUser,
                                 Model model) {
        if (currentUser == null) return "redirect:/auth/login";
        model.addAttribute("listingId", listingId);
        return "order/place-confirm";
    }

    // BUYER: Thực hiện đặt hàng
    @PostMapping("/place")
    public String placeOrder(@RequestParam Integer listingId,
                             @ModelAttribute("currentUser") User currentUser,
                             RedirectAttributes ra) {
        if (currentUser == null) return "redirect:/auth/login";
        try {
            Order order = orderService.placeOrder(listingId, currentUser.getId());
            ra.addFlashAttribute("successMessage", "Đặt hàng thành công! Chờ seller xác nhận.");
            // Redirect về trang đơn của buyer thay vì trang listing
            return "redirect:/orders/my-purchases";
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
            // Redirect về trang đơn để tránh Whitelabel/404 từ listing
            return "redirect:/orders/my-purchases";
        }
    }

    // ---------------------------------------------------------
    // BUYER: Danh sách đơn đã mua
    // ---------------------------------------------------------
    @GetMapping("/my-purchases")
    public String myPurchases(@RequestParam(defaultValue = "0") int page,
                              @RequestParam(required = false) String status,
                              @ModelAttribute("currentUser") User currentUser,
                              Model model) {
        if (currentUser == null) return "redirect:/auth/login";

        Pageable pageable = PageRequest.of(page, 10);
        Page<Order> orders = (status != null && !status.isEmpty())
                ? orderService.getBuyerOrdersByStatus(currentUser.getId(), OrderStatus.valueOf(status), pageable)
                : orderService.getBuyerOrders(currentUser.getId(), pageable);

        model.addAttribute("orders", orders);
        model.addAttribute("currentStatus", status);
        model.addAttribute("allStatuses", OrderStatus.values());
        model.addAttribute("viewType", "buyer");
        return "order/my-orders";
    }

    // ---------------------------------------------------------
    // SELLER: Danh sách đơn nhận được
    // ---------------------------------------------------------
    @GetMapping("/my-sales")
    public String mySales(@RequestParam(defaultValue = "0") int page,
                          @RequestParam(required = false) String status,
                          @ModelAttribute("currentUser") User currentUser,
                          Model model) {
        if (currentUser == null) return "redirect:/auth/login";

        // Nếu có filter status (ví dụ PENDING/CONFIRMED/...) thì chỉ hiển thị các Order thật.
        if (status != null && !status.isEmpty()) {
            Pageable pageable = PageRequest.of(page, 10);
            Page<Order> orders = orderService.getSellerOrdersByStatus(
                    currentUser.getId(),
                    OrderStatus.valueOf(status),
                    pageable
            );
            model.addAttribute("orders", orders);
            model.addAttribute("currentStatus", status);
            model.addAttribute("allStatuses", OrderStatus.values());
            model.addAttribute("viewType", "seller");
            return "order/my-orders";
        }

        Pageable pageable = PageRequest.of(page, 10);

        // Lấy tất cả order của seller để biết listing nào đã có order rồi.
        Page<Order> allOrdersPage = orderService.getSellerOrders(currentUser.getId(), Pageable.unpaged());
        List<Order> allOrders = allOrdersPage.getContent();
        Set<Integer> listingIdsWithOrders = new HashSet<>();
        for (Order o : allOrders) {
            if (o.getListing() != null && o.getListing().getId() != null) {
                listingIdsWithOrders.add(o.getListing().getId());
            }
        }

        // Convert Order -> MySalesRow
        List<MySalesRow> rows = new ArrayList<>();
        for (Order o : allOrders) {
            MySalesRow r = new MySalesRow();
            r.setListingPlaceholder(false);
            r.setId(o.getId());
            r.setStatus(o.getStatus() != null ? o.getStatus().name() : "");
            r.setBuyer(o.getBuyer());
            r.setSeller(o.getSeller());
            r.setListing(o.getListing());
            r.setPriceSnapshot(o.getPriceSnapshot());
            r.setFeeSnapshot(o.getFeeSnapshot());
            r.setFeePayerSnapshot(o.getFeePayerSnapshot());
            r.setEscrowAmount(o.getEscrowAmount());
            r.setCreatedAt(o.getCreatedAt());
            rows.add(r);
        }

        // Thêm placeholder cho những listing seller đã tạo nhưng chưa có order.
        List<Listing> sellerListings = listingRepository.findAll()
                .stream()
                .filter(l -> l.getSeller() != null
                        && l.getSeller().getId() != null
                        && l.getSeller().getId().equals(currentUser.getId()))
                .toList();

        for (Listing l : sellerListings) {
            if (l.getId() == null) continue;
            if (listingIdsWithOrders.contains(l.getId())) continue;

            BigDecimal price = l.getPrice() == null ? BigDecimal.ZERO : l.getPrice();
            BigDecimal fee = price.multiply(FEE_RATE).setScale(2, RoundingMode.HALF_UP);

            BigDecimal escrow;
            String feePayer = l.getFeePayer();
            if ("BUYER".equalsIgnoreCase(feePayer)) {
                escrow = price.add(fee);
            } else if ("SELLER".equalsIgnoreCase(feePayer)) {
                escrow = price;
            } else {
                escrow = price.add(fee.divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP));
            }

            // buyer placeholder để JSP không bị null
            User buyerPlaceholder = new User();
            buyerPlaceholder.setUsername("");

            MySalesRow r = new MySalesRow();
            r.setListingPlaceholder(true);
            r.setId(l.getId());
            r.setStatus(l.getStatus() != null ? l.getStatus() : "AVAILABLE");
            r.setBuyer(buyerPlaceholder);
            r.setSeller(l.getSeller());
            r.setListing(l);
            r.setPriceSnapshot(price);
            r.setFeeSnapshot(fee);
            r.setFeePayerSnapshot(feePayer);
            r.setEscrowAmount(escrow);
            r.setCreatedAt(l.getCreatedAt());
            rows.add(r);
        }

        // Sắp xếp theo thời gian tạo desc (order thật và placeholder cùng pool)
        rows.sort((a, b) -> {
            if (a.getCreatedAt() == null && b.getCreatedAt() == null) return 0;
            if (a.getCreatedAt() == null) return 1;
            if (b.getCreatedAt() == null) return -1;
            return b.getCreatedAt().compareTo(a.getCreatedAt());
        });

        int pageSize = pageable.getPageSize();
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageSize, rows.size());
        List<MySalesRow> pageContent = (start >= rows.size()) ? List.of() : rows.subList(start, end);

        PageImpl<MySalesRow> pageImpl = new PageImpl<>(pageContent, pageable, rows.size());

        model.addAttribute("orders", pageImpl);
        model.addAttribute("viewType", "seller");
        return "order/my-orders";
    }

    // ---------------------------------------------------------
    // Chi tiết đơn hàng (buyer & seller)
    // ---------------------------------------------------------
    @GetMapping("/detail/{orderId}")
    public String orderDetail(@PathVariable Integer orderId,
                              @ModelAttribute("currentUser") User currentUser,
                              Model model) {
        if (currentUser == null) return "redirect:/auth/login";
        try {
            Order order = orderService.getOrderAndValidateAccess(orderId, currentUser.getId());
            model.addAttribute("order", order);
            model.addAttribute("isBuyer",  order.getBuyer().getId().equals(currentUser.getId()));
            model.addAttribute("isSeller", order.getSeller().getId().equals(currentUser.getId()));
            return "order/detail";
        } catch (Exception e) {
            return "redirect:/home";
        }
    }

    // ---------------------------------------------------------
    // BUYER: Hủy đơn
    // ---------------------------------------------------------
    @PostMapping("/{orderId}/cancel")
    public String cancelOrder(@PathVariable Integer orderId,
                              @ModelAttribute("currentUser") User currentUser,
                              RedirectAttributes ra) {
        if (currentUser == null) return "redirect:/auth/login";
        try {
            orderService.cancelOrder(orderId, currentUser.getId());
            ra.addFlashAttribute("successMessage", "Đã hủy đơn và hoàn tiền về ví.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/orders/detail/" + orderId;
    }

    // ---------------------------------------------------------
    // SELLER: Xác nhận đơn
    // ---------------------------------------------------------
    @PostMapping("/{orderId}/confirm")
    public String confirmOrder(@PathVariable Integer orderId,
                               @ModelAttribute("currentUser") User currentUser,
                               RedirectAttributes ra) {
        if (currentUser == null) return "redirect:/auth/login";
        try {
            orderService.confirmOrder(orderId, currentUser.getId());
            ra.addFlashAttribute("successMessage", "Đã xác nhận đơn hàng.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/orders/detail/" + orderId;
    }

    // ---------------------------------------------------------
    // SELLER: Từ chối đơn
    // ---------------------------------------------------------
    @PostMapping("/{orderId}/reject")
    public String rejectOrder(@PathVariable Integer orderId,
                              @ModelAttribute("currentUser") User currentUser,
                              RedirectAttributes ra) {
        if (currentUser == null) return "redirect:/auth/login";
        try {
            orderService.rejectOrder(orderId, currentUser.getId());
            ra.addFlashAttribute("successMessage", "Đã từ chối đơn. Tiền đã hoàn về buyer.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/orders/detail/" + orderId;
    }

    // ---------------------------------------------------------
    // SELLER: Giao hàng
    // ---------------------------------------------------------
    @PostMapping("/{orderId}/deliver")
    public String deliverOrder(@PathVariable Integer orderId,
                               @ModelAttribute("currentUser") User currentUser,
                               RedirectAttributes ra) {
        if (currentUser == null) return "redirect:/auth/login";
        try {
            orderService.deliverOrder(orderId, currentUser.getId());
            ra.addFlashAttribute("successMessage", "Đã giao hàng. Chờ buyer xác nhận.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/orders/detail/" + orderId;
    }

    // ---------------------------------------------------------
    // BUYER: Xác nhận nhận hàng → giải ngân escrow
    // ---------------------------------------------------------
    @PostMapping("/{orderId}/complete")
    public String completeOrder(@PathVariable Integer orderId,
                                @ModelAttribute("currentUser") User currentUser,
                                RedirectAttributes ra) {
        if (currentUser == null) return "redirect:/auth/login";
        try {
            orderService.confirmReceipt(orderId, currentUser.getId());
            ra.addFlashAttribute("successMessage", "Xác nhận thành công! Tiền đã chuyển cho seller.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/orders/detail/" + orderId;
    }

    // ---------------------------------------------------------
    // BUYER: Mở khiếu nại → sang DisputeController (P5)
    // ---------------------------------------------------------
    @PostMapping("/{orderId}/dispute")
    public String disputeOrder(@PathVariable Integer orderId,
                               @ModelAttribute("currentUser") User currentUser,
                               RedirectAttributes ra) {
        if (currentUser == null) return "redirect:/auth/login";
        try {
            orderService.requestDispute(orderId, currentUser.getId());
            return "redirect:/dispute/create/" + orderId;
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/orders/detail/" + orderId;
        }
    }
}
