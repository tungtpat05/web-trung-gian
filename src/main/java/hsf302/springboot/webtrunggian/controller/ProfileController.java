package hsf302.springboot.webtrunggian.controller;

import hsf302.springboot.webtrunggian.entity.User;
import hsf302.springboot.webtrunggian.repository.UserRepository;
import hsf302.springboot.webtrunggian.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/user")
    public String userProfile(Authentication auth, Model model){
        String username = auth.getName();
        User user = userService.findByUsername(username);
        model.addAttribute("user",user);
        return "profile/userProfile";
    }
    @PostMapping("/update-username")
    public String nameUpdate(
            Authentication auth,
            @RequestParam String username,
            RedirectAttributes redirectAttributes
    ){
        String currentUsername = auth.getName();

        User user = userService.findByUsername(currentUsername);

        //check trùng username
        if (userService.existsByUsername(username)) {
            redirectAttributes.addFlashAttribute("error", "Username đã tồn tại");
            return "redirect:/profile/user";
        }

        //update
        user.setUsername(username);

        userRepository.save(user);

        redirectAttributes.addFlashAttribute("success", "Cập nhật thành công");

        return "redirect:/logout";

    }
}
