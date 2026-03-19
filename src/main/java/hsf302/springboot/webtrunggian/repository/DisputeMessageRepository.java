package hsf302.springboot.webtrunggian.repository;

import hsf302.springboot.webtrunggian.entity.DisputeMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DisputeMessageRepository extends JpaRepository<DisputeMessage, Integer> {
    List<DisputeMessage> findByDisputeIdOrderByCreatedAtAsc(Integer disputeId);
}
