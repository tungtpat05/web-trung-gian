package hsf302.springboot.webtrunggian.dto;

import hsf302.springboot.webtrunggian.entity.Listing;
import hsf302.springboot.webtrunggian.entity.User;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * View model cho trang "Đơn bán của tôi".
 * Khi seller chưa có order cho listing, ta tạo row placeholder từ Listing.
 */
@Data
public class MySalesRow {
    private boolean listingPlaceholder;

    // Dùng chung cho action link (orderId hoặc listingId cho placeholder)
    private Integer id;

    // JSP đang so sánh status theo string như: 'PENDING', 'CONFIRMED'...
    private String status;

    private User buyer;
    private User seller;

    private Listing listing;

    private BigDecimal priceSnapshot;
    private BigDecimal feeSnapshot;
    private String feePayerSnapshot;
    private BigDecimal escrowAmount;

    private LocalDateTime createdAt;
}

