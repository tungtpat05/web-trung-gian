package hsf302.springboot.webtrunggian.controller;

import hsf302.springboot.webtrunggian.service.WalletService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class WalletWebhookController {

    private WalletService walletService;

    @PostMapping("/sepay-webhook")
    public ResponseEntity<String> handleSePay(@RequestBody Map<String, Object> payload) {
        // SePay sent data in JSON format
        System.out.println("Đã nhận dữ liệu từ SePay: " + payload);

        // Check transferType
        String transferType = (String) payload.get("transferType");

        // Process the webhook data
        if(transferType.equals("in")) {
            System.out.println("Xử lý webhook cho giao dịch NẠP tiền");
            walletService.processWebHookForDeposit(payload);
        } else if(transferType.equals("out")) {
            System.out.println("Xử lý webhook cho giao dịch RÚT tiền");
            walletService.processWebHookForWithdraw(payload);
        }

        return ResponseEntity.ok("Xác nhận đã nhận dữ liệu");
    }
}
