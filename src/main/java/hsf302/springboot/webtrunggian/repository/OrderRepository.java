package hsf302.springboot.webtrunggian.repository;

import hsf302.springboot.webtrunggian.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
}
