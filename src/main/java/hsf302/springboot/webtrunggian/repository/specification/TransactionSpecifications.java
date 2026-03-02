package hsf302.springboot.webtrunggian.repository.specification;

import hsf302.springboot.webtrunggian.entity.WalletTransaction;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class TransactionSpecifications {

    public static Specification<WalletTransaction> hasId(Integer id) {
        return (root, query, cb) -> id == null ? null : cb.equal(root.get("id"), id);
    }

    // Tự động handle Enum: JPA sẽ tự convert String type sang Enum tương ứng trong Entity
    public static Specification<WalletTransaction> hasType(String type) {
        return (root, query, cb) -> (type == null || type.isEmpty()) ? null : cb.equal(root.get("type"), type);
    }

    public static Specification<WalletTransaction> createdBetween(LocalDate startDate, LocalDate endDate) {
        return (root, query, cb) -> {
            if (startDate == null && endDate == null) return null;

            if (startDate != null && endDate != null) {
                return cb.between(root.get("createdAt"),
                        startDate.atStartOfDay(),
                        endDate.atTime(LocalTime.MAX));
            }

            if (startDate != null) {
                return cb.greaterThanOrEqualTo(root.get("createdAt"), startDate.atStartOfDay());
            }

            return cb.lessThanOrEqualTo(root.get("createdAt"), endDate.atTime(LocalTime.MAX));
        };
    }
}