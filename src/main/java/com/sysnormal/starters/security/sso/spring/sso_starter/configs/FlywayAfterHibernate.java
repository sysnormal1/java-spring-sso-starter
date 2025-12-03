package com.sysnormal.starters.security.sso.spring.sso_starter.configs;

import com.sysnormal.starters.security.sso.spring.sso_starter.properties.database.DatabaseProperties;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * FlywayAfterHibernate
 *
 * @author aalencarvz1
 * @version 1.0.0
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "sso.database", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(DatabaseProperties.class)
public class FlywayAfterHibernate {

    private static final Logger logger = LoggerFactory.getLogger(FlywayAfterHibernate.class);

    /**
     * the properties
     */
    private final DatabaseProperties properties;

    /**
     * default constructor
     *
     * @param properties the properties
     */
    public FlywayAfterHibernate(DatabaseProperties properties) {
        logger.debug("INIT {}.{}", this.getClass().getSimpleName(), "FlywayAfterHibernate");
        this.properties = properties;
        logger.debug("END {}.{}", this.getClass().getSimpleName(), "FlywayAfterHibernate");
    }

    /**
     * flyway load
     *
     * @return the flyway instance
     */
    @Bean
    @ConditionalOnMissingBean(name = "ssoFlyway")
    public Flyway ssoFlyway() {
        logger.debug("INIT {}.{} {}", this.getClass().getSimpleName(), "ssoFlyway", properties.getFlyway().getLocations());
        Flyway result = null;
        try {
            result = Flyway.configure()
                    .dataSource(
                            properties.getDatasource().getJdbcUrl(),
                            properties.getDatasource().getUsername(),
                            properties.getDatasource().getPassword()
                    )
                    .locations(properties.getFlyway().getLocations())
                    .baselineOnMigrate(properties.getFlyway().isBaselineOnMigrate())
                    .load();
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.debug("END {}.{}", this.getClass().getSimpleName(), "ssoFlyway");
        return result;
    }

    /**
     * run flyway migration
     *
     * @param flyway the flyway instance
     * @return the runner
     */
    @Bean
    @ConditionalOnMissingBean(name = "ssoRunFlywayAfterHibernate")
    public ApplicationRunner ssoRunFlywayAfterHibernate(Flyway flyway) {
        logger.debug("INIT {}.{}", this.getClass().getSimpleName(), "ssoRunFlywayAfterHibernate");
        ApplicationRunner result = null;
        try {
            result = args -> {
                logger.debug("INIT {}.{}", this.getClass().getSimpleName(), "ssoRunFlywayAfterHibernate.ApplicationRunner");
                try {
                    flyway.migrate();
                    logger.debug("Flyway migrations executed after Hibernate initialization.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                logger.debug("END {}.{}", this.getClass().getSimpleName(), "ssoRunFlywayAfterHibernate.ApplicationRunner");
            };
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.debug("END {}.{}", this.getClass().getSimpleName(), "ssoRunFlywayAfterHibernate");
        return result;
    }
}