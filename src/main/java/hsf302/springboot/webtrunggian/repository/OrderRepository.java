package hsf302.springboot.webtrunggian.repository;

import hsf302.springboot.webtrunggian.entity.Order;
import hsf302.springboot.webtrunggian.entity.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    boolean existsByListingId(Integer listingId);


    // Buyer xem đơn của mình
    Page<Order> findByBuyerIdOrderByCreatedAtDesc(Integer buyerId, Pageable pageable);

    // Seller xem đơn của mình
    Page<Order> findBySellerIdOrderByCreatedAtDesc(Integer sellerId, Pageable pageable);

    // Filter theo status - buyer
    Page<Order> findByBuyerIdAndStatusOrderByCreatedAtDesc(Integer buyerId, OrderStatus status, Pageable pageable);

    // Filter theo status - seller
    Page<Order> findBySellerIdAndStatusOrderByCreatedAtDesc(Integer sellerId, OrderStatus status, Pageable pageable);

    // Kiểm tra buyer đã có đơn active cho listing này chưa (tránh đặt trùng)
    @Query("SELECT o FROM Order o WHERE o.listing.id = :listingId AND o.buyer.id = :buyerId " +
            "AND o.status NOT IN ('COMPLETED', 'CANCELLED')")
    Optional<Order> findActiveOrderByListingAndBuyer(@Param("listingId") Integer listingId,
                                                     @Param("buyerId") Integer buyerId);

}
