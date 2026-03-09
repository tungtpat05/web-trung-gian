package hsf302.springboot.webtrunggian.repository;

import hsf302.springboot.webtrunggian.entity.WithdrawRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WithdrawRequestRepository extends JpaRepository<WithdrawRequest, Integer> {
    Optional<WithdrawRequest> findByInternalCode(String internalCode);
    Page<WithdrawRequest> findByUserId(Integer userId, Pageable pageable);
}
