package hsf302.springboot.webtrunggian.service;

import hsf302.springboot.webtrunggian.entity.Listing;
import hsf302.springboot.webtrunggian.entity.Order;
import hsf302.springboot.webtrunggian.entity.User;
import hsf302.springboot.webtrunggian.entity.enums.OrderStatus;
import hsf302.springboot.webtrunggian.repository.ListingRepository;
import hsf302.springboot.webtrunggian.repository.OrderRepository;
import hsf302.springboot.webtrunggian.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ListingRepository listingRepository;
    private final UserRepository userRepository;
    private final WalletService walletService;

    // Phí nền tảng 5%
    private static final BigDecimal FEE_RATE = new BigDecimal("0.05");

    // =========================================================
    // BUYER: Đặt hàng
    // =========================================================
    @Transactional
    public Order placeOrder(Integer listingId, Integer buyerId) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy listing: " + listingId));

        if (!"AVAILABLE".equalsIgnoreCase(listing.getStatus())) {
            throw new IllegalStateException("Listing này không còn khả dụng.");
        }

        if (listing.getSeller().getId().equals(buyerId)) {
            throw new IllegalStateException("Bạn không thể mua listing của chính mình.");
        }

        orderRepository.findActiveOrderByListingAndBuyer(listingId, buyerId).ifPresent(o -> {
            throw new IllegalStateException("Bạn đã có đơn hàng đang xử lý cho listing này.");
        });

        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng: " + buyerId));

        // Tính phí theo feePayer
        BigDecimal price = listing.getPrice();
        BigDecimal fee = price.multiply(FEE_RATE).setScale(2, RoundingMode.HALF_UP);
        String feePayer = listing.getFeePayer();

        BigDecimal escrowAmount;
        if ("BUYER".equalsIgnoreCase(feePayer)) {
            escrowAmount = price.add(fee);
        } else if ("SELLER".equalsIgnoreCase(feePayer)) {
            escrowAmount = price;
        } else { // SPLIT
            escrowAmount = price.add(fee.divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP));
        }

        // Tạo order - snapshot giá tại thời điểm đặt
        Order order = new Order();
        order.setListing(listing);
        order.setBuyer(buyer);
        order.setSeller(listing.getSeller());
        order.setPriceSnapshot(price);
        order.setFeeSnapshot(fee);
        order.setFeePayerSnapshot(feePayer);
        order.setEscrowAmount(escrowAmount);
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());

        Order saved = orderRepository.save(order);

        // Khóa tiền escrow từ ví buyer
        walletService.lockEscrow(buyerId, escrowAmount, saved.getId());

        return saved;
    }

    // =========================================================
    // BUYER: Hủy đơn (chỉ khi PENDING)
    // =========================================================
    @Transactional
    public void cancelOrder(Integer orderId, Integer buyerId) {
        Order order = getOrderAndCheckOwner(orderId, buyerId, "buyer");

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Chỉ có thể hủy đơn khi đang ở trạng thái PENDING.");
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        walletService.refundEscrow(buyerId, order.getEscrowAmount(), orderId);
    }

    // =========================================================
    // SELLER: Xác nhận đơn (PENDING → CONFIRMED)
    // =========================================================
    @Transactional
    public void confirmOrder(Integer orderId, Integer sellerId) {
        Order order = getOrderAndCheckOwner(orderId, sellerId, "seller");

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Chỉ có thể xác nhận đơn đang PENDING.");
        }

        order.setStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order);
    }

    // =========================================================
    // SELLER: Từ chối đơn (PENDING → CANCELLED, hoàn tiền buyer)
    // =========================================================
    @Transactional
    public void rejectOrder(Integer orderId, Integer sellerId) {
        Order order = getOrderAndCheckOwner(orderId, sellerId, "seller");

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Chỉ có thể từ chối đơn đang PENDING.");
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        walletService.refundEscrow(order.getBuyer().getId(), order.getEscrowAmount(), orderId);
    }

    // =========================================================
    // SELLER: Giao hàng (CONFIRMED → DELIVERED)
    // =========================================================
    @Transactional
    public void deliverOrder(Integer orderId, Integer sellerId) {
        Order order = getOrderAndCheckOwner(orderId, sellerId, "seller");

        if (order.getStatus() != OrderStatus.CONFIRMED) {
            throw new IllegalStateException("Chỉ có thể giao hàng khi đơn đang CONFIRMED.");
        }

        order.setStatus(OrderStatus.DELIVERED);
        orderRepository.save(order);
    }

    // =========================================================
    // BUYER: Xác nhận nhận hàng → giải ngân escrow (DELIVERED → COMPLETED)
    // =========================================================
    @Transactional
    public void confirmReceipt(Integer orderId, Integer buyerId) {
        Order order = getOrderAndCheckOwner(orderId, buyerId, "buyer");

        if (order.getStatus() != OrderStatus.DELIVERED) {
            throw new IllegalStateException("Chỉ có thể xác nhận nhận hàng khi đơn DELIVERED.");
        }

        // Tính tiền seller thực nhận
        BigDecimal sellerReceives = calcSellerReceives(order);

        // Giải ngân escrow → chuyển cho seller
        walletService.releaseEscrow(
                order.getBuyer().getId(),
                order.getSeller().getId(),
                sellerReceives,
                orderId
        );

        // Hoàn phần dư về cho buyer (nếu có)
        BigDecimal refundToBuyer = order.getEscrowAmount().subtract(sellerReceives);
        if (refundToBuyer.compareTo(BigDecimal.ZERO) > 0) {
            walletService.refundEscrow(buyerId, refundToBuyer, orderId);
        }

        order.setStatus(OrderStatus.COMPLETED);
        order.setCompletedAt(LocalDateTime.now());
        orderRepository.save(order);
    }

    // =========================================================
    // READ
    // =========================================================
    public Page<Order> getBuyerOrders(Integer buyerId, Pageable pageable) {
        return orderRepository.findByBuyerIdOrderByCreatedAtDesc(buyerId, pageable);
    }

    public Page<Order> getSellerOrders(Integer sellerId, Pageable pageable) {
        return orderRepository.findBySellerIdOrderByCreatedAtDesc(sellerId, pageable);
    }

    public Page<Order> getBuyerOrdersByStatus(Integer buyerId, OrderStatus status, Pageable pageable) {
        return orderRepository.findByBuyerIdAndStatusOrderByCreatedAtDesc(buyerId, status, pageable);
    }

    public Page<Order> getSellerOrdersByStatus(Integer sellerId, OrderStatus status, Pageable pageable) {
        return orderRepository.findBySellerIdAndStatusOrderByCreatedAtDesc(sellerId, status, pageable);
    }

    public Order getOrderById(Integer orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng: " + orderId));
    }

    public Order getOrderAndValidateAccess(Integer orderId, Integer userId) {
        Order order = getOrderById(orderId);
        boolean isBuyer  = order.getBuyer().getId().equals(userId);
        boolean isSeller = order.getSeller().getId().equals(userId);
        if (!isBuyer && !isSeller) {
            throw new SecurityException("Bạn không có quyền truy cập đơn hàng này.");
        }
        return order;
    }

    // =========================================================
    // PRIVATE helpers
    // =========================================================
    private Order getOrderAndCheckOwner(Integer orderId, Integer userId, String role) {
        Order order = getOrderById(orderId);
        boolean valid = "buyer".equals(role)
                ? order.getBuyer().getId().equals(userId)
                : order.getSeller().getId().equals(userId);
        if (!valid) {
            throw new SecurityException("Bạn không có quyền thực hiện thao tác này.");
        }
        return order;
    }

    private BigDecimal calcSellerReceives(Order order) {
        String feePayer = order.getFeePayerSnapshot();
        BigDecimal price = order.getPriceSnapshot();
        BigDecimal fee   = order.getFeeSnapshot();

        if ("BUYER".equalsIgnoreCase(feePayer)) {
            return price; // buyer đã trả phí rồi, seller nhận full
        } else if ("SELLER".equalsIgnoreCase(feePayer)) {
            return price.subtract(fee);
        } else { // SPLIT
            return price.subtract(fee.divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP));
        }
    }
}
