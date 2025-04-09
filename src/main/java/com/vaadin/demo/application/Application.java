package com.vaadin.demo.application;

import com.vaadin.demo.application.data.Raffle;
import com.vaadin.demo.application.repository.RaffleRepository;
import com.vaadin.demo.application.repository.SamplePersonRepository;
import com.vaadin.demo.application.security.properties.KeycloakProperties;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import javax.sql.DataSource;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.sql.init.SqlDataSourceScriptDatabaseInitializer;
import org.springframework.boot.autoconfigure.sql.init.SqlInitializationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;

/**
 * The entry point of the Spring Boot application.
 * <p>
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 *
 */
@SpringBootApplication
@Theme(value = "jug-vienna-raffle")
@ConfigurationPropertiesScan(basePackageClasses = KeycloakProperties.class)
public class Application implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    @Bean
    SqlDataSourceScriptDatabaseInitializer dataSourceScriptDatabaseInitializer(DataSource dataSource,
            SqlInitializationProperties properties, SamplePersonRepository repository) {
        // This bean ensures the database is only initialized when empty
        return new SqlDataSourceScriptDatabaseInitializer(dataSource, properties) {
            @Override
            public boolean initializeDatabase() {
                if (repository.count() == 0L) {
                    return super.initializeDatabase();
                }

                return false;
            }
        };
    }

    @Bean
    CommandLineRunner initDatabase(RaffleRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                repository.save(createRaffle("305897255"));
                repository.save(createRaffle("305897281"));
                repository.save(createRaffle("306898838"));
            }
        };
    }

    private Raffle createRaffle(String meetup_event_id) {
        Raffle raffle = new Raffle();
        raffle.setMeetup_event_id(meetup_event_id);
        return raffle;
    }
}
