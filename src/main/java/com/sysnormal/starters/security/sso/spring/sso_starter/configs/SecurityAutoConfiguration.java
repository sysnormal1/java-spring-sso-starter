package com.sysnormal.starters.security.sso.spring.sso_starter.configs;

import com.sysnormal.starters.security.sso.spring.sso_starter.properties.security.SecurityProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;


/**
 * SecurityAutoConfiguration
 *
 * @author aalencarvz1
 * @version 1.0.0
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "sso.security", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(SecurityProperties.class)
public class SecurityAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(SecurityAutoConfiguration.class);

    /**
     * the properties
     */
    private final SecurityProperties properties;

    /**
     * default constructor
     *
     * @param properties the properties
     */
    public SecurityAutoConfiguration(SecurityProperties properties) {
        logger.debug("INIT {}.{}", this.getClass().getSimpleName(), "SecurityAutoConfiguration");
        this.properties = properties;
        logger.debug("END {}.{}", this.getClass().getSimpleName(), "SecurityAutoConfiguration");
    }

    /**
     * cors configure
     *
     * cannot change name of this method, spring internally filter this method by yout name, exactly this name
     *
     * @return the cors configuration
     */
    @Bean
    @ConditionalOnMissingBean(name = "corsConfigurationSource")
    public CorsConfigurationSource corsConfigurationSource() {
        logger.debug("INIT {}.{}", this.getClass().getSimpleName(), "corsConfigurationSource");
        UrlBasedCorsConfigurationSource result = null;
        try {
            CorsConfiguration configuration = new CorsConfiguration();
            configuration.setAllowedOriginPatterns(List.of("*"));
            configuration.setAllowedMethods(List.of("*"));
            configuration.setAllowedHeaders(List.of("*"));
            configuration.setAllowCredentials(true);
            result = new UrlBasedCorsConfigurationSource();
            result.registerCorsConfiguration("/**", configuration);
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.debug("END {}.{}", this.getClass().getSimpleName(), "corsConfigurationSource");
        return result;
    }

    /**
     * filter chain
     *
     * @param http the http security
     * @return the security filter chain instance
     * @throws Exception throw on exception
     */
    @Bean
    @ConditionalOnMissingBean(name = "ssoFilterChain")
    public SecurityFilterChain ssoFilterChain(HttpSecurity http) throws Exception {
        logger.debug("INIT {}.{}", this.getClass().getSimpleName(), "ssoFilterChain");
        SecurityFilterChain result = null;
        try {
            http
                    .csrf(csrf -> csrf.disable())
                    .cors(cors -> {
                    }) // enable CORS
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers(properties.getPublicEndPoints().toArray(new String[0])).permitAll()
                            .anyRequest().permitAll()
                    );
            result = http.build();
            logger.debug("no errors on  {}.{}", this.getClass().getSimpleName(), "ssoFilterChain");
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.debug("END {}.{}", this.getClass().getSimpleName(), "ssoFilterChain");
        return result;
    }
}
