package com.vaadin.demo.application.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.graphql.client.HttpSyncGraphQlClient;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class MeetupService {
  private final HttpSyncGraphQlClient httpSyncGraphQlClient;
  private RestClient restClient;
  private final OAuth2AuthorizedClientService authorizedClientService;

  public MeetupService(
      @Value("${keycloak.server-url}") String keycloakServerUrl,
      @Value("${keycloak.realm}") String keycloakRealm,
      OAuth2AuthorizedClientService authorizedClientService
  ) {
    this.authorizedClientService = authorizedClientService;

    String baseUrl = keycloakServerUrl + "/realms/" + keycloakRealm;
    this.restClient = RestClient.create(baseUrl);
    this.httpSyncGraphQlClient = HttpSyncGraphQlClient.create(this.restClient.mutate()
        .baseUrl(baseUrl + "/meetup-proxy/gql")
        .build());
  }

  private String getAccessToken() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    if (auth instanceof OAuth2AuthenticationToken oauth2Token) {
      String clientRegistrationId = oauth2Token.getAuthorizedClientRegistrationId();
      OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
          clientRegistrationId, oauth2Token.getName());

      if (client != null && client.getAccessToken() != null) {
        return client.getAccessToken().getTokenValue();
      }
    }

    throw new IllegalStateException("User is not authenticated with OAuth2 or token is missing.");
  }


  public MeResponse getMe() {
    /*
    entspricht query:
    query { self { id email name memberUrl memberPhotoUrl } }
     */
    String token = getAccessToken();

    RestClient authClient = this.restClient.mutate()
        .defaultHeader("Authorization", "Bearer " + token)
        .build();

    return authClient
        .get()
        .uri("/meetup-proxy/me")
        .retrieve()
        .body(MeResponse.class);
  }

  public String getMeInGroup() {
    /*
    entspricht
          return new GraphQLQuery(
          String.format(
              "query { groupByUrlname(urlname: \"%s\") { id isMember isOrganizer isPrimaryOrganizer urlname } self { id email name memberUrl memberPhotoUrl } }",
              groupUrlName));
     */
    String token = getAccessToken();

    RestClient authClient = this.restClient.mutate()
        .defaultHeader("Authorization", "Bearer " + token)
        .build();

    return authClient
        .get()
        .uri("/meetup-proxy/me-in-group")
        .retrieve()
        .body(String.class);
  }

  public String getMyGroups() {
    /*

          return new GraphQLQuery(
          "query { self { id memberships(filter: { status: [ACTIVE, LEADER], groupStatus: [PAID] }) { edges { node { id name urlname description isMember isOrganizer isPrimaryOrganizer membershipMetadata { preferences { name value booleanValue } } } } } } }"
      );

     */
    String token = getAccessToken();

    RestClient authClient = this.restClient.mutate()
        .defaultHeader("Authorization", "Bearer " + token)
        .build();

    return authClient
        .get()
        .uri("/meetup-proxy/my-groups")
        .retrieve()
        .body(String.class);
  }


  public class GraphQLQuery {

    private String query;

    public GraphQLQuery(String query) {
      this.query = query;
    }

    public String getQuery() {
      return query;
    }
  }



  // diese Methode kommt in Zukunft weg, jetzt fuer entwicklung ok.
  public String query(String query) {
    String token = getAccessToken();

    RestClient authClient = this.restClient.mutate()
        .defaultHeader("Authorization", "Bearer " + token)
        .build();

    query = query.replaceAll("\r\n", " ").replaceAll("\n", " ");

    return authClient
        .post()
        .uri("/meetup-proxy/gql")
        .body(new GraphQLQuery(query))
        .retrieve()
        .body(String.class);
  }


  public record MeResponse(MeData data) {}

  public record MeData(
      GroupByUrlname groupByUrlname,
      Self self
  ) {}

  public record GroupByUrlname(
      String id,
      boolean isMember,
      boolean isOrganizer,
      boolean isPrimaryOrganizer,
      String urlname
  ) {}

  public record Self(
      String id,
      String email,
      String name,
      String memberUrl,
      String memberPhotoUrl
  ) {}

}
