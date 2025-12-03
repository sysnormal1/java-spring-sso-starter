package com.sysnormal.starters.security.sso.spring.sso_starter.configs;

import com.sysnormal.starters.security.sso.spring.sso_starter.properties.auth.google.GoogleAuthProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(GoogleAuthProperties.class)
@ConditionalOnProperty(prefix = "sso.auth.google", name = "enabled", havingValue = "true")
public class GoogleAuthAutoConfiguration {

}
