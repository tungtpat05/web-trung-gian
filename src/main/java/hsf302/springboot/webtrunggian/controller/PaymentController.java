package hsf302.springboot.webtrunggian.controller;

import hsf302.springboot.webtrunggian.entity.User;
import hsf302.springboot.webtrunggian.entity.WalletTransaction;
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

    @GetMapping("payment/withdraw")
    public String withdrawForm() {
        return "payment/withdraw-form";
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

}
