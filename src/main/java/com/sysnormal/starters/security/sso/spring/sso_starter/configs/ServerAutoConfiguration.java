package com.sysnormal.starters.security.sso.spring.sso_starter.configs;

import com.sysnormal.starters.security.sso.spring.sso_starter.properties.server.ServerProperties;
import org.apache.catalina.connector.Connector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.tomcat.servlet.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.Ssl;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * WebAutoConfiguration
 * Configure server to allow run with external port configured on application.yml or .env file and different local port, wich no requires https
 *
 * @author aalencarvz1
 * @version 1.0.0
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "sso.server", name = "enabled", havingValue = "true", matchIfMissing = true)
//@ConditionalOnClass({TomcatServletWebServerFactory.class})
@EnableConfigurationProperties(ServerProperties.class)
@ComponentScan(basePackages = {
        "com.oiis.sso_starter.services.mail",
        "com.oiis.sso_starter.services.jwt",
        "com.oiis.sso_starter.services.auth",
        "com.oiis.sso_starter.server.auth"
})
public class ServerAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(ServerAutoConfiguration.class);


    /**
     * customizer webserver
     *
     * @param props the properties
     * @return the customizer instance
     */
    @Bean
    @ConditionalOnMissingBean(name = "ssoWebServerCustomizer")
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> ssoWebServerCustomizer(ServerProperties props) {
        logger.debug("INIT {}.{}", this.getClass().getSimpleName(), "ssoWebServerCustomizer");
        WebServerFactoryCustomizer<TomcatServletWebServerFactory> result = null;
        try {
            result = server -> {
                server.setPort(props.getPort());

                if (props.getSsl().isEnabled()) {
                    Ssl ssl = new Ssl();
                    ssl.setEnabled(true);
                    ssl.setKeyStore(props.getSsl().getKeyStore());
                    ssl.setKeyStorePassword(props.getSsl().getKeyStorePassword());
                    server.setSsl(ssl);
                }

                if (props.getLocalPort() != null) {
                    Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
                    connector.setPort(props.getLocalPort());
                    server.addAdditionalConnectors(connector);
                }
            };
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.debug("END {}.{}", this.getClass().getSimpleName(), "ssoWebServerCustomizer");
        return result;
    }
}