package hsf302.springboot.webtrunggian.controller;

import hsf302.springboot.webtrunggian.entity.Listing;
import hsf302.springboot.webtrunggian.entity.User;
import hsf302.springboot.webtrunggian.repository.ListingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Controller
@RequestMapping("/listings")
@RequiredArgsConstructor
public class ListingController {

    private final ListingRepository listingRepository;
    private static final BigDecimal FEE_RATE = new BigDecimal("0.05");

    // Chợ công khai — tất cả listing PUBLIC + AVAILABLE
    @GetMapping
    public String listAll(Model model) {
        List<Listing> listings = listingRepository.findAll()
                .stream()
                .filter(l -> "AVAILABLE".equalsIgnoreCase(l.getStatus())
                        && "PUBLIC".equalsIgnoreCase(l.getVisibility()))
                .toList();
        model.addAttribute("listings", listings);
        return "listing/listing-list";  // thay vì "listing/list"
    }

    // Chi tiết listing — hiển thị trong modal
    @GetMapping("/{listingId}")
    public String detail(@PathVariable Integer listingId,
                         @ModelAttribute("currentUser") User currentUser,
                         Model model) {
        Listing listing = listingRepository.findById(listingId).orElse(null);
        if (listing == null) return "redirect:/listings";

        BigDecimal fee   = listing.getPrice().multiply(FEE_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal buyerPays;
        String feeNote;

        switch (listing.getFeePayer().toUpperCase()) {
            case "BUYER":
                buyerPays = listing.getPrice().add(fee);
                feeNote   = "Phí giao dịch do bạn thanh toán: +" + fee;
                break;
            case "SELLER":
                buyerPays = listing.getPrice();
                feeNote   = "Phí giao dịch do seller chịu";
                break;
            default: // SPLIT
                BigDecimal half = fee.divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
                buyerPays = listing.getPrice().add(half);
                feeNote   = "Phí chia đôi: bạn trả thêm +" + half;
        }

        boolean isOwner = currentUser != null
                && listing.getSeller().getId().equals(currentUser.getId());

        model.addAttribute("listing",   listing);
        model.addAttribute("fee",       fee);
        model.addAttribute("buyerPays", buyerPays);
        model.addAttribute("feeNote",   feeNote);
        model.addAttribute("isOwner",   isOwner);
        return "listing/detail";
    }
}
