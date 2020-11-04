package pl.dawid.kaszyca.vm;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class ResetPasswordVM {

    @NotNull
    @Size(min = 1, max = 50)
    String email;

    @NotNull
    @Size(min = 4, max = 100)
    String password;

    String resetKey;
}
