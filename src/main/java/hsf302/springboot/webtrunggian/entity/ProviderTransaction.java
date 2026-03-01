package hsf302.springboot.webtrunggian.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "provider_transactions",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"provider", "provider_transaction_id"})})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProviderTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_request_id", nullable = false)
    private PaymentRequest paymentRequest;

    @Column(length = 30)
    private String provider;

    @Column(name = "provider_transaction_id", length = 100)
    private String providerTransactionId;

    @Column(name = "amount_paid", precision = 18, scale = 2)
    private BigDecimal amountPaid;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "raw_data", columnDefinition = "NVARCHAR(MAX)")
    private String rawData;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
