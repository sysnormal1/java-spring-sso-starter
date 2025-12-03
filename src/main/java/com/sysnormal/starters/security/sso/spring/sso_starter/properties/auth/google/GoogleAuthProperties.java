package com.sysnormal.starters.security.sso.spring.sso_starter.properties.auth.google;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "sso.auth.google")
@ConditionalOnProperty(prefix = "sso.auth.google", name = "enabled", havingValue = "true")
public class GoogleAuthProperties {
    private boolean enabled = false;
    private String clientId = null;
    private String clientSecret = null;
    private String redirectUri = null;
}