package pl.dawid.kaszyca.vm;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.dawid.kaszyca.dto.UserDTO;
import pl.dawid.kaszyca.model.User;

import javax.validation.constraints.Size;


@Getter
@Setter
@NoArgsConstructor
public class RegisterFormVM extends UserDTO {

    public static final int PASSWORD_MIN_LENGTH = 4;

    public static final int PASSWORD_MAX_LENGTH = 100;

    @Size(min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH)
    private String password;

    RegisterFormVM(User user, String password) {
        super(user);
        this.password = password;
    }

}
