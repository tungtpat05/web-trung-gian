package hsf302.springboot.webtrunggian.controller;

import hsf302.springboot.webtrunggian.entity.Dispute;
import hsf302.springboot.webtrunggian.entity.DisputeMessage;
import hsf302.springboot.webtrunggian.entity.User;
import hsf302.springboot.webtrunggian.service.DisputeService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/admin/dispute")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminDisputeController {

    private final DisputeService disputeService;

    @GetMapping("/list")
    public String listDisputes(Model model) {
        List<Dispute> disputes = disputeService.getAllDisputes();
        model.addAttribute("disputes", disputes);
        return "admin/dispute/list";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Integer id, Model model) {
        Dispute dispute = disputeService.getDisputeById(id);
        List<DisputeMessage> messages = disputeService.getMessages(id);
        model.addAttribute("dispute", dispute);
        model.addAttribute("messages", messages);
        return "admin/dispute/detail";
    }

    @PostMapping("/resolve")
    public String resolve(@RequestParam Integer disputeId,
                          @RequestParam String outcome,
                          @RequestParam String note,
                          @RequestParam(required = false) BigDecimal splitAmount,
                          HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) return "redirect:/auth/login";

        disputeService.resolveDispute(disputeId, currentUser.getId(), outcome, note, splitAmount);
        return "redirect:/admin/dispute/detail/" + disputeId;
    }

    @PostMapping("/message")
    public String addMessage(@RequestParam Integer disputeId,
                             @RequestParam String content,
                             HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) return "redirect:/auth/login";

        disputeService.addMessage(disputeId, currentUser.getId(), content);
        return "redirect:/admin/dispute/detail/" + disputeId;
    }
}
