package com.vaadin.demo.application.security.util;

import com.vaadin.demo.application.security.data.KeycloakUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class UserUtil {
    public static Optional<KeycloakUser> getCurrentUser() {

        try {
            return Optional.ofNullable(SecurityContextHolder.getContext())
                    .map(SecurityContext::getAuthentication)
                    .filter(Authentication::isAuthenticated)
                    .map(Authentication::getPrincipal)
                    .map(KeycloakUser.class::cast);
        } catch (ClassCastException e) {

        }
        return Optional.ofNullable(createMockUser());
    }

    private static KeycloakUser createMockUser() {
        KeycloakUser user = new KeycloakUser();
        user.setId("mock-id-123");
        user.setEmail("dev@example.com");
        user.setFirstName("Dev");
        user.setLastName("User");
        user.setLocale("en");
        user.setRoles(List.of("ROLE_DEV", "ROLE_USER"));
        user.setAuthorities(List.of(
                (GrantedAuthority) () -> "ROLE_DEV",
                (GrantedAuthority) () -> "ROLE_USER"
        ));
        user.setSessionId(UUID.randomUUID().toString());
        user.setSubject("mock-subject");
        user.setClaims(Map.of("preferred_username", "devuser"));
        return user;
    }
}
