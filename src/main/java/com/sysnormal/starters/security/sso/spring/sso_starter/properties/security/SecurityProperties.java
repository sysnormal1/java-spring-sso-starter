package com.sysnormal.starters.security.sso.spring.sso_starter.properties.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;


/**
 * security properties
 *
 * @author aalencarvz1
 * @version 1.0.0
 */
@ConfigurationProperties(prefix = "sso.security")
@Getter
@Setter
public class SecurityProperties {
    private boolean enabled = true;

    public List<String> publicEndPoints = List.of(
            "/auth/login",
            "/auth/register",
            "/auth/check_token",
            "/auth/send_email_recover_password",
            "/auth/password_change",
            "/auth/refresh_token",
            "/auth/google/get_login_url",
            "/auth/google/handle_code",
            "/auth/github/get_login_url",
            "/auth/github/handle_code"
    );

    private PasswordRules passwordRules = new PasswordRules();



    @Getter
    @Setter
    public static class PasswordRules {
        private int minLength = 8;
        private Boolean requireUppercase = true;
        private Boolean requireLowercase = true;
        private Boolean requireDigits = true;
        private Boolean requireSpecial = false;
    }
}
