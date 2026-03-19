package hsf302.springboot.webtrunggian.service;

import hsf302.springboot.webtrunggian.entity.Listing;
import hsf302.springboot.webtrunggian.repository.ListingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListingService {
    @Autowired
    private ListingRepository listingRepository;

    public List<Listing> getAllPublicListings() {
        return listingRepository.findByVisibility("PUBLIC");
    }

    public void saveListing(Listing listing) {
        listingRepository.save(listing);
    }
}