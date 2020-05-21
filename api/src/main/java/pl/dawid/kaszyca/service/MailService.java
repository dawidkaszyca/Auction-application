package pl.dawid.kaszyca.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
@Slf4j
public class MailService {

    private JavaMailSenderImpl mailSender;

    public MailService(Environment environment) {
        mailSender = new JavaMailSenderImpl();
        mailSender.setUsername(environment.getProperty("spring.mail.username"));
        mailSender.setPassword(environment.getProperty("spring.mail.password"));
        mailSender.setHost(environment.getProperty("spring.mail.host"));
        mailSender.setPort(Integer.parseInt(environment.getProperty("spring.mail.port")));
        Properties prop = mailSender.getJavaMailProperties();
        prop.put("mail.transport.protocol", "smtp");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.debug", "true");
    }

    @Async
    public void sendMail(String to, String content, String subject) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);
            message.setFrom(mailSender.getUsername());
            this.mailSender.send(message);
            log.debug("Sent email to User '{}'", to);
        } catch (MailException e) {
            log.warn("Email could not be sent to user '{}'", to, e);
        }
    }

    @Async
    public void sendActiveMail(String firstName, String activeUrl, String email) {
        String suffixMessage = " for your registration at our service. Confirm your account by clicking here : http://localhost:4200/api/confirm-registration/";
        String prefixMessage = "Thanks ";
        String content = prefixMessage + firstName + suffixMessage + activeUrl;
        String subject = "Confirm your registration at ...TODO...";
        sendMail(email, content, subject);
    }
}
