package hsf302.springboot.webtrunggian.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "listings")
@Data
@NoArgsConstructor
public class Listing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private User seller;

    private String title;
    private BigDecimal price;

    @Column(name = "hidden_content", columnDefinition = "NVARCHAR(MAX)")
    private String hiddenContent;

    private String visibility; // PUBLIC, PRIVATE
    
    @Column(name = "fee_payer")
    private String feePayer; // BUYER, SELLER, SPLIT

    private String status; // AVAILABLE, etc.

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
