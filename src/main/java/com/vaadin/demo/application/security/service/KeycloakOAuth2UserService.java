package com.vaadin.demo.application.security.service;

import com.vaadin.demo.application.security.data.KeycloakUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class KeycloakOAuth2UserService extends OidcUserService {

    public OidcUser loadUserFromRequest(final OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        final OidcUser oidcUser = super.loadUser(userRequest);

        final var realmRoles = this.findRoles(oidcUser.getAuthorities());
        final Collection<? extends GrantedAuthority> mappedAuthorities = realmRoles.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();

        final var user = new KeycloakUser();
        user.setId(oidcUser.getUserInfo().getSubject());
        user.setEmail(oidcUser.getEmail());
        user.setFirstName(oidcUser.getGivenName());
        user.setLastName(oidcUser.getFamilyName());
        user.setAuthorities(mappedAuthorities);
        user.setRoles(new HashSet<>(realmRoles));

        user.setIdToken(oidcUser.getIdToken());
        user.setSessionId(oidcUser.getAttribute("sid"));
        user.setSubject(oidcUser.getSubject());
        user.setIssuer(oidcUser.getIssuer());
        user.setClaims(oidcUser.getClaims());

        return user;
    }

    private List<String> findRoles(final Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
                .filter(OidcUserAuthority.class::isInstance)
                .map(OidcUserAuthority.class::cast)
                .findFirst()
                .map(this::extractRealmRoles)
                .orElseGet(Collections::emptyList)
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @SuppressWarnings("unchecked")
    private List<String> extractRealmRoles(final OidcUserAuthority oauthAuthority) {
        return Optional.ofNullable(oauthAuthority.getUserInfo().getClaimAsMap("realm_access"))
                .map(realm -> (Collection<String>) realm.get("roles"))
                .stream()
                .flatMap(Collection::stream)
                .toList();
    }
}
