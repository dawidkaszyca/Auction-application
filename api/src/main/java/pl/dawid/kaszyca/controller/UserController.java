package pl.dawid.kaszyca.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.dawid.kaszyca.exception.InvalidPasswordException;
import pl.dawid.kaszyca.exception.UserNotExistException;
import pl.dawid.kaszyca.model.User;
import pl.dawid.kaszyca.repository.UserRepository;
import pl.dawid.kaszyca.service.UserService;
import pl.dawid.kaszyca.service.MailService;
import pl.dawid.kaszyca.vm.RegisterFormVM;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@Slf4j
@CrossOrigin
@RequestMapping("/api")
public class UserController {

    private final UserRepository userRepository;

    private final UserService userService;

    private final MailService mailService;

    public UserController(UserRepository userRepository, UserService userService, MailService mailService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.mailService = mailService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void registerAccount(@Valid @RequestBody RegisterFormVM managedUserVM) {
        if (!checkPasswordLength(managedUserVM.getPassword())) {
            throw new InvalidPasswordException();
        }
       userService.registerUser(managedUserVM, managedUserVM.getPassword());
    }

    @GetMapping("/activate")
    public void activateAccount(@RequestParam(value = "key") String key) {
        Optional<User> user = userService.activateRegistration(key);
        if (!user.isPresent()) {
            throw new UserNotExistException();
        }
    }

    private static boolean checkPasswordLength(String password) {
        return !StringUtils.isEmpty(password) &&
                password.length() >= RegisterFormVM.PASSWORD_MIN_LENGTH &&
                password.length() <= RegisterFormVM.PASSWORD_MAX_LENGTH;
    }
}
