package com.sysnormal.starters.security.sso.spring.sso_starter.configs;

import com.sysnormal.starters.security.sso.spring.sso_starter.properties.security.SecurityProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

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
    //@ConditionalOnMissingBean(name = "corsConfigurationSource")
    @Primary
    public CorsConfigurationSource corsConfigurationSource() {
        logger.debug("INIT {}.{}", this.getClass().getSimpleName(), "corsConfigurationSource");
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("https://*", "http://*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        logger.debug("END {}.{}", this.getClass().getSimpleName(), "corsConfigurationSource");
        return source;
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public CorsFilter corsFilter(CorsConfigurationSource source) {
        logger.debug("INIT {}.{}", this.getClass().getSimpleName(), "corsFilter");
        logger.debug("END {}.{}", this.getClass().getSimpleName(), "corsFilter");
        return new CorsFilter(source);
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
                    .cors(Customizer.withDefaults()) // enable CORS
                    .authorizeHttpRequests(auth -> auth
                            //.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                            //.requestMatchers(properties.getPublicEndPoints().toArray(new String[0])).permitAll()
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
