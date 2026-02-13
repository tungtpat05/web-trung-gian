package hsf302.springboot.webtrunggian.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping
public class UserController {


    @RequestMapping("/home")
    @ResponseBody
    public String home(HttpServletResponse response, Authentication auth){

        Object principal = auth.getPrincipal();//lấy user details
        if (principal instanceof org.springframework.security.core.userdetails.User user) {
            return String.format("""
               <h1>Welcome!</h1>
               <h1>Username: %s</h1>
               <h1>Roles: %s</h1>
               <h1>Password(hash): %s</h1>
               """,
                    user.getUsername(),
                    user.getAuthorities(),
                    user.getPassword()//null là ổn
            );
        }
        return "something went wrong!";
    }
}
