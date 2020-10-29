package pl.dawid.kaszyca.vm;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class LoginFormVM {

    @NotNull
    @Size(min = 1, max = 50)
    String username;

    @NotNull
    @Size(min = 4, max = 100)
    String password;

    Boolean rememberMe;

}
