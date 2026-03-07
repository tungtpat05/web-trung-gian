package hsf302.springboot.webtrunggian.controller;

import hsf302.springboot.webtrunggian.entity.User;
import hsf302.springboot.webtrunggian.entity.WalletTransaction;
import hsf302.springboot.webtrunggian.entity.WithdrawRequest;
import hsf302.springboot.webtrunggian.service.PaymentService;
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
import java.util.Map;

@Controller
@RequestMapping
@AllArgsConstructor
public class PaymentController {

    private PaymentService paymentService;
    private UserService userService;

    @GetMapping("payment/deposit")
    public String depositForm() {
        return "payment/deposit-form";
    }

    @GetMapping("payment/deposit/qr")
    public String depositQr(
            @RequestParam("internalCode") String internalCode,
            @RequestParam("amount") BigDecimal amount,
            Model model
    ) {
        model.addAttribute("internalCode", internalCode);
        model.addAttribute("amount", amount);
        return "payment/deposit-qr";
    }

    // User submits the deposit form, we redirect to the QR code page
    @PostMapping("payment/deposit")
    public String createPaymentRequest(@RequestParam("amount") String amount, @ModelAttribute("user") User currentUser, RedirectAttributes redirectAttributes) {
        BigDecimal depositAmount = new BigDecimal(amount);
        String internalCode = paymentService.createDepositRequest(currentUser.getId(), depositAmount);

        redirectAttributes.addAttribute("internalCode", internalCode);
        redirectAttributes.addAttribute("amount", depositAmount);

        return "redirect:/payment/deposit/qr";
    }


    @GetMapping("payment/history")
    public String listTransactions(
            @RequestParam(value = "id", required = false) Integer id,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "startDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            Model model) {

        // 10 bản ghi mỗi trang, sắp xếp mới nhất lên đầu
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());

        // Gọi Service với đầy đủ các tham số lọc
        Page<WalletTransaction> transactionPage = paymentService.searchTransactions(id, type, startDate, endDate, pageable);

        model.addAttribute("transactionPage", transactionPage);

        return "payment/transaction-history";
    }

    // Show form to create new withdraw request
    @GetMapping("payment/withdraw-requests/new")
    public String withdrawForm() {
        return "payment/withdraw-form";
    }

    // Create new withdraw request
    @PostMapping("payment/withdraw-requests")
    public String createWithdrawRequest(
            @RequestParam("amount") String amount,
            @RequestParam("bank-name") String bankName,
            @RequestParam("bank-acc") String bankAcc,
            @ModelAttribute("user") User currentUser,
            RedirectAttributes redirectAttributes
    ) {
        BigDecimal withdrawAmount = new BigDecimal(amount);

        try {
            // Create Withdraw Request
            paymentService.createWithdrawRequest(currentUser.getId(), withdrawAmount, bankName, bankAcc);
            redirectAttributes.addFlashAttribute("successMessage", "Withdraw request created successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/payment/withdraw-requests/new";
    }

    // Show list of withdraw requests of current user
    @GetMapping("payment/withdraw-requests")
    public String listWithdrawRequest(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            Model model) {

        // 10 bản ghi mỗi trang, sắp xếp mới nhất lên đầu
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());

        // Gọi Service với đầy đủ các tham số lọc
        Page<WithdrawRequest> withdrawRequests = paymentService.searchWithDrawRequests(pageable);

        model.addAttribute("withdrawRequests", withdrawRequests);

        return "payment/withdraw-list";
    }

    // User cancels a withdraw request
    @PostMapping("payment/withdraw-requests/{withdrawRequestId}/cancel")
    public String cancelWithdrawRequest(
            @PathVariable Integer withdrawRequestId,
            @ModelAttribute("user") User currentUser,
            RedirectAttributes redirectAttributes
    ) {
        try {
            User user = userService.findByUsername(currentUser.getUsername());
            paymentService.cancelWithdrawRequest(withdrawRequestId, user.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Withdraw request cancelled successfully!");
            System.out.println("User " + currentUser.getId() + " cancelled withdraw request " + withdrawRequestId);
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            System.out.println("User " + currentUser.getId() + " failed to cancel withdraw request " + withdrawRequestId + ": " + e.getMessage());
        }
        return "redirect:/payment/withdraw-requests";
    }
}
