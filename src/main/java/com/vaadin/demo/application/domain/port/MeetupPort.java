package com.vaadin.demo.application.domain.port;

import com.vaadin.demo.application.data.MeetupEvent;
import com.vaadin.demo.application.data.Member;
import com.vaadin.demo.application.data.Participant;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Port for Meetup service operations
 * This defines the interface for interacting with Meetup-related functionality
 */
public interface MeetupPort {
    
    /**
     * Get an event by its Meetup ID
     */
    Optional<MeetupEvent> getEventByMeetupId(String meetupId);
    
    /**
     * Get all events
     */
    List<MeetupEvent> getAllEvents();
    
    /**
     * Get all participants for an event
     */
    List<Participant> getParticipantsForEvent(MeetupEvent event);
    
    /**
     * Get raffle-eligible participants for an event
     */
    List<Participant> getRaffleEligibleParticipants(MeetupEvent event);
    
    /**
     * Import a Meetup event by ID
     * This will fetch event data from external service and store it locally
     */
    MeetupEvent importEvent(String meetupId);
    
    /**
     * Sync members for an event from external service to local database
     */
    int syncEventMembers(Long eventId);
    
    /**
     * Sync members for an event by Meetup ID
     */
    int syncEventMembersByMeetupId(String meetupId);
    
    /**
     * Mark a participant as having entered the raffle
     */
    Participant markParticipantEnteredRaffle(Long participantId);
    
    /**
     * Mark a participant as attended and having entered the raffle
     */
    Participant markParticipantAttendedAndEnteredRaffle(Long participantId);
    
    /**
     * Mark a participant as no-show and having entered the raffle
     */
    Participant markParticipantNoShowAndEnteredRaffle(Long participantId);
    
    /**
     * Reset raffle entry status for all participants of an event
     */
    void resetRaffleEntryForEvent(MeetupEvent event);
}