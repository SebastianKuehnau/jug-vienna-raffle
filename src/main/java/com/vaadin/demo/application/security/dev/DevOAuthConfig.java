package com.vaadin.demo.application.security.dev;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

@Configuration
@ConditionalOnProperty(name = "app.security.enabled", havingValue = "false")
public class DevOAuthConfig {

    @Bean
    @Primary
    public ClientRegistrationRepository clientRegistrationRepository() {
        // Mock-ClientRegistration erstellen
        ClientRegistration mockRegistration = ClientRegistration
                .withRegistrationId("mock-oauth2")
                .clientId("mock-client-id")
                .clientSecret("mock-secret")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                .scope("read", "write")
                .authorizationUri("https://example.com/oauth2/authorize")
                .tokenUri("https://example.com/oauth2/token")
                .userInfoUri("https://example.com/oauth2/userinfo")
                .userNameAttributeName("sub")
                .clientName("Mock OAuth2 Client")
                .build();

        // Eigenen ClientRegistrationRepository zurückgeben
        return new ClientRegistrationRepository() {
            @Override
            public ClientRegistration findByRegistrationId(String registrationId) {
                return "mock-oauth2".equals(registrationId) ? mockRegistration : null;
            }
        };
    }
}