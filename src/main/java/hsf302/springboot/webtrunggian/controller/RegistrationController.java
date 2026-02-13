package hsf302.springboot.webtrunggian.controller;

import hsf302.springboot.webtrunggian.entity.MyUser;
import hsf302.springboot.webtrunggian.repository.UserRepository;
import hsf302.springboot.webtrunggian.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
public class RegistrationController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @GetMapping("/login")
//    @ResponseBody
    public String login(HttpServletResponse response, Authentication auth) {
        if (auth != null && auth.isAuthenticated()//check sign up
                && !(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/home";
        }
//        response.setHeader("Cache-Control",
//                "no-cache, no-store, must-revalidate");
//        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 86400000);//giới hạn cache
        return "login";
    }


    @GetMapping("/register")
    public String register(Authentication auth) {
        if (auth != null && auth.isAuthenticated()
                && !(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/home";
        }
//        model.addAttribute("user", new MyUser());
        return "register";
    }

    @PostMapping("/doRegister")
    public String doRegister(
            @Valid MyUser user,
            BindingResult result
    ) {
        System.out.println("REGISTER HIT");
        System.out.println(user);
        if (result.hasErrors()) {
            return "register"; // quay lại JSP
        }

        user.setPassword(encoder.encode(user.getPassword()));
        userRepository.save(user);
        return "redirect:/auth/login";
    }


    @GetMapping("/reset")
//    @ResponseBody
    public String reset(HttpServletResponse response, Authentication auth) {
        if (auth != null && auth.isAuthenticated()
                && !(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/home";
        }
        /*
        Lấy email được nhập vào
        kiểm tra tồn tại trong DB
        Nếu có thì gửi verification qua email
         */
        return "reset";
    }

    //check account status (sign in or not)
    @GetMapping("/status")
    @ResponseBody
    public boolean status(Authentication auth) {
        //true = đăng nhập hợp lệ
        return auth != null
                && auth.isAuthenticated()
                && !(auth.getPrincipal() instanceof String);
    }

    /*=============================================Test=============================================*/
    @GetMapping("/user/userProfile")
//    @ResponseBody
    @PreAuthorize("hasRole('USER')") //chỉ cho USER role - có tài khoản
    public String userProfile() {
        return "userProfile";
    }

    @GetMapping("/admin/adminProfile")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')") //chỉ cho ADMIN role
    public String adminProfile() {
        return "Welcome to Admin Profile";
    }
}
