package hsf302.springboot.webtrunggian.service;

import hsf302.springboot.webtrunggian.entity.*;
import hsf302.springboot.webtrunggian.entity.enums.PaymentRequestStatus;
import hsf302.springboot.webtrunggian.entity.enums.WalletTransactionReferenceType;
import hsf302.springboot.webtrunggian.entity.enums.WalletTransactionType;
import hsf302.springboot.webtrunggian.entity.enums.WithdrawRequestStatus;
import hsf302.springboot.webtrunggian.repository.*;
import hsf302.springboot.webtrunggian.repository.specification.TransactionSpecifications;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.SQLOutput;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class PaymentService {

    private PaymentRequestRepository paymentRequestRepository;
    private ProviderTransactionRepository providerTransactionRepository;
    private WalletRepository walletRepository;
    private WalletTransactionRepository walletTransactionRepository;
    private UserRepository userRepository;
    private final WithdrawRequestRepository withdrawRequestRepository;


    @Transactional
    public String createDepositRequest(Integer userId, BigDecimal amount) {
        // 1. Create UNIQUE internal_code: NAP + timestamp
        String internalCode = "NAP" + System.currentTimeMillis();

        // 2. Save to payment_requests table with status "PENDING"
        PaymentRequest request = new PaymentRequest();

        User user = new User();
        user.setId(userId);

        request.setUser(user);
        request.setAmountExpected(amount);
        request.setInternalCode(internalCode);
        request.setStatus(PaymentRequestStatus.PENDING);
        paymentRequestRepository.save(request);

        return internalCode;
    }

    @Transactional
    public void processWebHook(Map<String, Object> payload) {
        // Get data form payload
        String content = (String) payload.get("content");
        String internalCode = content.substring(content.indexOf("NAP"));

        // Find request in payment_requests with internal_code = internalCode
        PaymentRequest paymentRequest = paymentRequestRepository.findByInternalCode(internalCode).orElse(null);
        if (paymentRequest == null) {
            System.out.println("Not found payment request with internalCode: " + internalCode);
            return;
        }

        if (!paymentRequest.getStatus().equals(PaymentRequestStatus.PENDING)) {
            System.out.println("Payment request with internalCode: " + internalCode + " is not pending. Current status: " + paymentRequest.getStatus());
            return;
        }

        // Save to the provider_transactions table (For transparency and prevent duplicate)
        ProviderTransaction providerTransaction = new ProviderTransaction();
        providerTransaction.setPaymentRequest(paymentRequest);
        providerTransaction.setProvider("SEPAY");
        providerTransaction.setProviderTransactionId(payload.get("id").toString());
        providerTransaction.setAmountPaid(new BigDecimal(payload.get("transferAmount").toString()));

        // 2026-03-01 15:46:00 (Payload) not match LocalDateTime Format (2026-03-01T15:46:00)
        // Convert
        String transactionDate = payload.get("transactionDate")
                .toString()
                .replace(" ", "T");
        providerTransaction.setPaidAt(LocalDateTime.parse(transactionDate));

        providerTransaction.setRawData(payload.toString());
        providerTransactionRepository.save(providerTransaction);

        // Update Wallet balance
        Wallet wallet = walletRepository.findByUserId(paymentRequest.getUser().getId()).orElse(null);
        BigDecimal amountPaid = providerTransaction.getAmountPaid();

        BigDecimal oldBalance = wallet.getBalance();
        wallet.setBalance(oldBalance.add(amountPaid));
        walletRepository.save(wallet);

        // Log of Balance change (wallet_transactions)
        WalletTransaction walletTransaction = new WalletTransaction();
        walletTransaction.setWallet(wallet);
        walletTransaction.setType(WalletTransactionType.TOP_UP);
        walletTransaction.setAmount(amountPaid);
        walletTransaction.setBalanceBefore(oldBalance);
        walletTransaction.setBalanceAfter(wallet.getBalance());
        walletTransaction.setReferenceType(WalletTransactionReferenceType.PAYMENT);
        walletTransaction.setReferenceId(paymentRequest.getId());
        walletTransactionRepository.save(walletTransaction);

        // Complete payment request
        paymentRequest.setStatus(PaymentRequestStatus.SUCCESS);
        paymentRequest.setCompletedAt(LocalDateTime.now());
        System.out.println("Payment request with internalCode: " + internalCode + " is completed. Amount paid: " + amountPaid);
        paymentRequestRepository.save(paymentRequest);
    }

    public Page<WalletTransaction> searchTransactions(Integer id, String type, LocalDate start, LocalDate end, Pageable pageable) {
        Specification<WalletTransaction> spec = Specification.where(TransactionSpecifications.hasId(id))
                .and(TransactionSpecifications.hasType(type))
                .and(TransactionSpecifications.createdBetween(start, end));

        return walletTransactionRepository.findAll(spec, pageable);
    }

    public void createWithdrawRequest(Integer userId, BigDecimal withdrawAmount, String bankName, String bankAcc) {
        if (withdrawAmount == null || withdrawAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Số tiền rút phải lớn hơn 0");
        }

        // Get balance of current user
        Wallet wallet = walletRepository.findByUserId(userId).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy ví của người dùng: " + userId));
        if (wallet.getBalance().compareTo(withdrawAmount) < 0) {
            throw new IllegalArgumentException("Số dư không đủ. Số dư hiện tại: " + wallet.getBalance());
        }

        // Create request
        WithdrawRequest withdrawRequest = new WithdrawRequest();
        User user = new User();
        user.setId(userId);
        withdrawRequest.setUser(user);
        withdrawRequest.setAmount(withdrawAmount);
        withdrawRequest.setBankName(bankName);
        withdrawRequest.setBankAcc(bankAcc);
        withdrawRequest.setStatus(WithdrawRequestStatus.PENDING);
        // Save to DB
        withdrawRequestRepository.save(withdrawRequest);
    }
}
