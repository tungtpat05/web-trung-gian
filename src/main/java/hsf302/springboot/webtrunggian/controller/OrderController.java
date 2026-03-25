package hsf302.springboot.webtrunggian.controller;

import hsf302.springboot.webtrunggian.entity.Dispute;
import hsf302.springboot.webtrunggian.entity.Order;
import hsf302.springboot.webtrunggian.entity.User;
import hsf302.springboot.webtrunggian.entity.enums.OrderStatus;
import hsf302.springboot.webtrunggian.service.DisputeService;
import hsf302.springboot.webtrunggian.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final DisputeService disputeService;

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
            return "redirect:/orders/detail/" + order.getId();
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/listings/" + listingId;
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
        model.addAttribute("disputeMap", new HashMap<Integer, Integer>());
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

        Pageable pageable = PageRequest.of(page, 10);
        Page<Order> orders = (status != null && !status.isEmpty())
                ? orderService.getSellerOrdersByStatus(currentUser.getId(), OrderStatus.valueOf(status), pageable)
                : orderService.getSellerOrders(currentUser.getId(), pageable);

        // Build disputeMap: orderId -> disputeId for DISPUTED orders
        Map<Integer, Integer> disputeMap = new HashMap<>();
        for (Order o : orders.getContent()) {
            if (o.getStatus() == OrderStatus.DISPUTED) {
                Dispute d = disputeService.getDisputeByOrderId(o.getId());
                if (d != null) {
                    disputeMap.put(o.getId(), d.getId());
                }
            }
        }

        model.addAttribute("orders", orders);
        model.addAttribute("currentStatus", status);
        model.addAttribute("allStatuses", OrderStatus.values());
        model.addAttribute("viewType", "seller");
        model.addAttribute("disputeMap", disputeMap);
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
}
