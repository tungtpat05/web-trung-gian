package hsf302.springboot.webtrunggian.controller;

import hsf302.springboot.webtrunggian.entity.User;
import hsf302.springboot.webtrunggian.service.PaymentService;
import hsf302.springboot.webtrunggian.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
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
}
