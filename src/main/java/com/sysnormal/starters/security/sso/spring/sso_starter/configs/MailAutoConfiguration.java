package com.sysnormal.starters.security.sso.spring.sso_starter.configs;

import com.sysnormal.starters.security.sso.spring.sso_starter.properties.mail.MailProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;


/**
 * MailAutoConfiguration
 *
 * @author aalencarvz1
 * @version 1.0.0
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(MailProperties.class)
@ConditionalOnProperty(prefix = "sso.mail", name = "enabled", havingValue = "true", matchIfMissing = true)
//@Import(org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration.class) // spring 3.5.6
@Import(org.springframework.boot.mail.autoconfigure.MailSenderAutoConfiguration.class) //spring 4.0.0
@ComponentScan(basePackages = "com.oiis.sso_starter.services.mail")
public class MailAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(MailAutoConfiguration.class);

    /**
     * mail sender configure
     *
     * @param props the properties
     * @return the mail sender instance
     */
    @Bean
    @ConditionalOnMissingBean(name = "ssoMailSender")
    public JavaMailSender ssoMailSender(MailProperties props) {
        logger.debug("INIT {}.{}", this.getClass().getSimpleName(), "ssoMailSender");
        JavaMailSenderImpl result = null;
        try {
            result = new JavaMailSenderImpl();
            result.setHost(props.getHost());
            result.setPort(props.getPort());
            result.setUsername(props.getUsername());
            result.setPassword(props.getPassword());
            result.setProtocol(props.getProtocol());
            result.getJavaMailProperties().putAll(props.getProperties());
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.debug("END {}.{}", this.getClass().getSimpleName(), "ssoMailSender");
        return result;
    }
}