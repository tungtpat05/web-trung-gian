package hsf302.springboot.webtrunggian.controller;

import hsf302.springboot.webtrunggian.entity.User;
import hsf302.springboot.webtrunggian.entity.WalletTransaction;
import hsf302.springboot.webtrunggian.entity.WithdrawRequest;
import hsf302.springboot.webtrunggian.service.WalletService;
import hsf302.springboot.webtrunggian.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;

@Controller
@RequestMapping("/wallet")
@AllArgsConstructor
public class WalletController {

    private WalletService walletService;
    private UserService userService;

    @GetMapping("/payment")
    public String depositForm() {
        return "wallet/payment-form";
    }

    @GetMapping("/payment/qr")
    public String depositQr(
            @RequestParam("internalCode") String internalCode,
            @RequestParam("amount") BigDecimal amount,
            Model model
    ) {
        model.addAttribute("internalCode", internalCode);
        model.addAttribute("amount", amount);
        return "wallet/payment-qr";
    }

    // User submits the deposit form, we redirect to the QR code page
    @PostMapping("/payment")
    public String createPaymentRequest(@RequestParam("amount") String amount, @ModelAttribute("currentUser") User currentUser, RedirectAttributes redirectAttributes) {
        BigDecimal depositAmount = new BigDecimal(amount);
        String internalCode = walletService.createDepositRequest(currentUser.getId(), depositAmount);

        redirectAttributes.addAttribute("internalCode", internalCode);
        redirectAttributes.addAttribute("amount", depositAmount);

        return "redirect:/wallet/payment/qr";
    }


    // Service check User Role -> if Admin, show all transactions, else show only transactions of current user
    @GetMapping("/stransactions")
    public String listTransactions(
            @RequestParam(value = "id", required = false) Integer id,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "startDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @ModelAttribute("currentUser") User currentUser,
            Model model) {

        // 10 bản ghi mỗi trang, sắp xếp mới nhất lên đầu
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());

        // Gọi Service với đầy đủ các tham số lọc
        Page<WalletTransaction> transactionPage = walletService.searchTransactions(id, type, startDate, endDate, pageable, currentUser);

        model.addAttribute("transactionPage", transactionPage);

        return "wallet/transaction-history";
    }

    // Show form to create new withdraw request
    @GetMapping("/withdraw-requests/new")
    public String withdrawForm() {
        return "wallet/withdraw-form";
    }

    // Create new withdraw request
    @PostMapping("/withdraw-requests")
    public String createWithdrawRequest(
            @RequestParam("amount") String amount,
            @RequestParam("bank-name") String bankName,
            @RequestParam("bank-acc") String bankAcc,
            @ModelAttribute("currentUser") User currentUser,
            RedirectAttributes redirectAttributes
    ) {
        BigDecimal withdrawAmount = new BigDecimal(amount);

        try {
            // Create Withdraw Request
            walletService.createWithdrawRequest(currentUser.getId(), withdrawAmount, bankName, bankAcc);
            redirectAttributes.addFlashAttribute("successMessage", "Tạo yêu cầu rút tiền thành công!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/wallet/withdraw-requests/new";
    }

    // Show list of withdraw requests of current user
    @GetMapping("/withdraw-requests")
    public String listWithdrawRequestOfCurrentUser(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @ModelAttribute("currentUser") User currentUser,
            Model model) {

        // 10 bản ghi mỗi trang, sắp xếp mới nhất lên đầu
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());

        // Gọi Service với đầy đủ các tham số lọc
        Page<WithdrawRequest> withdrawRequests = walletService.searchWithDrawRequestsOfCurrentUser(currentUser.getId(), pageable);

        model.addAttribute("withdrawRequests", withdrawRequests);

        return "wallet/withdraw-list";
    }

    // User cancels a withdraw request
    @PostMapping("/withdraw-requests/{withdrawRequestId}/cancel")
    public String cancelWithdrawRequest(
            @PathVariable Integer withdrawRequestId,
            @ModelAttribute("currentUser") User currentUser,
            RedirectAttributes redirectAttributes
    ) {
        try {
            User user = userService.findByUsername(currentUser.getUsername());
            walletService.cancelWithdrawRequest(withdrawRequestId, user.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Withdraw request cancelled successfully!");
            System.out.println("User " + currentUser.getId() + " cancelled withdraw request " + withdrawRequestId);
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            System.out.println("User " + currentUser.getId() + " failed to cancel withdraw request " + withdrawRequestId + ": " + e.getMessage());
        }
        return "redirect:/wallet/withdraw-requests";
    }
}
