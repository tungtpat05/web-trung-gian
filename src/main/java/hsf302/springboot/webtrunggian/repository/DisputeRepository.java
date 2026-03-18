package hsf302.springboot.webtrunggian.repository;

import hsf302.springboot.webtrunggian.entity.Dispute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface DisputeRepository extends JpaRepository<Dispute, Integer> {
    Optional<Dispute> findByOrderId(Integer orderId);
    List<Dispute> findAllByCreatedById(Integer userId);
}
