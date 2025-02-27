package study.sunsuwedding.domain.user.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import study.sunsuwedding.domain.user.entity.Couple;
import study.sunsuwedding.domain.user.entity.Planner;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSignUpRequest {

    @NotEmpty(message = "역할은 비어있으면 안됩니다.")
    private String role;

    @NotEmpty(message = "이름은 비어있으면 안됩니다.")
    @Size(min = 2, max = 8, message = "이름은 2에서 8자 이내여야 합니다.")
    private String username;

    @NotEmpty(message = "이메일은 비어있으면 안됩니다.")
    @Size(max = 255, message = "이메일은 255자 이내여야 합니다.")
    @Pattern(regexp = "^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$", message = "이메일 형식으로 작성해주세요")
    private String email;

    @NotEmpty(message = "패스워드는 비어있으면 안됩니다.")
    @Size(min = 8, max = 20, message = "패스워드는 8에서 20자 이내여야 합니다.")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@#$%^&+=!~`<>,./?;:'\"\\[\\]{}\\\\()|_-])\\S*$", message = "영문, 숫자, 특수문자가 포함되어야하고 공백이 포함될 수 없습니다.")
    private String password;

    @NotEmpty(message = "패스워드2는 비어있으면 안됩니다.")
    @Size(min = 8, max = 20, message = "패스워드는 8에서 20자 이내여야 합니다.")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@#$%^&+=!~`<>,./?;:'\"\\[\\]{}\\\\()|_-])\\S*$", message = "영문, 숫자, 특수문자가 포함되어야하고 공백이 포함될 수 없습니다.")
    private String password2;

    public Planner toPlannerEntity(String encodedPassword) {
        return new Planner(username, email, encodedPassword);
    }

    public Couple toCoupleEntity(String encodedPassword) {
        return new Couple(username, email, encodedPassword);
    }
}
