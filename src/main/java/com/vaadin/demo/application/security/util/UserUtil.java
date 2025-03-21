package com.vaadin.demo.application.security.util;

import com.vaadin.demo.application.security.data.KeycloakUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class UserUtil {
    public static Optional<KeycloakUser> getCurrentUser() {
        return Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getPrincipal)
                .map(KeycloakUser.class::cast);
    }
}
