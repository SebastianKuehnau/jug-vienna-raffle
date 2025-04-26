package com.vaadin.demo.application.domain.model;

import com.vaadin.demo.application.data.MeetupEvent;
import com.vaadin.demo.application.data.Member;
import com.vaadin.demo.application.data.Participant;
import com.vaadin.demo.application.data.Prize;
import com.vaadin.demo.application.data.Raffle;
// Import Java class for API models
import com.vaadin.demo.application.services.meetup.MeetupService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper utility class to convert between JPA entities and domain records
 */
public class Mapper {

    /**
     * Convert from API MeetupEvent to domain EventRecord 
     */
    public static EventRecord toEventRecord(MeetupService.MeetupEvent apiEvent) {
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
            entity.getEmail()
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
            ParticipantRecord.fromJpaRsvpStatus(entity.getRsvpStatus()),
            ParticipantRecord.fromJpaAttendanceStatus(entity.getAttendanceStatus())
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
            toParticipantRecord(entity.getWinner()),
            toRaffleRecord(entity.getRaffle(), false) // Avoid infinite recursion
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
    
    // Methods to convert from domain records to JPA entities would be added here
    // For completeness, but they're not shown to keep this focused
}