package hsf302.springboot.webtrunggian.controller;

import hsf302.springboot.webtrunggian.service.PaymentService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class PaymentWebhookController {

    private PaymentService paymentService;

    @PostMapping("/sepay-webhook")
    public ResponseEntity<String> handleSePay(@RequestBody Map<String, Object> payload) {
        // SePay sent data in JSON format
        System.out.println("Đã nhận dữ liệu từ SePay: " + payload);

        // Process the webhook data
        paymentService.processWebHook(payload);

        return ResponseEntity.ok("Xác nhận đã nhận dữ liệu");
    }
}
