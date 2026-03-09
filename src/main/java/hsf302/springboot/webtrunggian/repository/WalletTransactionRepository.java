package hsf302.springboot.webtrunggian.repository;

import hsf302.springboot.webtrunggian.entity.WalletTransaction;
import hsf302.springboot.webtrunggian.repository.specification.TransactionSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Integer>, JpaSpecificationExecutor<WalletTransaction> {
}
