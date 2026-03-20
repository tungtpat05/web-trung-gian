package hsf302.springboot.webtrunggian.entity.enums;

public enum OrderStatus {
    PENDING,      // Buyer vừa đặt, chờ seller xác nhận
    CONFIRMED,    // Seller xác nhận, đang thực hiện
    DELIVERED,    // Seller đã giao, chờ buyer confirm
    COMPLETED,    // Hoàn thành, escrow released
    DISPUTED,     // Đang khiếu nại (P5 dùng)
    CANCELLED,    // Đã hủy
    PROCESSING    // Giữ lại tương thích
}


