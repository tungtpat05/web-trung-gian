package hsf302.springboot.webtrunggian.service;

import hsf302.springboot.webtrunggian.entity.*;
import hsf302.springboot.webtrunggian.entity.enums.DisputeStatus;
import hsf302.springboot.webtrunggian.entity.enums.OrderStatus;
import hsf302.springboot.webtrunggian.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DisputeService {

    private final DisputeRepository disputeRepository;
    private final DisputeMessageRepository disputeMessageRepository;
    private final DisputeEvidenceRepository disputeEvidenceRepository;
    private final OrderRepository orderRepository;
    private final WalletService walletService;

    @Transactional
    public Dispute createDispute(Integer orderId, Integer userId, String reason, String description) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng: " + orderId));

        if (order.getStatus() == OrderStatus.DISPUTED) {
            throw new IllegalStateException("Đơn hàng này đã đang trong quá trình khiếu nại.");
        }

        if (order.getStatus() != OrderStatus.CONFIRMED && order.getStatus() != OrderStatus.DELIVERED) {
            throw new IllegalStateException("Chỉ có thể khiếu nại khi đơn CONFIRMED hoặc DELIVERED.");
        }

        Dispute dispute = new Dispute();
        dispute.setOrder(order);
        
        User creator = new User();
        creator.setId(userId);
        dispute.setCreatedBy(creator);
        
        dispute.setReason(reason);
        dispute.setDescription(description);
        dispute.setStatus(DisputeStatus.PENDING);
        dispute.setCreatedAt(LocalDateTime.now());

        // Update order status
        order.setStatus(OrderStatus.DISPUTED);
        orderRepository.save(order);

        return disputeRepository.save(dispute);
    }

    @Transactional
    public DisputeMessage addMessage(Integer disputeId, Integer senderId, String content) {
        Dispute dispute = disputeRepository.findById(disputeId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khiếu nại: " + disputeId));

        DisputeMessage message = new DisputeMessage();
        message.setDispute(dispute);
        
        User sender = new User();
        sender.setId(senderId);
        message.setSender(sender);
        
        message.setContent(content);
        message.setCreatedAt(LocalDateTime.now());

        if (dispute.getStatus() == DisputeStatus.PENDING) {
            dispute.setStatus(DisputeStatus.UNDER_REVIEW);
            disputeRepository.save(dispute);
        }

        return disputeMessageRepository.save(message);
    }

    @Transactional
    public DisputeEvidence addEvidence(Integer disputeId, Integer userId, String fileUrl, String type) {
        Dispute dispute = disputeRepository.findById(disputeId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khiếu nại: " + disputeId));

        DisputeEvidence evidence = new DisputeEvidence();
        evidence.setDispute(dispute);
        
        User user = new User();
        user.setId(userId);
        evidence.setUploadedBy(user);
        
        evidence.setFileUrl(fileUrl);
        // evidence.setType(EvidenceType.valueOf(type)); // Need to ensure type exists in EvidenceType enum
        evidence.setCreatedAt(LocalDateTime.now());

        return disputeEvidenceRepository.save(evidence);
    }

    @Transactional
    public void resolveDispute(Integer disputeId, Integer adminId, String outcome, String note, BigDecimal splitAmount) {
        Dispute dispute = disputeRepository.findById(disputeId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khiếu nại: " + disputeId));

        Order order = dispute.getOrder();
        User admin = new User();
        admin.setId(adminId);

        dispute.setResolvedBy(admin);
        dispute.setResolvedAt(LocalDateTime.now());
        dispute.setResolutionNote(note);

        switch (outcome) {
            case "REFUND_BUYER":
                walletService.refundEscrow(order.getBuyer().getId(), order.getEscrowAmount(), order.getId());
                dispute.setStatus(DisputeStatus.REFUNDED);
                order.setStatus(OrderStatus.CANCELLED);
                break;
            case "PAY_SELLER":
                walletService.releaseEscrow(order.getBuyer().getId(), order.getSeller().getId(), order.getEscrowAmount(), order.getId());
                dispute.setStatus(DisputeStatus.RESOLVED);
                order.setStatus(OrderStatus.COMPLETED);
                order.setCompletedAt(LocalDateTime.now());
                break;
            case "SPLIT":
                if (splitAmount == null || splitAmount.compareTo(BigDecimal.ZERO) < 0 || splitAmount.compareTo(order.getEscrowAmount()) > 0) {
                    throw new IllegalArgumentException("Số tiền chia không hợp lệ.");
                }
                // splitAmount to Buyer, rest to Seller
                walletService.refundEscrow(order.getBuyer().getId(), splitAmount, order.getId());
                BigDecimal toSeller = order.getEscrowAmount().subtract(splitAmount);
                if (toSeller.compareTo(BigDecimal.ZERO) > 0) {
                    walletService.releaseEscrow(order.getBuyer().getId(), order.getSeller().getId(), toSeller, order.getId());
                }
                dispute.setStatus(DisputeStatus.RESOLVED);
                order.setStatus(OrderStatus.COMPLETED);
                order.setCompletedAt(LocalDateTime.now());
                break;
            case "CANCEL_ORDER":
                // Similar to refund if money was locked
                walletService.refundEscrow(order.getBuyer().getId(), order.getEscrowAmount(), order.getId());
                dispute.setStatus(DisputeStatus.CLOSED);
                order.setStatus(OrderStatus.CANCELLED);
                break;
            default:
                throw new IllegalArgumentException("Outcome không hợp lệ: " + outcome);
        }

        disputeRepository.save(dispute);
        orderRepository.save(order);
    }

    public List<Dispute> getDisputesByUser(Integer userId) {
        // Find disputes created by user or where user is buyer/seller of the order
        // For simplicity, let's just find by createdBy for now or query by order
        return disputeRepository.findAllByCreatedById(userId);
    }

    public List<Dispute> getAllDisputes() {
        return disputeRepository.findAll();
    }

    public Dispute getDisputeById(Integer id) {
        return disputeRepository.findById(id).orElse(null);
    }

    public List<DisputeMessage> getMessages(Integer disputeId) {
        return disputeMessageRepository.findByDisputeIdOrderByCreatedAtAsc(disputeId);
    }

    public List<DisputeEvidence> getEvidences(Integer disputeId) {
        return disputeEvidenceRepository.findByDisputeId(disputeId);
    }
}
