package pl.dawid.kaszyca.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dawid.kaszyca.config.AuthoritiesConstants;
import pl.dawid.kaszyca.dto.UserDTO;
import pl.dawid.kaszyca.exception.EmailAlreadyUsedException;
import pl.dawid.kaszyca.exception.InvalidPasswordException;
import pl.dawid.kaszyca.exception.UserNotExistException;
import pl.dawid.kaszyca.exception.UsernameAlreadyUsedException;
import pl.dawid.kaszyca.model.Authority;
import pl.dawid.kaszyca.model.User;
import pl.dawid.kaszyca.repository.AuthorityRepository;
import pl.dawid.kaszyca.repository.UserRepository;
import pl.dawid.kaszyca.util.MapperUtil;
import pl.dawid.kaszyca.util.RandomUtil;
import pl.dawid.kaszyca.util.SecurityUtil;
import pl.dawid.kaszyca.vm.ChangePasswordVM;
import pl.dawid.kaszyca.vm.ResetPasswordVM;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@Transactional
@Slf4j
public class UserService {

    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    private StatisticService statisticService;

    private AuthorityRepository authorityRepository;

    private MailService mailService;

    AuctionService auctionService;

    AttachmentService attachmentService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       AuthorityRepository authorityRepository, MailService mailService, @NonNull @Lazy AuctionService auctionService,
                       @NonNull @Lazy AttachmentService attachmentService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authorityRepository = authorityRepository;
        this.mailService = mailService;
        this.auctionService = auctionService;
        this.attachmentService = attachmentService;
    }

    @Autowired
    public void setStatisticService(StatisticService statisticService) {
        this.statisticService = statisticService;
    }

    public Optional<User> activateRegistration(String key) {
        log.debug("Activating user for activation key {}", key);
        return userRepository.findOneByActivationKey(key)
                .map(user -> {
                    // activate given user for the registration key.
                    user.setActivated(true);
                    user.setActivationKey(null);
                    log.debug("Activated user: {}", user);
                    return user;
                });
    }

    public void registerUser(UserDTO userDTO, String password, String language) {
        if (!checkPasswordLength(password)) {
            throw new InvalidPasswordException();
        }
        userRepository.findOneByLogin(userDTO.getLogin().toLowerCase()).ifPresent(existingUser -> {
            boolean removed = removeNonActivatedUser(existingUser);
            if (!removed) {
                throw new UsernameAlreadyUsedException();
            }
        });
        userRepository.findOneByEmailIgnoreCase(userDTO.getEmail()).ifPresent(existingUser -> {
            boolean removed = removeNonActivatedUser(existingUser);
            if (!removed) {
                throw new EmailAlreadyUsedException();
            }
        });
        User newUser = new User();
        String encryptedPassword = passwordEncoder.encode(password);
        newUser.setLogin(userDTO.getLogin().toLowerCase());
        newUser.setPassword(encryptedPassword);
        newUser.setFirstName(userDTO.getFirstName());
        newUser.setLastName(userDTO.getLastName());
        if (userDTO.getEmail() != null) {
            newUser.setEmail(userDTO.getEmail().toLowerCase());
        }
        newUser.setActivated(false);
        newUser.setActivationKey(RandomUtil.generateActivationKey());
        Set<Authority> authorities = new HashSet<>();
        authorityRepository.findById(AuthoritiesConstants.USER).ifPresent(authorities::add);
        newUser.setAuthorities(authorities);
        userRepository.save(newUser);
        log.debug("Created Information for User: {}", newUser);
        log.debug("Send activation mail to ", newUser.getFirstName());
        mailService.sendActivationEmail(newUser, language);
        statisticService.incrementDailyRegistration();
    }

    private static boolean checkPasswordLength(String password) {
        return !StringUtils.isEmpty(password) &&
                password.length() >= UserDTO.PASSWORD_MIN_LENGTH &&
                password.length() <= UserDTO.PASSWORD_MAX_LENGTH;
    }

    public void updateUser(String firstName, String lastName, String email) {
        SecurityUtil.getCurrentUserLogin()
                .flatMap(userRepository::findOneByLogin)
                .ifPresent(user -> {
                    user.setFirstName(firstName);
                    user.setLastName(lastName);
                    if (email != null) {
                        user.setEmail(email.toLowerCase());
                    }
                    log.debug("Changed Information for User: {}", user);
                });
    }

    public Optional<User> getCurrentUserObject() {
        Optional<String> login = SecurityUtil.getCurrentUserLogin();
        return login.map(userRepository::findOneByLogin).orElse(null);
    }

    public User getUserObjectById(Long id) {
        Optional<User> user = userRepository.findOneById(id);
        return user.orElse(null);
    }

    public String getCurrentUserName() {
        Optional<User> user = getCurrentUserObject();
        return user.map(User::getFirstName).orElse(null);
    }

    private boolean removeNonActivatedUser(User existingUser) {
        if (existingUser.getActivated()) {
            return false;
        }
        userRepository.delete(existingUser);
        userRepository.flush();
        return true;
    }

    public void changePassword(String currentClearTextPassword, String newPassword) {
        SecurityUtil.getCurrentUserLogin()
                .flatMap(userRepository::findOneByLogin)
                .ifPresent(user -> {
                    String currentEncryptedPassword = user.getPassword();
                    if (!passwordEncoder.matches(currentClearTextPassword, currentEncryptedPassword)) {
                        throw new InvalidPasswordException();
                    }
                    String encryptedPassword = passwordEncoder.encode(newPassword);
                    user.setPassword(encryptedPassword);
                    log.debug("Changed password for User: {}", user);
                });
    }

    public UserDTO getCurrentUser() {
        Optional<User> user = getCurrentUserObject();
        if (user.isPresent())
            return MapperUtil.map(user.get(), UserDTO.class);
        return null;
    }

    /**
     * Not activated users should be automatically deleted after 3 days.
     * <p>
     * This is scheduled to get fired everyday, at 01:00 (am).
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void removeNotActivatedUsers() {
        userRepository
                .findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(Instant.now().minus(3, ChronoUnit.DAYS))
                .forEach(user -> {
                    log.debug("Deleting not activated user {}", user.getLogin());
                    userRepository.delete(user);
                });
    }

    public void updateUser(User user) {
        userRepository.save(user);
    }

    public Optional<User> getUserByLogin(String login) {
        return userRepository.findOneByLogin(login);
    }

    public String getCurrentUserEmail() {
        Optional<User> user = getCurrentUserObject();
        return user.map(User::getEmail).orElse(null);
    }

    public boolean remindPassword(String email, String language) {
        Optional<User> optionalUser = userRepository.findOneByEmailIgnoreCase(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setResetKey(RandomUtil.generateResetKey());
            user.setResetKeyDate(Instant.now());
            userRepository.save(user);
            mailService.sendPasswordResetMail(user, language);
            return true;
        }
        return false;
    }

    public boolean checkResetKey(String key) {
        Optional<User> optionalUser = userRepository.findOneByResetKey(key);
        return optionalUser.isPresent();
    }

    public void resetPassword(ResetPasswordVM resetPasswordVM, String language) {
        Optional<User> optionalUser = userRepository.findOneByEmailIgnoreCaseAndResetKey(resetPasswordVM.getEmail(), resetPasswordVM.getResetKey());
        if (optionalUser.isPresent() && validateResetKeyDate(optionalUser.get())) {
            User user = optionalUser.get();
            String encodedPassword = passwordEncoder.encode(resetPasswordVM.getPassword());
            user.setPassword(encodedPassword);
            user.setResetKey(null);
            user.setResetKeyDate(null);
            userRepository.save(user);
            mailService.sendPasswordChangedMail(user, language);
        } else {
            throw new UserNotExistException();
        }
    }

    private boolean validateResetKeyDate(User user) {
        Instant currentDate = Instant.now().minus(1, ChronoUnit.HOURS);
        return user.getResetKeyDate().compareTo(currentDate) > 0;
    }

    public void updatePassword(ChangePasswordVM changePasswordVM, String language) {
        Optional<User> optionalUser = getCurrentUserObject();
        if (optionalUser.isPresent() && checkOldPassword(optionalUser.get().getPassword(), changePasswordVM.getOldPassword())) {
            User user = optionalUser.get();
            user.setPassword(changePasswordVM.getNewPassword());
            userRepository.save(user);
            mailService.sendPasswordChangedMail(user, language);
        } else {
            throw new UserNotExistException();
        }
    }

    private boolean checkOldPassword(String currentPasswordHash, String oldPassword) {
        return passwordEncoder.matches(oldPassword, currentPasswordHash);
    }

    public List<UserDTO> allUsers() {
        List<User> users = userRepository.findAll();
        if (!users.isEmpty())
            return MapperUtil.mapAll(users, UserDTO.class);
        return new ArrayList<>();
    }

    public void removeUserById(long id) {
        Optional<User> optionalUser = userRepository.findOneById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            auctionService.removeUserAuction(user);
            attachmentService.removeUserPhoto(user);
            userRepository.delete(user);
        }
    }

    public boolean userContainsAuthority(User user, String role) {
        Optional<Authority> authority = authorityRepository.findById(role);
        return authority.isPresent() && user.getAuthorities().contains(authority.get());
    }

    public void grantUserPermissionById(long id) {
        setUserAdmin(id, true);
    }

    public void revokeUserPermissionById(long id) {
        setUserAdmin(id, false);
    }

    private void setUserAdmin(long id, boolean isGrant) {
        Optional<User> optionalUser = userRepository.findOneById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            Set<Authority> authorities = user.getAuthorities();
            if (isGrant)
                authorityRepository.findById(AuthoritiesConstants.ADMIN).ifPresent(authorities::add);
            else
                authorityRepository.findById(AuthoritiesConstants.ADMIN).ifPresent(authorities::remove);
            user.setAuthorities(authorities);
            userRepository.save(user);
        } else {
            throw new UserNotExistException();
        }
    }
}
