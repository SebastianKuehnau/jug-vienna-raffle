package com.vaadin.demo.application.application.service;

import com.vaadin.demo.application.domain.model.EventRecord;
import com.vaadin.demo.application.domain.model.ParticipantRecord;
import com.vaadin.demo.application.domain.port.MeetupPort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Application service that uses the MeetupPort
 * This service acts as a façade between the UI layer and the domain layer
 */
@Service
public class MeetupApplicationService {
    
    private final MeetupPort meetupPort;
    
    public MeetupApplicationService(MeetupPort meetupPort) {
        this.meetupPort = meetupPort;
    }
    
    /**
     * Get an event by its Meetup ID
     */
    public Optional<EventRecord> getEventByMeetupId(String meetupId) {
        return meetupPort.getEventByMeetupId(meetupId);
    }
    
    /**
     * Get all events
     */
    public List<EventRecord> getAllEvents() {
        return meetupPort.getAllEvents();
    }
    
    /**
     * Get all participants for an event
     */
    public List<ParticipantRecord> getParticipantsForEvent(EventRecord event) {
        return meetupPort.getParticipantsForEvent(event);
    }
    
    /**
     * Get raffle-eligible participants for an event
     */
    public List<ParticipantRecord> getRaffleEligibleParticipants(EventRecord event) {
        return meetupPort.getRaffleEligibleParticipants(event);
    }
    
    /**
     * Import a Meetup event by ID
     * This will fetch event data from external service and store it locally
     */
    public EventRecord importEvent(String meetupId) {
        return meetupPort.importEvent(meetupId);
    }
    
    /**
     * Sync members for an event from external service to local database
     */
    public int syncEventMembers(Long eventId) {
        return meetupPort.syncEventMembers(eventId);
    }
    
    /**
     * Sync members for an event by Meetup ID
     */
    public int syncEventMembersByMeetupId(String meetupId) {
        return meetupPort.syncEventMembersByMeetupId(meetupId);
    }
    
    /**
     * Mark a participant as having entered the raffle
     */
    public ParticipantRecord markParticipantEnteredRaffle(Long participantId) {
        return meetupPort.markParticipantEnteredRaffle(participantId);
    }
    
    /**
     * Mark a participant as attended and having entered the raffle
     */
    public ParticipantRecord markParticipantAttendedAndEnteredRaffle(Long participantId) {
        return meetupPort.markParticipantAttendedAndEnteredRaffle(participantId);
    }
    
    /**
     * Mark a participant as no-show and having entered the raffle
     */
    public ParticipantRecord markParticipantNoShowAndEnteredRaffle(Long participantId) {
        return meetupPort.markParticipantNoShowAndEnteredRaffle(participantId);
    }
    
    /**
     * Reset raffle entry status for all participants of an event
     */
    public void resetRaffleEntryForEvent(EventRecord event) {
        meetupPort.resetRaffleEntryForEvent(event);
    }
}