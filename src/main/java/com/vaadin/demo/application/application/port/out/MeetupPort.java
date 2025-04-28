package com.vaadin.demo.application.application.port.out;

import com.vaadin.demo.application.domain.model.EventRecord;
import com.vaadin.demo.application.domain.model.ParticipantRecord;

import java.util.List;
import java.util.Optional;

/**
 * Port for Meetup service operations
 * This defines the interface for interacting with Meetup-related functionality
 */
public interface MeetupPort {

    /**
     * Get an event by its Meetup ID
     */
    Optional<EventRecord> getEventByMeetupId(String meetupId);

    /**
     * Get all events
     */
    List<EventRecord> getAllEvents();

    /**
     * Get all participants for an event
     */
    List<ParticipantRecord> getParticipantsForEvent(EventRecord event);

    /**
     * Get raffle-eligible participants for an event
     */
    List<ParticipantRecord> getRaffleEligibleParticipants(EventRecord event);

    /**
     * Import a Meetup event by ID
     * This will fetch event data from external service and store it locally
     */
    EventRecord importEvent(String meetupId);

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
    ParticipantRecord markParticipantEnteredRaffle(Long participantId);

    /**
     * Mark a participant as attended and having entered the raffle
     */
    ParticipantRecord markParticipantAttendedAndEnteredRaffle(Long participantId);

    /**
     * Mark a participant as no-show and having entered the raffle
     */
    ParticipantRecord markParticipantNoShowAndEnteredRaffle(Long participantId);

    /**
     * Reset raffle entry status for all participants of an event
     */
    void resetRaffleEntryForEvent(EventRecord event);

    /**
     * Get an event by ID (needed for UI form handling)
     */
    Optional<EventRecord> getEventById(Long id);

    /**
     * Save an event (needed for UI form handling)
     */
    EventRecord saveEvent(EventRecord event);

    /**
     * Get a participant by ID (needed for UI form handling)
     */
    Optional<ParticipantRecord> getParticipantById(Long id);

    /**
     * Mark a participant as not having entered the raffle
     */
    ParticipantRecord markParticipantNotEnteredRaffle(Long participantId);

    /**
     * Mark a participant as attended (without affecting raffle status)
     */
    ParticipantRecord markParticipantAttended(Long participantId);

    /**
     * Mark a participant as no-show (without affecting raffle status)
     */
    ParticipantRecord markParticipantNoShow(Long participantId);

    /**
     * Reset a participant's attendance status to unknown
     */
    ParticipantRecord resetParticipantAttendanceStatus(Long participantId);
}