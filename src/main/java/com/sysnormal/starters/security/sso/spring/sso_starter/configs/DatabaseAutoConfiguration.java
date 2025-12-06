package com.sysnormal.starters.security.sso.spring.sso_starter.configs;

import com.sysnormal.starters.security.sso.spring.sso_starter.database.entities.sso.User;
import com.sysnormal.starters.security.sso.spring.sso_starter.database.repositories.sso.UsersRepository;
import com.sysnormal.starters.security.sso.spring.sso_starter.properties.database.DatabaseProperties;
import com.sysnormal.libs.utils.Constants;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * DatabaseAutoConfiguration
 *
 * @author aalencarvz1
 * @version 1.0.0
 */
@Configuration(proxyBeanMethods = false)
@EnableJpaRepositories(
        basePackageClasses = UsersRepository.class,
        entityManagerFactoryRef = DatabaseAutoConfiguration.entityManagerFactoryQualifier,
        transactionManagerRef = DatabaseAutoConfiguration.transactionManagerQualifier
)
@ConditionalOnProperty(prefix = "sso.database", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(DatabaseProperties.class)
public class DatabaseAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseAutoConfiguration.class);

    public static final String datasourceName = "sso";
    public static final String datasourcePrefix = Constants.Spring.DATASOURCE.PROPERTY + "." + datasourceName;
    public static final String persistenceUnitName = datasourceName;
    public static final String entityManagerFactoryQualifier = datasourceName +  Constants.Jakarta.PERSISTENCE.ENTITYMANAGERFACTORY.CLASSNAME;
    public static final String transactionManagerQualifier = datasourceName + Constants.SpringFramework.TRANSACTION.TRANSACTIONMANAGER.CLASSNAME;

    /**
     * properties
     */
    private final DatabaseProperties properties;

    /**
     * default constructor
     * @param properties the properties
     */
    public DatabaseAutoConfiguration(DatabaseProperties properties) {
        logger.debug("INIT {}.{}", this.getClass().getSimpleName(), "DatabaseAutoConfiguration");
        this.properties = properties;
        logger.debug("END {}.{}", this.getClass().getSimpleName(), "DatabaseAutoConfiguration");
    }

    /**
     * datasource
     * @return the datasource
     */
    @Bean
    @Primary
    public DataSource ssoDataSource() {
        logger.debug("INIT {}.{}", this.getClass().getSimpleName(), "ssoDataSource");
        DataSource result = null;
        try {
            result = org.springframework.boot.jdbc.DataSourceBuilder.create()
                    .url(properties.getDatasource().getJdbcUrl())
                    .username(properties.getDatasource().getUsername())
                    .password(properties.getDatasource().getPassword())
                    .driverClassName(properties.getDatasource().getDriverClassName())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.debug("END {}.{}", this.getClass().getSimpleName(), "ssoDataSource");
        return result;
    }

    /**
     * entity manager factory
     *
     * @param builder the builder
     * @return the factory bean
     */
    @Bean
    @Primary
    @ConditionalOnMissingBean(name = entityManagerFactoryQualifier)
    public LocalContainerEntityManagerFactoryBean ssoEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        logger.debug("INIT {}.{}", this.getClass().getSimpleName(), entityManagerFactoryQualifier);
        LocalContainerEntityManagerFactoryBean result = null;
        try {
            Map<String, Object> jpaProps = new HashMap<>();
            jpaProps.put(org.hibernate.cfg.AvailableSettings.HBM2DDL_AUTO, properties.getJpa().getHibernate().getDdlAuto());
            jpaProps.put(Constants.Hibernate.DIALECT.PROPERTY, properties.getJpa().getHibernate().getDialect());
            //jpaProps.put("properties", properties.getJpa().getProperties());
            jpaProps.put(Constants.Hibernate.GLOBALLY_QUOTED_IDENTIFIERS.PROPERTY, properties.getJpa().getHibernate().isGloballyQuotedIdentifiers());
            result = builder
                    .dataSource(ssoDataSource())
                    .packages(User.class)
                    .persistenceUnit(persistenceUnitName)
                    .properties(jpaProps)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.debug("END {}.{}", this.getClass().getSimpleName(), entityManagerFactoryQualifier);
        return result;
    }

    /**
     * transaction manager
     *
     * @param ssoEntityManagerFactory the factory
     * @return the transaction manager
     */
    @Bean
    @Primary
    @ConditionalOnMissingBean(name = transactionManagerQualifier)
    public PlatformTransactionManager ssoTransactionManager(@Qualifier(entityManagerFactoryQualifier) EntityManagerFactory ssoEntityManagerFactory) {
        logger.debug("INIT {}.{}", this.getClass().getSimpleName(), transactionManagerQualifier);
        JpaTransactionManager result = null;
        try {
            result = new JpaTransactionManager(ssoEntityManagerFactory);
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.debug("END {}.{}", this.getClass().getSimpleName(), transactionManagerQualifier);
        return result;
    }
}