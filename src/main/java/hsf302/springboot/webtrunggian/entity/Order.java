package hsf302.springboot.webtrunggian.entity;

import hsf302.springboot.webtrunggian.entity.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "listing_id")
    private Listing listing;

    @ManyToOne
    @JoinColumn(name = "buyer_id")
    private User buyer;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private User seller;

    @Column(name = "price_snapshot")
    private BigDecimal priceSnapshot;

    @Column(name = "fee_snapshot")
    private BigDecimal feeSnapshot;

    @Column(name = "fee_payer_snapshot")
    private String feePayerSnapshot;

    @Column(name = "escrow_amount")
    private BigDecimal escrowAmount;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(name = "escrow_release_at")
    private LocalDateTime escrowReleaseAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "completed_at")
    private LocalDateTime completedAt;
}
