package hsf302.springboot.webtrunggian.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordResetForm {
    @NotBlank(message = "Mật khẩu không đáp ứng yêu cầu")
    @Size(min = 8, max = 255, message = "Mật khẩu không đáp ứng yêu cầu")
    private String newPassword;

    @NotBlank(message = "Mật khẩu xác nhận không khớp với mật khẩu mới")
    private String confirmPassword;

}
