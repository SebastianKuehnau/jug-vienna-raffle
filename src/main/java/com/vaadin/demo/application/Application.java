package com.vaadin.demo.application;

import com.vaadin.demo.application.data.Raffle;
import com.vaadin.demo.application.repository.RaffleRepository;
import com.vaadin.demo.application.repository.SamplePersonRepository;
import com.vaadin.demo.application.security.properties.KeycloakProperties;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
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
@Push
public class Application implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    // Removed automatic data initialization - now using HTTP endpoint
    // Use: curl -X POST http://localhost:8080/api/data/init
}
