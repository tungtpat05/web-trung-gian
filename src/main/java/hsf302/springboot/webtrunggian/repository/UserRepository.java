package hsf302.springboot.webtrunggian.repository;

import hsf302.springboot.webtrunggian.entity.MyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<MyUser, Long> {
    Optional<MyUser> findByEmail(String email);
    Optional<MyUser> findByUsername(String username);
}
