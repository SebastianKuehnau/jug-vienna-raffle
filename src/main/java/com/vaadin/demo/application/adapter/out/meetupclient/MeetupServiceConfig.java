package com.vaadin.demo.application.adapter.out.meetupclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;

@Configuration
public class MeetupServiceConfig {

    @Bean
    @ConditionalOnProperty(name = "app.security.enabled", havingValue = "true", matchIfMissing = true)
    public MeetupAPIClient keycloakMeetupService(
            @Value("${keycloak.server-url}") String keycloakServerUrl,
            @Value("${keycloak.realm}") String keycloakRealm,
            OAuth2AuthorizedClientService authorizedClientService

    ) {
        return new KeycloakMeetupAPIClientImpl(keycloakServerUrl, keycloakRealm, authorizedClientService);
    }

    @Bean
    @ConditionalOnProperty(name = "app.security.enabled", havingValue = "false")
    public MeetupAPIClient devMeetupService() {
        return new DevMeetupAPIClientImpl();
    }
}