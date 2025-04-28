package com.vaadin.demo.application.adapter.meetupclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.vaadin.demo.application.adapter.meetupclient.impl.MeetupEventWithRsvpWrapper;
import com.vaadin.demo.application.adapter.meetupclient.impl.GetEventWrapper;
import com.vaadin.demo.application.adapter.meetupclient.impl.MeetupEventsWrapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.graphql.client.HttpSyncGraphQlClient;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.client.RestClient;

import java.util.Optional;
import java.util.Set;

public class KeycloakMeetupClientImpl implements MeetupClient {

  private final HttpSyncGraphQlClient httpSyncGraphQlClient;
  private RestClient restClient;
  private final OAuth2AuthorizedClientService authorizedClientService;

  public KeycloakMeetupClientImpl(
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




  @Override
  public Optional<MeetupEvent> getEvent(String meetupEventId) {
    var query = "query { event(id:\"" + meetupEventId + "\") { id dateTime title description eventType eventUrl status token }  }";
    var body =   queryNew(query);
    try {
      ObjectMapper mapper = new ObjectMapper();
      mapper.registerModule(new JavaTimeModule());
      return Optional.ofNullable(GetEventWrapper.parse(body, mapper));

    } catch (RuntimeException e) {
      e.printStackTrace();;
      return Optional.empty();
    }
  }

  @Override
  public Optional<MeetupEventWithRSVPs> getEventWithRSVPs(String meetupEventId) {

    var query = "query { event(id:\"" + meetupEventId + "\") { id dateTime title description eventType eventUrl status token rsvps (first: 300) { edges { node { id isFirstEvent isHost member { id email gender memberUrl name state status username memberPhoto { baseUrl highResUrl id standardUrl thumbUrl }  }} }  }  }}";

    var body = queryNew(query);
    System.out.println(body);
    try {
      ObjectMapper mapper = new ObjectMapper();
      mapper.registerModule(new JavaTimeModule());
      return Optional.ofNullable(MeetupEventWithRsvpWrapper.parse(body, mapper));

    } catch (RuntimeException e) {
      return Optional.empty();
    }
  }

  @Override
  public Set<MeetupEvent> getEvents() {
    var query = "query { groupByUrlname (urlname: \"java-vienna\") { id events { edges { node { id token title dateTime description eventType eventUrl status }} }  } }";

    var body = queryNew(query);
    try {
      ObjectMapper mapper = new ObjectMapper();
      mapper.registerModule(new JavaTimeModule());
      MeetupEventsWrapper wrapper = mapper.readValue(body, MeetupEventsWrapper.class);
      return wrapper.getEvents();
    } catch (JsonProcessingException e) {
      return Set.of();
    }
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
  public String legacyQuery(String query) {
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

  @Override
  public String queryNew(String query) {
    String token = getAccessToken();
    RestClient authClient = this.restClient.mutate()
        .defaultHeader("Authorization", "Bearer " + token)
        .build();
    query = query.replaceAll("\r\n", " ").replaceAll("\n", " ");

    return authClient
        .post()
        .uri("/meetup-proxy/gql-ext")
        .body(new GraphQLQuery(query))
        .retrieve()
        .body(String.class);
  }
}
