package hsf302.springboot.webtrunggian.repository;

import hsf302.springboot.webtrunggian.entity.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Integer>, JpaSpecificationExecutor<WalletTransaction> {
}
