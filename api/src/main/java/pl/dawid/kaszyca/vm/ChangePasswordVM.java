package pl.dawid.kaszyca.vm;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class ChangePasswordVM {

    @NotNull
    @Size(min = 4, max = 100)
    String oldPassword;

    @NotNull
    @Size(min = 4, max = 100)
    String newPassword;
}
