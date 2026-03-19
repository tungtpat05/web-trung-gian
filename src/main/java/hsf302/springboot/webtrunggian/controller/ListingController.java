package hsf302.springboot.webtrunggian.controller;

import hsf302.springboot.webtrunggian.service.ListingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ListingController {
    @Autowired
    private ListingService listingService;

    @GetMapping("/listings")
    public String viewListings(Model model) {
        model.addAttribute("listItems", listingService.getAllPublicListings());
        return "listing-list"; // Tên file .jsp của bạn
    }
}