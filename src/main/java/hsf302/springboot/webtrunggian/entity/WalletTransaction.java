package hsf302.springboot.webtrunggian.entity;

import hsf302.springboot.webtrunggian.entity.enums.WalletTransactionReferenceType;
import hsf302.springboot.webtrunggian.entity.enums.WalletTransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "wallet_transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private WalletTransactionType type;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(name = "balance_before", nullable = false, precision = 18, scale = 2)
    private BigDecimal balanceBefore;

    @Column(name = "balance_after", nullable = false, precision = 18, scale = 2)
    private BigDecimal balanceAfter;

    @Enumerated(EnumType.STRING)
    @Column(name = "reference_type", length = 30)
    private WalletTransactionReferenceType referenceType;

    @Column(name = "reference_id")
    private Integer referenceId;

    private String note;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}