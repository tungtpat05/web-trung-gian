package hsf302.springboot.webtrunggian.repository;

import hsf302.springboot.webtrunggian.entity.PaymentRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRequestRepository extends JpaRepository<PaymentRequest, Integer> {
    Optional<PaymentRequest> findByInternalCode(String internalCode);
}
