package com.vaadin.demo.application.security.dev;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Configuration
@ConditionalOnProperty(name = "app.security.enabled", havingValue = "false")
public class DevOAuth2ServiceConfig {

    @Bean
    public OAuth2AuthorizedClientService authorizedClientService() {
        return new OAuth2AuthorizedClientService() {
            @Override
            public <T extends OAuth2AuthorizedClient> T loadAuthorizedClient(String clientRegistrationId, String principalName) {
                return null; // Keine gespeicherten Clients im Dev-Modus
            }

            @Override
            public void saveAuthorizedClient(OAuth2AuthorizedClient authorizedClient, Authentication principal) {
                // Nichts speichern im Dev-Modus

            }

            @Override
            public void removeAuthorizedClient(String clientRegistrationId, String principalName) {
                // Nichts zu entfernen im Dev-Modus
            }
        };
    }
}