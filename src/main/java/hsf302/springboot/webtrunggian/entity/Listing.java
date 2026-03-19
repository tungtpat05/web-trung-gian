package hsf302.springboot.webtrunggian.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "listings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Listing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "seller_id")
    private Integer sellerId; // Sau này có thể map @ManyToOne với User

    private String title;
    private BigDecimal price;

    @Column(name = "hidden_content")
    private String hiddenContent;

    private String visibility; // PUBLIC, PRIVATE

    @Column(name = "fee_payer")
    private String feePayer; // BUYER, SELLER, SPLIT

    private String status; // AVAILABLE, v.v.

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}