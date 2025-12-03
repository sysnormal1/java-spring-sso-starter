package com.sysnormal.starters.security.sso.spring.sso_starter.properties.server;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * server properties
 *
 * @author aalencarvz1
 * @version 1.0.0
 */
@ConfigurationProperties(prefix = "sso.server")
@Getter
@Setter
public class ServerProperties {

    private boolean enabled = true;
    private int port = 3000;
    private Integer localPort = null;
    private Ssl ssl = new Ssl();

    @Getter
    @Setter
    public static class Ssl {
        private boolean enabled = false;
        private String keyStore;
        private String keyStorePassword;
        private String keyStoreType;
    }
}