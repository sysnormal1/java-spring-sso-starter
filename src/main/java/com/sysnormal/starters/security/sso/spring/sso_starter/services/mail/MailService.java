package com.sysnormal.starters.security.sso.spring.sso_starter.services.mail;

import com.sysnormal.starters.security.sso.spring.sso_starter.properties.mail.MailProperties;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * mail service
 *
 * @author aalencarvz1
 * @version 1.0.0
 */
@Service
@EnableConfigurationProperties(MailProperties.class)
public class MailService {

    private final MailProperties properties;

    @Autowired
    private JavaMailSender mailSender;

    public MailService(MailProperties properties) {
        this.properties = properties;
    }

    public boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email != null && email.matches(emailRegex);
    }


    public void sendEmail(String to, String subject, String text, String html) throws Exception {
        if (isValidEmail(to)) {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, html);
            helper.setFrom(properties.getUsername()); // configure conforme seu SMTP
            mailSender.send(message);
        } else {
            throw new Exception("invalid mail");
        }
    }
}
