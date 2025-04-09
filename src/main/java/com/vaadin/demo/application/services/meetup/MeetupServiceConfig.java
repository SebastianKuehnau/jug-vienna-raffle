package com.vaadin.demo.application.services.meetup;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

@Configuration
public class MeetupServiceConfig {

    @Bean
    @ConditionalOnProperty(name = "app.security.enabled", havingValue = "true", matchIfMissing = true)
    public MeetupService keycloakMeetupService(
            @Value("${keycloak.server-url}") String keycloakServerUrl,
            @Value("${keycloak.realm}") String keycloakRealm,
            OAuth2AuthorizedClientService authorizedClientService

    ) {
        return new KeycloakMeetupServiceImpl(keycloakServerUrl, keycloakRealm, authorizedClientService);
    }

    @Bean
    @ConditionalOnProperty(name = "app.security.enabled", havingValue = "false")
    public MeetupService devMeetupService() {
        return new DevMeetupServiceImpl();
    }
}