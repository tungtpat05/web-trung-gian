package hsf302.springboot.webtrunggian.service;

import hsf302.springboot.webtrunggian.entity.*;
import hsf302.springboot.webtrunggian.entity.enums.*;
import hsf302.springboot.webtrunggian.repository.*;
import hsf302.springboot.webtrunggian.repository.specification.TransactionSpecifications;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@AllArgsConstructor
public class WalletService {

    private PaymentRequestRepository paymentRequestRepository;
    private ProviderTransactionRepository providerTransactionRepository;
    private WalletRepository walletRepository;
    private WalletTransactionRepository walletTransactionRepository;
    private UserRepository userRepository;
    private WithdrawRequestRepository withdrawRequestRepository;


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

    private String extractDepositCode(String content) {
        Pattern pattern = Pattern.compile("NAP\\d+");
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    private String extractWithdrawCode(String content) {
        Pattern pattern = Pattern.compile("RUT\\d+");
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    @Transactional
    public void processWebHookForDeposit(Map<String, Object> payload) {
        // Get data form payload
        String content = (String) payload.get("content");
        System.out.println("Received webhook with content: " + content);
        String internalCode = extractDepositCode(content);
        System.out.println("Processing deposit webhook with internalCode: " + internalCode);

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
        walletTransaction.setType(WalletTransactionType.PAYMENT);
        walletTransaction.setAmount(amountPaid);
        walletTransaction.setBalanceBefore(oldBalance);
        walletTransaction.setBalanceAfter(wallet.getBalance());
        walletTransaction.setReferenceType(WalletTransactionReferenceType.PAYMENT);
        walletTransaction.setReferenceId(paymentRequest.getId());
        walletTransactionRepository.save(walletTransaction);

        // Complete payment request
        paymentRequest.setStatus(PaymentRequestStatus.COMPLETED);
        paymentRequest.setCompletedAt(LocalDateTime.now());
        System.out.println("Payment request with internalCode: " + internalCode + " is completed. Amount paid: " + amountPaid);
        paymentRequestRepository.save(paymentRequest);
    }

    public Page<WalletTransaction> searchTransactions(Integer id, String type, LocalDate start, LocalDate end, Pageable pageable, User currentUser) {
        Specification<WalletTransaction> spec = Specification.where(TransactionSpecifications.hasId(id))
                .and(TransactionSpecifications.hasType(type))
                .and(TransactionSpecifications.createdBetween(start, end));

        if (currentUser.getRole() == UserRole.USER) {
            spec = spec.and(TransactionSpecifications.hasUserId(currentUser.getId()));
        }
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

        // Create UNIQUE internal_code: RUT + timestamp
        String internalCode = "RUT" + System.currentTimeMillis();

        User user = new User();
        user.setId(userId);
        withdrawRequest.setUser(user);
        withdrawRequest.setAmount(withdrawAmount);
        withdrawRequest.setInternalCode(internalCode);
        withdrawRequest.setBankName(bankName);
        withdrawRequest.setBankAcc(bankAcc);
        withdrawRequest.setStatus(WithdrawRequestStatus.PENDING);
        // Save to DB
        withdrawRequestRepository.save(withdrawRequest);

        wallet.setBalance(wallet.getBalance().subtract(withdrawAmount));
        wallet.setLockedBalance(wallet.getBalance().add(withdrawAmount));
        walletRepository.save(wallet);
    }

    public Page<WithdrawRequest> searchWithDrawRequests(Pageable pageable) {
        return withdrawRequestRepository.findAll(pageable);
    }

    public Page<WithdrawRequest> searchWithDrawRequestsOfCurrentUser(Integer userId, Pageable pageable) {
        return withdrawRequestRepository.findByUserId(userId, pageable);
    }

    @Transactional
    public void cancelWithdrawRequest(Integer withdrawRequestId, Integer userId) {
        WithdrawRequest withdrawRequest = withdrawRequestRepository.findById(withdrawRequestId).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy yêu cầu rút tiền: " + withdrawRequestId));
        if (!withdrawRequest.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Bạn không có quyền hủy yêu cầu rút tiền này");
        }
        if (!withdrawRequest.getStatus().equals(WithdrawRequestStatus.PENDING)) {
            throw new IllegalArgumentException("Chỉ có thể hủy yêu cầu rút tiền đang ở trạng thái PENDING");
        }

        withdrawRequest.setStatus(WithdrawRequestStatus.CANCELED);
        withdrawRequestRepository.save(withdrawRequest);

        // Unlock balance
        Wallet wallet = walletRepository.findByUserId(userId).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy ví của người dùng: " + userId));
        wallet.setLockedBalance(wallet.getLockedBalance().subtract(withdrawRequest.getAmount()));
        wallet.setBalance(wallet.getBalance().add(withdrawRequest.getAmount()));
        walletRepository.save(wallet);
    }

    // Process Withdraw Webhook
    @Transactional
    public void processWebHookForWithdraw(Map<String, Object> payload) {
        // Get data form payload
        String content = (String) payload.get("content");
        System.out.println("Received withdraw webhook with content: " + content);
        String internalCode = extractWithdrawCode(content);
        System.out.println("Processing withdraw webhook with internalCode: " + internalCode);

        // Find request in withdraw_requests with internal_code = internalCode
        WithdrawRequest withdrawRequest = withdrawRequestRepository.findByInternalCode(internalCode).orElse(null);
        if (withdrawRequest == null) {
            System.out.println("Not found withdraw request with internalCode: " + internalCode);
            return;
        }

        if (!withdrawRequest.getStatus().equals(WithdrawRequestStatus.PENDING)) {
            System.out.println("Withdraw request with internalCode: " + internalCode + " is not pending. Current status: " + withdrawRequest.getStatus());
            return;
        }

        // Update Wallet balance
        Wallet wallet = walletRepository.findByUserId(withdrawRequest.getUser().getId()).orElse(null);
        BigDecimal transferAmount = new BigDecimal(payload.get("transferAmount").toString());

        wallet.setLockedBalance(wallet.getLockedBalance().subtract(transferAmount));
        walletRepository.save(wallet);

        // Log of Balance change (wallet_transactions)
        WalletTransaction walletTransaction = new WalletTransaction();
        walletTransaction.setWallet(wallet);
        walletTransaction.setType(WalletTransactionType.WITHDRAW);
        walletTransaction.setAmount(transferAmount);
        walletTransaction.setBalanceBefore(wallet.getBalance().add(transferAmount));
        walletTransaction.setBalanceAfter(wallet.getBalance());
        walletTransaction.setReferenceType(WalletTransactionReferenceType.WITHDRAW);
        walletTransaction.setReferenceId(withdrawRequest.getId());
        walletTransactionRepository.save(walletTransaction);

        // Complete withdraw request
        withdrawRequest.setStatus(WithdrawRequestStatus.COMPLETED);
        withdrawRequest.setUpdatedAt(LocalDateTime.now());
        System.out.println("Withdraw request with internalCode: " + internalCode + " is completed. Amount paid: " + transferAmount);
        withdrawRequestRepository.save(withdrawRequest);
    }
}
