package hsf302.springboot.webtrunggian.repository;

import hsf302.springboot.webtrunggian.entity.Listing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ListingRepository extends JpaRepository<Listing, Integer> {
    // Tìm các bài đăng đang hiển thị công khai
    List<Listing> findByVisibility(String visibility);
}