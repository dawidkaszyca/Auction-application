package pl.dawid.kaszyca.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.dawid.kaszyca.dto.UserDTO;
import pl.dawid.kaszyca.model.User;
import pl.dawid.kaszyca.service.UserService;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@Slf4j
@CrossOrigin
@RequestMapping("/api")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity registerAccount(@Valid @RequestBody UserDTO managedUserVM) {
        try {
            userService.registerUser(managedUserVM, managedUserVM.getPassword());
            return new ResponseEntity(HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Cannot create user");
            return new ResponseEntity(e.getMessage(), HttpStatus.valueOf(500));
        }
    }

    @GetMapping("/activate")
    public ResponseEntity activateAccount(@RequestParam(value = "key") String key) {
        try {
            Optional<User> user = userService.activateRegistration(key);
            return user != null ? new ResponseEntity(user, HttpStatus.OK) : new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            log.error("Cannot find user");
            return new ResponseEntity(e.getMessage(), HttpStatus.valueOf(500));
        }
    }

    @GetMapping("/profile")
    public ResponseEntity getCurrentUser() {
        try {
            UserDTO user = userService.getCurrentUser();
            return user != null ? new ResponseEntity(user, HttpStatus.OK) : new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            log.error("Cannot find user");
            return new ResponseEntity(e.getMessage(), HttpStatus.valueOf(500));
        }
    }

    @PostMapping("/update")
    public ResponseEntity updateUser(@RequestBody UserDTO user) {
        try {
            userService.updateUser(user.getFirstName(), user.getLastName(), user.getEmail());
            return new ResponseEntity(HttpStatus.OK);
        } catch (Exception e) {
            log.error("Cannot change user data");
            return new ResponseEntity(e.getMessage(), HttpStatus.valueOf(500));
        }
    }
}
