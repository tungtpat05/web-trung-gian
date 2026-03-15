package hsf302.springboot.webtrunggian.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendResetPasswordMail(String toEmail, String userName, String iOTPCode) {

        String subject = "Reset Your Password";

        String body = "Hello " + userName + ",\n\n"
                + "We received a request to reset the password for your Intermediate Transaction account.\n\n"
                + "If you made this request, please copy the OTP code below:\n\n"
                + iOTPCode+"\n\n"
                + "For your security, this code will expire in 30 Second.\n\n"
                + "If you did not request a password reset, you can safely ignore this email — your account will remain secure.\n\n"
                + "Thank you,\n\n"
                + "The Intermediate Transaction company.\n\n"
                + "testingacc4now@gmail.com | 012345678";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }
}