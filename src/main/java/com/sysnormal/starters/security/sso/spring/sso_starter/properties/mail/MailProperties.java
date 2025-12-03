package com.sysnormal.starters.security.sso.spring.sso_starter.properties.mail;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Properties;


/**
 * mail properties
 *
 * @author aalencarvz1
 * @version 1.0.0
 */
@ConfigurationProperties(prefix = "sso.mail")
@Getter
@Setter
public class MailProperties {

    private boolean enabled = true;
    private String host = "";
    private int port = 445;
    private String username = "";
    private String password = "";
    private String protocol = "";
    private Properties properties = new Properties();

}
