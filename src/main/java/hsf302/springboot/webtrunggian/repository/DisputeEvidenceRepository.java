package hsf302.springboot.webtrunggian.repository;

import hsf302.springboot.webtrunggian.entity.DisputeEvidence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DisputeEvidenceRepository extends JpaRepository<DisputeEvidence, Integer> {
    List<DisputeEvidence> findByDisputeId(Integer disputeId);
}
