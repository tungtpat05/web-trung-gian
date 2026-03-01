package hsf302.springboot.webtrunggian.repository;

import hsf302.springboot.webtrunggian.entity.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Integer> {
}
