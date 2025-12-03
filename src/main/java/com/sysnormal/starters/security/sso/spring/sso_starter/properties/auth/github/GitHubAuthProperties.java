package com.sysnormal.starters.security.sso.spring.sso_starter.properties.auth.github;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "sso.auth.github")
@ConditionalOnProperty(prefix = "sso.auth.github", name = "enabled", havingValue = "true")
public class GitHubAuthProperties {
    private boolean enabled = false;
    private String clientId = null;
    private String clientSecret = null;
    private String redirectUri = null;
}