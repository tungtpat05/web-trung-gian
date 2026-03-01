package hsf302.springboot.webtrunggian.entity;

import hsf302.springboot.webtrunggian.entity.enums.PaymentRequestStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "amount_expected", nullable = false, precision = 18, scale = 2)
    private BigDecimal amountExpected;

    @Column(name = "internal_code", unique = true, length = 50)
    private String internalCode;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private PaymentRequestStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "completed_at")
    private LocalDateTime completedAt;
}
