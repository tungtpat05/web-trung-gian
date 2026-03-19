package hsf302.springboot.webtrunggian.repository;

import hsf302.springboot.webtrunggian.entity.ProviderTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProviderTransactionRepository extends JpaRepository<ProviderTransaction, Integer> {
}
