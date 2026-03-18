package hsf302.springboot.webtrunggian.controller;

import hsf302.springboot.webtrunggian.entity.Dispute;
import hsf302.springboot.webtrunggian.entity.DisputeMessage;
import hsf302.springboot.webtrunggian.entity.User;
import hsf302.springboot.webtrunggian.service.DisputeService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/dispute")
@RequiredArgsConstructor
public class DisputeController {

    private final DisputeService disputeService;

    @GetMapping("/my-disputes")
    public String myDisputes(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) return "redirect:/auth/login";
        
        List<Dispute> disputes = disputeService.getDisputesByUser(currentUser.getId());
        model.addAttribute("disputes", disputes);
        return "dispute/list";
    }

    @GetMapping("/create/{orderId}")
    public String showCreateForm(@PathVariable Integer orderId, Model model) {
        model.addAttribute("orderId", orderId);
        return "dispute/create";
    }

    @PostMapping("/create")
    public String createDispute(@RequestParam Integer orderId,
                                @RequestParam String reason,
                                @RequestParam String description,
                                HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) return "redirect:/auth/login";

        Dispute dispute = disputeService.createDispute(orderId, currentUser.getId(), reason, description);
        return "redirect:/dispute/detail/" + dispute.getId();
    }

    @GetMapping("/detail/{id}")
    public String showDetail(@PathVariable Integer id, Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) return "redirect:/auth/login";

        Dispute dispute = disputeService.getDisputeById(id);
        if (dispute == null) return "redirect:/home";

        // Security check: only buyer or seller can view
        if (!dispute.getOrder().getBuyer().getId().equals(currentUser.getId()) &&
            !dispute.getOrder().getSeller().getId().equals(currentUser.getId())) {
            return "redirect:/home";
        }

        List<DisputeMessage> messages = disputeService.getMessages(id);
        model.addAttribute("dispute", dispute);
        model.addAttribute("messages", messages);
        model.addAttribute("currentUser", currentUser);
        return "dispute/detail";
    }

    @PostMapping("/message")
    public String addMessage(@RequestParam Integer disputeId,
                             @RequestParam String content,
                             HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) return "redirect:/auth/login";

        disputeService.addMessage(disputeId, currentUser.getId(), content);
        return "redirect:/dispute/detail/" + disputeId;
    }
}
