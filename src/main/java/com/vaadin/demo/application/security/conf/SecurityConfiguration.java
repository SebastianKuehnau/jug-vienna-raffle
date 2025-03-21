package com.vaadin.demo.application.security.conf;


import com.vaadin.demo.application.security.properties.KeycloakProperties;
import com.vaadin.demo.application.security.service.KeycloakOAuth2UserService;
import com.vaadin.flow.spring.security.VaadinSavedRequestAwareAuthenticationSuccessHandler;
import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.annotation.web.configurers.RememberMeConfigurer;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class SecurityConfiguration extends VaadinWebSecurity {

    private final KeycloakProperties keycloakProperties;
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final KeycloakOAuth2UserService keycloakOAuth2UserService;

    public SecurityConfiguration(KeycloakProperties keycloakProperties,
                                 ClientRegistrationRepository clientRegistrationRepository,
                                 KeycloakOAuth2UserService keycloakOAuth2UserService) {
        this.keycloakProperties = keycloakProperties;
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.keycloakOAuth2UserService = keycloakOAuth2UserService;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        // Customize your WebSecurity configuration.
        super.configure(web);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        /*
         Configure your static resources with public access before calling
         super.configure(HttpSecurity) as it adds final anyRequest matcher
        */
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/public/**").permitAll()
                .requestMatchers("/**").authenticated()
        );

        super.configure(http);

        // disable spring security features which are not required
        http.httpBasic(HttpBasicConfigurer::disable)
                .formLogin(FormLoginConfigurer::disable)
                .rememberMe(RememberMeConfigurer::disable);

        http.oauth2Login(oauth -> oauth.userInfoEndpoint(userInfo -> userInfo.oidcUserService(keycloakOAuth2UserService::loadUserFromRequest))
                        .successHandler(new VaadinSavedRequestAwareAuthenticationSuccessHandler())
                        .defaultSuccessUrl(keycloakProperties.appBaseUrl(), true)
                        .failureUrl(keycloakProperties.appBaseUrl())
                        .permitAll())
                .exceptionHandling(exp -> exp.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint(keycloakProperties.loginUrl())))
                .logout(logout -> logout.logoutSuccessHandler(this.logoutSuccessHandler())
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                ).oidcLogout(logout -> logout.backChannel(Customizer.withDefaults()));
    }

    private OidcClientInitiatedLogoutSuccessHandler logoutSuccessHandler() {
        final var logoutSuccessHandler = new OidcClientInitiatedLogoutSuccessHandler(this.clientRegistrationRepository);
        logoutSuccessHandler.setRedirectStrategy(new VaadinRedirectStrategy());
        logoutSuccessHandler.setPostLogoutRedirectUri(keycloakProperties.appBaseUrl());
        return logoutSuccessHandler;
    }
}
