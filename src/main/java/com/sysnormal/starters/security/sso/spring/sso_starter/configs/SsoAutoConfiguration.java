package com.sysnormal.starters.security.sso.spring.sso_starter.configs;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


/**
 * SsoAutoConfiguration
 *
 * @author aalencarvz1
 * @version 1.0.0
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(name = "org.springframework.boot.SpringApplication")
@Import({
        DatabaseAutoConfiguration.class,
        FlywayAfterHibernate.class,
        SecurityAutoConfiguration.class,
        MailAutoConfiguration.class,
        ServerAutoConfiguration.class,
        GoogleAuthAutoConfiguration.class
})
public class SsoAutoConfiguration {
}
