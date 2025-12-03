package com.sysnormal.starters.security.sso.spring.sso_starter.properties.database;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Arrays;
import java.util.Properties;


/**
 * database properties
 *
 * @author aalencarvz1
 * @version 1.0.0
 */
@ConfigurationProperties(prefix = "sso.database")
@Getter
@Setter
public class DatabaseProperties {

    private boolean enabled = true;
    private Datasource datasource = new Datasource();
    private Jpa jpa = new Jpa();
    private Flyway flyway = new Flyway();


    @Getter
    @Setter
    public static class Datasource {
        private String jdbcUrl = "jdbc:mysql://127.0.0.1:3306/my_sso";
        private String username = "root";
        private String password = "masterkey";
        private String driverClassName = "com.mysql.cj.jdbc.Driver";
    }

    @Getter
    @Setter
    public static class Flyway {
        private boolean enabled = false;
        private String[] locations = {"classpath:com.sysnormal.starters.security.sso.spring.sso_starter.properties.database.migrations"};
        private boolean baselineOnMigrate = true;


        /**
         * Ensures the library's default migration path is always present.
         * If the user sets custom locations, the default one will be appended if missing.
         */
        public void setLocations(String[] locations) {
            // Define the default location
            String defaultLocation = "classpath:com/sysnormal/starters/security/sso/spring/sso_starter/database/migrations";

            // Se o usuário não passou nada, mantém o padrão
            if (locations == null || locations.length == 0) {
                this.locations = new String[]{defaultLocation};
                return;
            }

            // Verifica se o default já existe (comparando de forma case-insensitive)
            boolean containsDefault = Arrays.stream(locations)
                    .anyMatch(loc -> loc.equalsIgnoreCase(defaultLocation));

            // Se não existe, adiciona
            if (!containsDefault) {
                String[] merged = Arrays.copyOf(locations, locations.length + 1);
                merged[locations.length] = defaultLocation;
                this.locations = merged;
            } else {
                this.locations = locations;
            }
        }
    }



    @Getter
    @Setter
    public static class Jpa {
        private Hibernate hibernate = new Hibernate();
        private Properties properties = new Properties();

        @Getter
        @Setter
        public static class Hibernate {
            private String ddlAuto = "none";
            private String dialect = "org.hibernate.dialect.MySQLDialect";
            private boolean globallyQuotedIdentifiers = true;
        }
    }


}