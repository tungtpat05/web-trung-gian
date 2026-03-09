package hsf302.springboot.webtrunggian.controller;

import hsf302.springboot.webtrunggian.entity.WalletTransaction;
import hsf302.springboot.webtrunggian.entity.WithdrawRequest;
import hsf302.springboot.webtrunggian.service.WalletService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
@RequestMapping("/admin/wallet")
@AllArgsConstructor
public class AdminWalletController {

    private WalletService walletService;

    // Show list of withdraw requests of all user
    @GetMapping("/withdraw-requests")
    public String listWithdrawRequest(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            Model model) {

        // 10 bản ghi mỗi trang, sắp xếp mới nhất lên đầu
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());

        // Gọi Service với đầy đủ các tham số lọc
        Page<WithdrawRequest> withdrawRequests = walletService.searchWithDrawRequests(pageable);

        model.addAttribute("withdrawRequests", withdrawRequests);

        return "wallet/admin-withdraw-list";
    }
    
}
