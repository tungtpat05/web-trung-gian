package hsf302.springboot.webtrunggian.security;

import hsf302.springboot.webtrunggian.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@AllArgsConstructor
@EnableMethodSecurity // Cho phép dùng:@PreAuthorize, @PostAuthorize, @Secured
public class SecurityConfig {

    //Mã hoá các mật khẩu theo BCrypt và trả lại
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private final UserService userService;

    @Bean
    public UserDetailsService userDetailsService() {return userService;}

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

//    @Bean
//    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
//        /*
//        Ví dụ:
//         */
//        UserDetails admin = User
//                .withUsername("Amiya")
//                .password(encoder.encode("123"))
//                .roles("ADMIN")
//                .build();
//
//        UserDetails user = User
//                .withUsername("Ejaz")
//                .password(encoder.encode("123"))
//                .roles()
//                .build();
//
//        // InMemoryUserDetailsManager stores the users (in RAM)
//        return new InMemoryUserDetailsManager(admin, user);
//    }
/*====================================================================================================================*/
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
            // Disable CSRF for simplicity (REST API scenario)
//                .csrf(csrf -> csrf.disable())//csrf: 1 loại tấn công mạng
            .csrf(AbstractHttpConfigurer::disable)
            // Authorization rules - phân quyền theo URL
            .authorizeHttpRequests(auth -> auth
                            .dispatcherTypeMatchers(//cấu hình bảo mật - cho phép tất cả request truy cập ko xác thực
                                    jakarta.servlet.DispatcherType.FORWARD,
                                    jakarta.servlet.DispatcherType.ERROR
                            )
                            .permitAll()
                            .requestMatchers(
                                    "/css/**",
                                    "/js/**",
                                    "/images/**",
                                    "/fonts/**",
                                    "/vendor/**"
                            ).permitAll()
                            // public pages
                            .requestMatchers(
                                    "/auth/login",
                                    "/auth/register",
                                    "/auth/doRegister",
                                    "/auth/reset",
                                    "/auth/status"
                            ).permitAll()
                            .requestMatchers(
                                    "/home"
                            ).authenticated()//user đã đăng nhập
//                        .requestMatchers("/auth/admin/**").hasRole("ADMIN")//user có roler cụ thể

                            .anyRequest().authenticated()
            )

            .formLogin(form -> form
                    .loginPage("/auth/login")
                    .usernameParameter("username")
                    .passwordParameter("password")
                    .loginProcessingUrl("/doLogin")//nhận thông tin từ loginPage do spring tự tạo
                    .defaultSuccessUrl("/home", true) //login success tự động đến page này
                    .failureUrl("/auth/login?error=true").permitAll()
            );

    return http.build();
}
}
