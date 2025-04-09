package com.vaadin.demo.application.services.meetup;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.Set;

public interface MeetupService {

    Optional<MeetupEvent> getEvent(String meetupEventId);

    record MeetupEvent(String id, String token, String title, OffsetDateTime dateTime, String description) {
    }

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

    MeResponse getMe() ;

    String getMeInGroup() ;

    String getMyGroups() ;
}
