package hsf302.springboot.webtrunggian.service;

import hsf302.springboot.webtrunggian.entity.PasswordReset;
import hsf302.springboot.webtrunggian.entity.User;
import hsf302.springboot.webtrunggian.repository.PasswordResetTokenRepository;
import hsf302.springboot.webtrunggian.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import javax.management.RuntimeErrorException;

@Service
public class PassworkResetService {

    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;

    public PassworkResetService(
            PasswordResetTokenRepository tokenRepository,
            UserRepository userRepository
    ){
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
    }

    public void saveToken(String email, String otp, String token){

        // tìm user theo email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // tạo entity
        PasswordReset resetToken = new PasswordReset();

        resetToken.setUser(user);
        resetToken.setOtp(otp);
        resetToken.setToken(token);
        resetToken.setCreatedAt(LocalDateTime.now());
        resetToken.setExpiresAt(LocalDateTime.now().plusSeconds(30));

        tokenRepository.save(resetToken);
    }
    public PasswordReset TokenCheck(String token){

        PasswordReset resetToken = tokenRepository.findByToken(token)
                .orElse(null);

        if(resetToken == null) return null;

        if(resetToken.getExpiresAt().isBefore(LocalDateTime.now()))
            return null;

        return resetToken;
    }

    public void deleteToken(PasswordReset resetToken) {
        tokenRepository.delete(resetToken);
    }
}
