package hsf302.springboot.webtrunggian.controller;

import hsf302.springboot.webtrunggian.entity.PasswordReset;
import hsf302.springboot.webtrunggian.entity.User;
import hsf302.springboot.webtrunggian.form.PasswordResetForm;
import hsf302.springboot.webtrunggian.repository.UserRepository;
import hsf302.springboot.webtrunggian.service.UserService;
import hsf302.springboot.webtrunggian.service.EmailService;
import hsf302.springboot.webtrunggian.service.PassworkResetService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import java.util.UUID;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
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

    public String generateOTP() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    public String generateToken() {
        return UUID.randomUUID().toString();
    }

    @Autowired
    private EmailService emailService;

    @Autowired
    private PassworkResetService passworkResetService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private UserService userService;

    /*
     * =============================================================================
     * =========================
     */
    @GetMapping("/login")
    // @ResponseBody
    public String login(HttpServletResponse response, Authentication auth) {
        if (auth != null && auth.isAuthenticated()// check sign up
                && !(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/home";
        }
        System.out.println("HIT");
        // response.setHeader("Cache-Control",
        // "no-cache, no-store, must-revalidate");
        // response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 86400000);// giới hạn cache
        return "registration/login";
    }

    /*
     * =============================================================================
     * =========================
     */
    @GetMapping("/register")
    public String register(Authentication auth, Model model) {
        if (auth != null && auth.isAuthenticated()
                && !(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/home";
        }
        model.addAttribute("user", new User());
        return "registration/register";
    }

    @PostMapping("/doRegister")
    public String doRegister(
            @Valid @ModelAttribute("user") User user,
            BindingResult result,
            Model model) {
        System.out.println("REGISTER HIT");
        System.out.println(user);

        // Check cho @Valid
        if (result.hasErrors()) {
            return "registration/register"; // quay lại JSP
        }

        // check email duplicate
        if (userService.existsByEmail(user.getEmail())) {
            result.rejectValue(
                    "email",
                    "error.user",
                    "Email already exists");
        }

        // check username duplicate
        if (userService.existsByUsername(user.getUsername())) {
            result.rejectValue(
                    "username",
                    "error.user",
                    "Username already exists");
        }

        // Check for logical errors
        if (result.hasErrors()) {
            return "registration/register";
        }

        user.setPassword(encoder.encode(user.getPassword()));
        userRepository.save(user);

        // Create each user with 1 wallet
        userService.createWallet(user);

        return "redirect:/auth/login";
    }

    /*
     * =============================================================================
     * =========================
     */
    @GetMapping("/reset")
    // @ResponseBody
    public String reset(HttpServletResponse response, Authentication auth, Model model) {
        if (auth != null && auth.isAuthenticated()
                && !(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/home";
        }
        model.addAttribute("user", new User());
        return "registration/reset";
    }

    @PostMapping("/doReset")
    public String doReset(
            @Valid @ModelAttribute("user") User user,
            BindingResult result, Model model
        ) {
        System.out.println("RESET HIT!");
        if (result.hasErrors()) {
            return "registration/reset"; // quay lại JSP
        }
        // check email duplicate
        if (userService.existsByEmail(user.getEmail())) {

            String otp   = generateOTP();
            String token = generateToken();

            // lưu OTP code và token vào DB
            passworkResetService.saveToken(user.getEmail(), otp, token);

            // gửi email
            emailService.sendResetPasswordMail(
                    user.getEmail(),
                    user.getUsername(),
                    otp
                );
            return "redirect:/auth/verify?token=" + token;
        }


        result.rejectValue("email", "error.email", "Email doesn't exists");
        return "registration/reset";
    }
    /*
     * =============================================================================
     * =========================
     */
    @GetMapping("/verify")
    public String verify(
        Authentication auth,
        @RequestParam("token") String token,
        Model model
    ) {
        if (auth != null && auth.isAuthenticated()
                && !(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/home";
        }

        model.addAttribute("passwordReset", new PasswordReset());
        model.addAttribute("token", token);
        return "registration/verify";
    }
    @PostMapping("/doVerify")
    public String doVerify(
        @Valid @ModelAttribute("passwordReset") PasswordReset pReset,
        @RequestParam ("token") String token,
        BindingResult result,
        Model model
    ){
        System.out.println("VERIFY HIT!");

        model.addAttribute("token", token);
        if (result.hasErrors()) {
            return "registration/verify"; // quay lại JSP
        }

        if (pReset.getOtp() == null || pReset.getOtp().trim().isEmpty()) {
            result.rejectValue("otp", "error.otp", "Mã OTP ko hợp");
            return "registration/verify";
        }

        PasswordReset tokenResult = passworkResetService.TokenCheck(token);
        if (tokenResult == null) {
            result.rejectValue("otp", "error.token", "Token hết hạn");
            return "registration/verify";
        }

        if (!tokenResult.getOtp().equals(pReset.getOtp())) {
            result.rejectValue("otp", "error.otp", "Mã OTP ko hợp");
            return "registration/verify";
        }

        return "redirect:/auth/newPassword?token=" + token;
    }
    /*
     * =============================================================================
     * =========================
     */
    @GetMapping("/newPassword")
    public String newPassword(
        Authentication auth,
        @RequestParam("token") String token,
        @ModelAttribute("passwordForm") PasswordResetForm passwordForm,
        BindingResult result,
        Model model
    ){
        if (auth != null && auth.isAuthenticated()
                && !(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/home";
        }

        if (passworkResetService.TokenCheck(token) == null) {
            result.rejectValue("newPassword", "error.token", "Token hết hạn");
        }

        model.addAttribute("token", token);
        return "registration/newPassword";
    }

    @PostMapping("/doNewPassword")
    public String doNewPassword(
        @Valid @ModelAttribute("passwordForm") PasswordResetForm passwordForm,
        BindingResult result,
        @RequestParam("token") String token,
        Model model
    ){
        System.out.println("NEW PASSWORD HIT!");

        model.addAttribute("token", token);

        PasswordReset tokenResult = passworkResetService.TokenCheck(token);
        if (tokenResult == null) {
            result.rejectValue("newPassword", "error.token", "Token hết hạn");
            return "registration/newPassword";
        }

        if (!result.hasFieldErrors("confirmPassword")
                && !result.hasFieldErrors("newPassword")
                && passwordForm.getConfirmPassword() != null
                && !passwordForm.getConfirmPassword().equals(passwordForm.getNewPassword())) {
            result.rejectValue(
                "confirmPassword",
                "error.confirm",
                "Mật khẩu xác nhận không khớp với mật khẩu mới");
        }

        if (result.hasErrors()) {
            return "registration/newPassword";
        }

        User user = null;
        user = tokenResult.getUser();
        if (user != null) {
            user.setPassword(encoder.encode(passwordForm.getNewPassword()));
        }
        if (user != null) {
            userRepository.save(user);
        }
        passworkResetService.deleteToken(tokenResult);

        return "redirect:/auth/login";
    }
    /*
     * =============================================================================
     * =========================
     */
    // check account status (sign in or not)
    @GetMapping("/status")
    @ResponseBody
    public boolean status(Authentication auth) {
        // true = đăng nhập hợp lệ
        return auth != null
                && auth.isAuthenticated()
                && !(auth.getPrincipal() instanceof String);
    }

    /*
     * =============================================Test============================
     * =================
     */
    @GetMapping("/user/userProfile")
    // @ResponseBody
    @PreAuthorize("hasRole('USER')") // chỉ cho USER role - có tài khoản
    public String userProfile() {
        return "userProfile";
    }

    @GetMapping("/admin/adminProfile")
//    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')") // chỉ cho ADMIN role
    public String adminProfile() {
//        return "Welcome to Admin Profile";
        return "registration/adminProfile";
    }
}
