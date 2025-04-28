package com.vaadin.demo.application.adapter;

import com.vaadin.demo.application.adapter.out.persistence.data.MeetupEvent;
import com.vaadin.demo.application.adapter.out.persistence.data.MeetupMember;
import com.vaadin.demo.application.adapter.out.persistence.data.Member;
import com.vaadin.demo.application.adapter.out.persistence.data.Participant;
import com.vaadin.demo.application.adapter.out.persistence.data.Prize;
import com.vaadin.demo.application.adapter.out.persistence.data.PrizeTemplate;
import com.vaadin.demo.application.adapter.out.persistence.data.Raffle;
// Import Java class for API models
import com.vaadin.demo.application.domain.model.EventRecord;
import com.vaadin.demo.application.domain.model.MemberRecord;
import com.vaadin.demo.application.domain.model.ParticipantRecord;
import com.vaadin.demo.application.domain.model.ParticipantRecord.AttendanceStatus;
import com.vaadin.demo.application.domain.model.ParticipantRecord.RsvpStatus;
import com.vaadin.demo.application.domain.model.PrizeRecord;
import com.vaadin.demo.application.domain.model.PrizeTemplateRecord;
import com.vaadin.demo.application.domain.model.RaffleRecord;
import com.vaadin.demo.application.adapter.out.meetupclient.MeetupClient;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper utility class to convert between JPA entities and domain records
 */
public class Mapper {

    /**
     * Convert from API MeetupEvent to domain EventRecord
     */
    public static EventRecord toEventRecord(MeetupClient.MeetupEvent apiEvent) {
        if (apiEvent == null) return null;

        return new EventRecord(
            null, // No ID since this is coming from API
            apiEvent.id(),
            apiEvent.title(),
            apiEvent.description(),
            apiEvent.dateTime(),
            null, // No venue in API model
            apiEvent.eventUrl()
        );
    }

    /**
     * Convert JPA Member to domain MemberRecord
     */
    public static MemberRecord toMemberRecord(Member entity) {
        if (entity == null) return null;

        return new MemberRecord(
            entity.getId(),
            entity.getMeetupId(),
            entity.getName(),
            entity.getEmail(),
            entity.getLastUpdated()
        );
    }

    /**
     * Convert JPA MeetupEvent to domain EventRecord
     */
    public static EventRecord toEventRecord(MeetupEvent entity) {
        if (entity == null) return null;

        return new EventRecord(
            entity.getId(),
            entity.getMeetupId(),
            entity.getTitle(),
            entity.getDescription(),
            entity.getDateTime(),
            null, // venue is not in the entity
            entity.getEventUrl()
        );
    }

    /**
     * Convert JPA Participant to domain ParticipantRecord
     */
    public static ParticipantRecord toParticipantRecord(Participant entity) {
        if (entity == null) return null;

        return new ParticipantRecord(
            entity.getId(),
            toMemberRecord(entity.getMember()),
            toEventRecord(entity.getMeetupEvent()),
            entity.getRsvpId(),
            Boolean.TRUE.equals(entity.getIsOrganizer()),
            Boolean.TRUE.equals(entity.getHasEnteredRaffle()),
            fromJpaRsvpStatus(entity.getRsvpStatus()),
            fromJpaAttendanceStatus(entity.getAttendanceStatus())
        );
    }

    /**
     * Convert JPA Prize to domain PrizeRecord
     */
    public static PrizeRecord toPrizeRecord(Prize entity) {
        if (entity == null) return null;

        return new PrizeRecord(
            entity.getId(),
            entity.getName(),
            entity.getDescription(),
            entity.getTemplateText(),
            toParticipantRecord(entity.getWinner()),
            toRaffleRecord(entity.getRaffle(), false), // Avoid infinite recursion
            entity.getVoucherCode(),
            entity.getValidUntil()
        );
    }

    /**
     * Convert JPA PrizeTemplate to domain PrizeTemplateRecord
     */
    public static PrizeTemplateRecord toPrizeTemplateRecord(PrizeTemplate entity) {
        if (entity == null) return null;

        return new PrizeTemplateRecord(
            entity.getId(),
            entity.getName(),
            entity.getDescription(),
            entity.getTemplateText(),
            entity.getVoucherCode(),
            entity.getValidUntil()
        );
    }

    /**
     * Convert JPA Raffle to domain RaffleRecord
     *
     * @param includePrizes whether to include the prizes (to avoid recursion)
     */
    public static RaffleRecord toRaffleRecord(Raffle entity, boolean includePrizes) {
        if (entity == null) return null;

        List<PrizeRecord> prizes = includePrizes && entity.getPrizes() != null ?
            entity.getPrizes().stream()
                .map(Mapper::toPrizeRecord)
                .collect(Collectors.toList()) :
            List.of();

        return new RaffleRecord(
            entity.getId(),
            toEventRecord(entity.getEvent()),
            entity.getMeetup_event_id(),
            prizes
        );
    }

    /**
     * Convert JPA Raffle to domain RaffleRecord including prizes
     */
    public static RaffleRecord toRaffleRecord(Raffle entity) {
        return toRaffleRecord(entity, true);
    }

    /**
     * Convert JPA AttendanceStatus to domain model AttendanceStatus
     */
    public static AttendanceStatus fromJpaAttendanceStatus(Participant.AttendanceStatus status) {
        if (status == null) return AttendanceStatus.UNKNOWN;

        return switch (status) {
            case ATTENDED -> AttendanceStatus.ATTENDED;
            case NO_SHOW -> AttendanceStatus.NO_SHOW;
            case UNKNOWN -> AttendanceStatus.UNKNOWN;
        };
    }

    /**
     * Convert domain model AttendanceStatus to JPA AttendanceStatus
     */
    public static Participant.AttendanceStatus toJpaAttendanceStatus(AttendanceStatus status) {
        if (status == null) return Participant.AttendanceStatus.UNKNOWN;

        return switch (status) {
            case ATTENDED -> Participant.AttendanceStatus.ATTENDED;
            case NO_SHOW -> Participant.AttendanceStatus.NO_SHOW;
            case UNKNOWN -> Participant.AttendanceStatus.UNKNOWN;
        };
    }

    /**
     * Convert JPA RsvpStatus to domain model RsvpStatus
     */
    public static RsvpStatus fromJpaRsvpStatus(Participant.RSVPStatus status) {
        if (status == null) return RsvpStatus.NO;

        return switch (status) {
            case YES -> RsvpStatus.YES;
            case NO -> RsvpStatus.NO;
        };
    }

    /**
     * Convert domain model RsvpStatus to JPA RsvpStatus
     */
    public static Participant.RSVPStatus toJpaRsvpStatus(RsvpStatus status) {
        if (status == null) return Participant.RSVPStatus.NO;

        return switch (status) {
            case YES -> Participant.RSVPStatus.YES;
            case NO -> Participant.RSVPStatus.NO;
        };
    }

    /**
     * Updates the member data from a Meetup API response
     */
    public static void updateFromApiResponse(MeetupMember meetupMember,
        MeetupClient.Member apiMember) {
        meetupMember.setMeetupId(apiMember.id());
        meetupMember.setName(apiMember.name());
        meetupMember.setEmail(apiMember.email());
        meetupMember.setRsvpId(apiMember.rsvp_id());
        meetupMember.setIsOrganizer(apiMember.isOrganizer());
        meetupMember.setHasEnteredRaffle(apiMember.hasEnteredRaffle());
        // Don't update RSVP status or attendance info as they might have been manually set
        meetupMember.setLastUpdated( OffsetDateTime.now() );
    }

    /**
     * Updates the member data from a Meetup API response
     */
    public static void updateFromApiResponse(Member member, MeetupClient.RSVP apiMember) {
        member.setName(apiMember.name());
        member.setEmail(apiMember.email());
        member.setLastUpdated(OffsetDateTime.now());
    }

    /**
     * Updates the event data from a Meetup API response
     */
    public static void updateFromApiResponse(MeetupEvent meetupEvent,
        MeetupClient.MeetupEvent apiEvent) {
        meetupEvent.setMeetupId(apiEvent.id());
        meetupEvent.setToken(apiEvent.token());
        meetupEvent.setTitle(apiEvent.title());
        meetupEvent.setDescription(apiEvent.description());
        meetupEvent.setDateTime(apiEvent.dateTime());
        meetupEvent.setEventUrl(apiEvent.eventUrl());
        meetupEvent.setStatus(apiEvent.status());
        meetupEvent.setLastUpdated(OffsetDateTime.now());
    }

    // Methods to convert from domain records to JPA entities would be added here
    // For completeness, but they're not shown to keep this focused
}