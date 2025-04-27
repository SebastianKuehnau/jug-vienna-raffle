package com.vaadin.demo.application.application.service;

import com.vaadin.demo.application.domain.model.*;
import com.vaadin.demo.application.domain.port.MeetupPort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    
    // ===== Form Record methods for UI layer =====
    
    /**
     * Get an event form record by ID
     */
    public Optional<EventFormRecord> getEventFormById(Long id) {
        return meetupPort.getEventById(id)
                .map(EventFormRecord::fromEventRecord);
    }
    
    /**
     * Get all events as form records
     */
    public List<EventFormRecord> getAllEventForms() {
        return meetupPort.getAllEvents().stream()
                .map(EventFormRecord::fromEventRecord)
                .collect(Collectors.toList());
    }
    
    /**
     * Save an event form
     */
    public EventFormRecord saveEventForm(EventFormRecord eventForm) {
        EventRecord eventRecord = eventForm.toEventRecord();
        EventRecord savedEvent = meetupPort.saveEvent(eventRecord);
        return EventFormRecord.fromEventRecord(savedEvent);
    }
    
    /**
     * Get participants for an event as form records
     */
    public List<ParticipantFormRecord> getParticipantFormsForEvent(Long eventId) {
        return meetupPort.getEventById(eventId)
                .map(event -> meetupPort.getParticipantsForEvent(event).stream()
                        .map(ParticipantFormRecord::fromParticipantRecord)
                        .collect(Collectors.toList()))
                .orElse(List.of());
    }
    
    /**
     * Get a participant form record by ID
     */
    public Optional<ParticipantFormRecord> getParticipantFormById(Long id) {
        return meetupPort.getParticipantById(id)
                .map(ParticipantFormRecord::fromParticipantRecord);
    }
    
    /**
     * Update participant raffle status
     */
    public ParticipantFormRecord updateParticipantRaffleStatus(Long participantId, boolean hasEnteredRaffle) {
        ParticipantRecord updatedParticipant = hasEnteredRaffle ? 
                meetupPort.markParticipantEnteredRaffle(participantId) :
                meetupPort.markParticipantNotEnteredRaffle(participantId);
        return ParticipantFormRecord.fromParticipantRecord(updatedParticipant);
    }
    
    /**
     * Update participant attendance status
     */
    public ParticipantFormRecord updateParticipantAttendanceStatus(Long participantId, String attendanceStatus) {
        ParticipantRecord updatedParticipant;
        if ("ATTENDED".equals(attendanceStatus)) {
            updatedParticipant = meetupPort.markParticipantAttended(participantId);
        } else if ("NO_SHOW".equals(attendanceStatus)) {
            updatedParticipant = meetupPort.markParticipantNoShow(participantId);
        } else {
            updatedParticipant = meetupPort.resetParticipantAttendanceStatus(participantId);
        }
        return ParticipantFormRecord.fromParticipantRecord(updatedParticipant);
    }
}