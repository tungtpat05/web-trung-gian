package hsf302.springboot.webtrunggian.controller;

import hsf302.springboot.webtrunggian.entity.User;
import hsf302.springboot.webtrunggian.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@AllArgsConstructor
public class GlobalModelAttribute {

    private final UserService userService;

    @ModelAttribute("currentUser")
    public User populateCurrentUser(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()
                || auth.getPrincipal().equals("anonymousUser")) {
            return null;
        }

        String username = auth.getName();
        return userService.findByUsername(username);
    }
}