package com.vaadin.demo.application.services;

import com.vaadin.demo.application.data.MeetupEvent;
import com.vaadin.demo.application.data.Member;
import com.vaadin.demo.application.data.Participant;
import com.vaadin.demo.application.data.Participant.RSVPStatus;
import com.vaadin.demo.application.repository.MeetupEventRepository;
import com.vaadin.demo.application.repository.MemberRepository;
import com.vaadin.demo.application.repository.ParticipantRepository;
import com.vaadin.demo.application.services.meetup.MeetupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Service for synchronizing Meetup.com data with the local database
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MeetupDataService {

    private final MeetupService meetupService;
    private final MeetupEventRepository meetupEventRepository;
    private final MemberRepository memberRepository;
    private final ParticipantRepository participantRepository;

    /**
     * Import or update a meetup event from the Meetup.com API
     * @param meetupEventId The Meetup.com event ID
     * @return The imported/updated event
     */
    @Transactional
    public MeetupEvent importMeetupEvent(String meetupEventId) {
        // Get the event details from the Meetup.com API
        Optional<MeetupService.MeetupEvent> apiEventOpt = meetupService.getEvent(meetupEventId);
        
        if (apiEventOpt.isEmpty()) {
            throw new IllegalArgumentException("Meetup event not found: " + meetupEventId);
        }
        
        MeetupService.MeetupEvent apiEvent = apiEventOpt.get();
        
        // Find or create the event in our database
        MeetupEvent event = meetupEventRepository.findByMeetupId(meetupEventId)
                .orElseGet(() -> {
                    MeetupEvent newEvent = new MeetupEvent();
                    newEvent.setMeetupId(meetupEventId);
                    return newEvent;
                });
        
        // Update the event with data from the API
        event.updateFromApiResponse(apiEvent);
        meetupEventRepository.save(event);
        
        // Update members
        syncMembersForEvent(event);
        
        return event;
    }
    
    /**
     * Sync just the members for an existing event without updating the event data
     * @param eventId The database ID of the event
     * @return The number of members synced
     */
    @Transactional
    public int syncEventMembers(Long eventId) {
        MeetupEvent event = meetupEventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found: " + eventId));
        
        return syncMembersForEvent(event);
    }
    
    /**
     * Sync just the members for an existing event without updating the event data
     * @param meetupEventId The Meetup.com event ID
     * @return The number of members synced
     */
    @Transactional
    public int syncEventMembersByMeetupId(String meetupEventId) {
        MeetupEvent event = meetupEventRepository.findByMeetupId(meetupEventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found: " + meetupEventId));
        
        return syncMembersForEvent(event);
    }
    
    /**
     * Sync members for an event from the Meetup API
     * @param event The event to sync members for
     * @return The number of members synced
     */
    private int syncMembersForEvent(MeetupEvent event) {
        // Get the event details with RSVPs from the Meetup.com API
        Optional<MeetupService.MeetupEventWithRSVPs> apiEventOpt = meetupService.getEventWithRSVPs(event.getMeetupId());
        
        if (apiEventOpt.isEmpty()) {
            throw new IllegalArgumentException("Meetup event not found in API: " + event.getMeetupId());
        }
        
        MeetupService.MeetupEventWithRSVPs apiEvent = apiEventOpt.get();
        
        // Update members and return count
        return updateParticipantsFromRSVPs(event, apiEvent);
    }
    
    /**
     * Update all meetup events (useful for scheduled tasks)
     */
    @Transactional
    public void updateAllMeetupEvents() {
        List<MeetupEvent> events = meetupEventRepository.findAll();
        for (MeetupEvent event : events) {
            try {
                importMeetupEvent(event.getMeetupId());
            } catch (Exception e) {
                log.error("Error updating meetup event {}: {}", event.getMeetupId(), e.getMessage());
            }
        }
    }
    
    /**
     * Get all meetup events
     */
    @Transactional(readOnly = true)
    public List<MeetupEvent> getAllMeetupEvents() {
        return meetupEventRepository.findAll();
    }
    
    /**
     * Get a meetup event by ID
     */
    @Transactional(readOnly = true)
    public Optional<MeetupEvent> getMeetupEventById(Long id) {
        return meetupEventRepository.findById(id);
    }
    
    /**
     * Get a meetup event by Meetup.com ID
     */
    @Transactional(readOnly = true)
    public Optional<MeetupEvent> getMeetupEventByMeetupId(String meetupId) {
        return meetupEventRepository.findByMeetupId(meetupId);
    }
    
    /**
     * Get all participants for a meetup event
     */
    public List<Participant> getParticipantsForEvent(MeetupEvent event) {
        return participantRepository.findByMeetupEvent(event);
    }
    
    /**
     * Get participants eligible for a raffle (RSVP=YES, not organizers, attended the event)
     */
    public List<Participant> getRaffleEligibleParticipants(MeetupEvent event) {
        return participantRepository.findByMeetupEventAndRsvpStatusAndIsOrganizerAndAttendanceStatus(
                event, RSVPStatus.YES, false, Participant.AttendanceStatus.ATTENDED);
    }
    
    /**
     * Update participant RSVP status
     */
    @Transactional
    public Participant updateParticipantRsvpStatus(Long participantId, RSVPStatus rsvpStatus) {
        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new IllegalArgumentException("Participant not found: " + participantId));
        
        participant.setRsvpStatus(rsvpStatus);
        participant.setLastUpdated(OffsetDateTime.now());
        return participantRepository.save(participant);
    }
    
    /**
     * Update participant attendance status
     */
    @Transactional
    public Participant updateParticipantAttendanceStatus(Long participantId, Participant.AttendanceStatus attendanceStatus) {
        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new IllegalArgumentException("Participant not found: " + participantId));
        
        switch (attendanceStatus) {
            case ATTENDED -> participant.markAsAttended();
            case NO_SHOW -> participant.markAsNoShow();
            case UNKNOWN -> participant.resetAttendanceInfo();
        }
        
        return participantRepository.save(participant);
    }
    
    /**
     * Mark a participant as having entered the raffle
     */
    @Transactional
    public Participant markParticipantEnteredRaffle(Long participantId) {
        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new IllegalArgumentException("Participant not found: " + participantId));
        
        participant.setHasEnteredRaffle(true);
        participant.setLastUpdated(OffsetDateTime.now());
        return participantRepository.save(participant);
    }
    
    /**
     * Reset raffle entry status for all participants of an event
     */
    @Transactional
    public void resetRaffleEntryForEvent(MeetupEvent event) {
        List<Participant> participants = participantRepository.findByMeetupEvent(event);
        for (Participant participant : participants) {
            participant.setHasEnteredRaffle(false);
            participant.setLastUpdated(OffsetDateTime.now());
        }
        participantRepository.saveAll(participants);
    }
    
    /**
     * Helper method to update participants from API event with RSVPs data
     * @return Number of participants updated or created
     */
    private int updateParticipantsFromRSVPs(MeetupEvent event, MeetupService.MeetupEventWithRSVPs apiEvent) {
        if (apiEvent.rsvps() == null) {
            return 0;
        }
        
        int count = 0;
        
        // Get all existing participants for this event
        List<Participant> existingParticipants = participantRepository.findByMeetupEvent(event);
        Set<String> rsvpMemberIds = new HashSet<>();
        
        // Process RSVPs from API
        for (MeetupService.RSVP rsvp : apiEvent.rsvps()) {
            // Skip invalid data
            if (rsvp.id() == null) {
                continue;
            }
            
            // Track which members were found in the API response
            rsvpMemberIds.add(rsvp.id());
            
            // Find or create Member
            Member member = memberRepository.findByMeetupId(rsvp.id())
                    .orElseGet(() -> {
                        Member newMember = new Member();
                        newMember.setMeetupId(rsvp.id());
                        newMember.setName(rsvp.name());
                        newMember.setEmail(rsvp.email());
                        newMember.setLastUpdated(OffsetDateTime.now());
                        return memberRepository.save(newMember);
                    });
            
            // Update member data
            member.setName(rsvp.name());
            member.setEmail(rsvp.email());
            member.setLastUpdated(OffsetDateTime.now());
            memberRepository.save(member);
            
            // Find or create Participant
            Participant participant = participantRepository.findByMeetupEventAndMember(event, member)
                    .orElseGet(() -> {
                        Participant newParticipant = new Participant();
                        newParticipant.setMeetupEvent(event);
                        newParticipant.setMember(member);
                        return newParticipant;
                    });
            
            // Update participant data
            participant.setRsvpId(rsvp.id());  // Use id as rsvpId for now
            
            // Important: In this implementation, we're not receiving the RSVP status from the API
            // For now, let's set everyone to YES as the default
            participant.setRsvpStatus(RSVPStatus.YES);
            
            participant.setLastUpdated(OffsetDateTime.now());
            
            participantRepository.save(participant);
            count++;
        }
        
        // Mark participants as NO if they're in the database but not in the API response
        List<Participant> missingParticipants = existingParticipants.stream()
                .filter(p -> p.getMember() != null && !rsvpMemberIds.contains(p.getMember().getMeetupId()))
                .toList();
        
        for (Participant missingParticipant : missingParticipants) {
            missingParticipant.setRsvpStatus(RSVPStatus.NO);
            missingParticipant.setLastUpdated(OffsetDateTime.now());
            participantRepository.save(missingParticipant);
            count++;
        }
        
        return count;
    }
    
    /**
     * Convert string RSVP status to enum
     */
    private RSVPStatus convertRsvpStatus(String status) {
        if (status == null) {
            return RSVPStatus.NO;
        }
        
        if (status.equalsIgnoreCase("yes")) {
            return RSVPStatus.YES;
        } else {
            return RSVPStatus.NO;
        }
    }
}