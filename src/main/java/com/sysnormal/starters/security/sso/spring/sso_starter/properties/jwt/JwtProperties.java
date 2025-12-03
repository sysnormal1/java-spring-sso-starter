package com.sysnormal.starters.security.sso.spring.sso_starter.properties.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;


/**
 * jwt properties
 *
 * @author aalencarvz1
 * @version 1.0.0
 */
@ConfigurationProperties(prefix = "sso.jwt")
@Getter
@Setter
public class JwtProperties {

    private boolean enabled = true;
    private String secretKey = "f324a2deac2fe6c1b8585fd3bae1df33956d6a918cfeffcbf772cb2ec4001bf8";
    private Long defaultTokenExpiration = 60000L;
    private Long defaultRefreshTokenExpiration = 300000L;
}