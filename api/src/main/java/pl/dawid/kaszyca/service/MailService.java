package pl.dawid.kaszyca.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import pl.dawid.kaszyca.model.User;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.Properties;

@Service
@Slf4j
public class MailService {

    private JavaMailSenderImpl mailSender;
    private static final String USER = "user";
    private static final String BASE_URL = "baseUrl";
    private static final String CURRENT_DATE = "currentDate";
    private static final String TITLE = "title";
    private static final String EMAIL = "email";

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Autowired
    private MessageSource messageSource;

    public MailService(Environment environment) {
        mailSender = new JavaMailSenderImpl();
        mailSender.setUsername(environment.getProperty("spring.mail.username"));
        mailSender.setPassword(environment.getProperty("spring.mail.password"));
        mailSender.setHost(environment.getProperty("spring.mail.host"));
        mailSender.setPort(Integer.parseInt(Objects.requireNonNull(environment.getProperty("spring.mail.port"))));
        Properties prop = mailSender.getJavaMailProperties();
        prop.put("mail.transport.protocol", "smtp");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.debug", "true");
    }

    @Async
    public void sendActivationEmail(User user, String language) {
        log.debug("Sending activation email to '{}'", user.getEmail());
        Context context = getUserContext(user, language);
        sendEmailFromTemplate(user.getEmail(), context, "mail/activationEmail", "email.activation.title", language);
    }

    @Async
    public void sendPasswordResetMail(User user, String language) {
        log.debug("Sending password reset email to '{}'", user.getEmail());
        Context context = getUserContext(user, language);
        sendEmailFromTemplate(user.getEmail(), context, "mail/passwordResetEmail", "email.reset.title", language);
    }

    @Async
    public void sendPasswordChangedMail(User user, String language) {
        log.debug("Sending password changed email to '{}'", user.getEmail());
        Context context = getUserContext(user, language);
        sendEmailFromTemplate(user.getEmail(), context, "mail/passwordChangedEmail", "email.password.change.title", language);
    }

    @Async
    public void sendReportedAuctionMail(String email, String auctionTitle, String language) {
        log.debug("Sending password changed email to '{}'", email);
        Context context = getReportedAuctionContext(email, auctionTitle, language);
        sendEmailFromTemplate(email, context, "mail/reportAuctionEmail", "email.report.title", language);
    }

    private Context getUserContext(User user, String language) {
        Locale locale = Locale.forLanguageTag(language);
        Context context = new Context(locale);
        context.setVariable(USER, user);
        context.setVariable(CURRENT_DATE, getCurrentDate());
        context.setVariable(BASE_URL, "http://192.168.1.5:4200");
        return context;
    }

    private Context getReportedAuctionContext(String email, String auctionTitle, String language) {
        Locale locale = Locale.forLanguageTag(language);
        Context context = new Context(locale);
        context.setVariable(TITLE, auctionTitle);
        context.setVariable(CURRENT_DATE, getCurrentDate());
        context.setVariable(EMAIL, email);
        context.setVariable(BASE_URL, "http://192.168.1.5:4200");
        return context;
    }

    private void sendEmailFromTemplate(String email, Context context, String templateName, String titleKey, String language) {
        String content = templateEngine.process(templateName, context);
        String subject = messageSource.getMessage(titleKey, null, Locale.forLanguageTag(language));
        sendEmail(email, subject, content, false, true);
    }

    private String getCurrentDate() {
        Date now = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return simpleDateFormat.format(now);
    }

    private void sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
        log.info("Send email[multipart '{}' and html '{}'] to '{}' with subject '{}' and content={}",
                isMultipart, isHtml, to, subject, content);
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, StandardCharsets.UTF_8.name());
            message.setTo(to);
            message.setFrom(Objects.requireNonNull(mailSender.getUsername()));
            message.setSubject(subject);
            message.setText(content, isHtml);
            mailSender.send(mimeMessage);
            log.debug("Sent email to User '{}'", to);
        } catch (MailException | MessagingException e) {
            log.warn("Email could not be sent to user '{}'", to, e);
        }
    }
}
