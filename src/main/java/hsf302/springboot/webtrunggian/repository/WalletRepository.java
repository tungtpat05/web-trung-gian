package hsf302.springboot.webtrunggian.repository;

import hsf302.springboot.webtrunggian.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Integer> {
    Optional<Wallet> findByUserId(Integer userId);
}
