package com.vaadin.demo.application.security.dev;

import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

@Configuration
@ConditionalOnProperty(name = "app.security.enabled", havingValue = "false")
@Order(99)
public class DevSecurityConfiguration extends VaadinWebSecurity {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        // VaadinWebSecurity Basiskonfiguration beibehalten
//        super.configure(http);
//
//        // Alle Anfragen erlauben
//        http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
//
//        // OAuth2 und OIDC komplett deaktivieren
//        http.oauth2Login(AbstractHttpConfigurer::disable)
//                .oauth2Client(AbstractHttpConfigurer::disable)
//                .oidcLogout(AbstractHttpConfigurer::disable);
//
//        // CSRF deaktivieren für einfachere Tests
//        http.csrf(csrf -> csrf.disable());

        http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .csrf(csrf -> csrf.disable())
                .oauth2Login(AbstractHttpConfigurer::disable)
                .oauth2Client(AbstractHttpConfigurer::disable)
                .oidcLogout(AbstractHttpConfigurer::disable);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
    }
}
