package hsf302.springboot.webtrunggian.repository;

import hsf302.springboot.webtrunggian.entity.PasswordReset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordReset, Integer> {

    Optional<PasswordReset> findByToken(String token);

}