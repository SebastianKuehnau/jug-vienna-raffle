package com.vaadin.demo.application.adapter.meetupclient;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface MeetupClient {

    Optional<MeetupEvent> getEvent(String meetupEventId);

    Optional<MeetupEventWithRSVPs> getEventWithRSVPs(String meetupEventId);

    record MeetupEvent(String id,
                       String token,
                       String title,
                       OffsetDateTime dateTime,
                       String description,
                       String eventUrl,
                       String status,
                       Set<Member> members) {
    }

    record MemberPhoto(String id, String baseUrl, String highResUrl, String standardUrl, String thumbUrl) {}

    record RSVP(String id, String email, String gender, String memberUrl, String name, String state, String status, String username, MemberPhoto memberPhoto) { }

    record MeetupEventWithRSVPs(String id, String token, String title, OffsetDateTime dateTime, String description, List<RSVP> rsvps) { }

    Set<MeetupEvent> getEvents();

    record MeResponse(MeData data) {
    }

    record MeData(
            GroupByUrlname groupByUrlname,
            Self self
    ) {
    }

    record GroupByUrlname(
            String id,
            boolean isMember,
            boolean isOrganizer,
            boolean isPrimaryOrganizer,
            String urlname
    ) {
    }

    record Self(
            String id,
            String email,
            String name,
            String memberUrl,
            String memberPhotoUrl
    ) {
    }

    record Member(String id, String name, String email, String rsvp_id, Boolean isOrganizer, Boolean hasEnteredRaffle) {

    }

    MeResponse getMe() ;

    String getMeInGroup() ;

    String getMyGroups() ;

    // diese Methode kommt in Zukunft weg, jetzt fuer entwicklung ok.
    String legacyQuery(String query) ;

    String queryNew(String query) ;
}
