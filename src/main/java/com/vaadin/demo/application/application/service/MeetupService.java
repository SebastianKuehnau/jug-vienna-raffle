package com.vaadin.demo.application.application.service;

import com.vaadin.demo.application.data.MeetupEvent;
import com.vaadin.demo.application.data.Member;
import com.vaadin.demo.application.data.Participant;
import com.vaadin.demo.application.data.Participant.RSVPStatus;
import com.vaadin.demo.application.domain.port.MeetupPort;
import com.vaadin.demo.application.repository.MeetupEventRepository;
import com.vaadin.demo.application.repository.MemberRepository;
import com.vaadin.demo.application.repository.ParticipantRepository;
import com.vaadin.demo.application.services.meetup.MeetupService.MeetupEventWithRSVPs;
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
 * Implementation of the MeetupPort interface
 * This service is responsible for all Meetup-related operations and synchronization
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MeetupService implements MeetupPort {

    // External service client
    private final com.vaadin.demo.application.services.meetup.MeetupService meetupApiClient;
    
    // Repositories
    private final MeetupEventRepository meetupEventRepository;
    private final MemberRepository memberRepository;
    private final ParticipantRepository participantRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<MeetupEvent> getEventByMeetupId(String meetupId) {
        return meetupEventRepository.findByMeetupId(meetupId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MeetupEvent> getAllEvents() {
        return meetupEventRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Participant> getParticipantsForEvent(MeetupEvent event) {
        return participantRepository.findByMeetupEvent(event);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Participant> getRaffleEligibleParticipants(MeetupEvent event) {
        return participantRepository.findByMeetupEventAndRsvpStatusAndIsOrganizerAndAttendanceStatus(
                event, RSVPStatus.YES, false, Participant.AttendanceStatus.ATTENDED);
    }

    @Override
    @Transactional
    public MeetupEvent importEvent(String meetupEventId) {
        // Fetch event data from external API
        Optional<com.vaadin.demo.application.services.meetup.MeetupService.MeetupEvent> apiEventOpt = 
                meetupApiClient.getEvent(meetupEventId);
        
        if (apiEventOpt.isEmpty()) {
            throw new IllegalArgumentException("Meetup event not found: " + meetupEventId);
        }
        
        com.vaadin.demo.application.services.meetup.MeetupService.MeetupEvent apiEvent = apiEventOpt.get();
        
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
        
        // Sync members for the event
        syncEventMembersByMeetupId(meetupEventId);
        
        return event;
    }

    @Override
    @Transactional
    public int syncEventMembers(Long eventId) {
        MeetupEvent event = meetupEventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found: " + eventId));
        
        return syncMembersForEvent(event);
    }

    @Override
    @Transactional
    public int syncEventMembersByMeetupId(String meetupEventId) {
        MeetupEvent event = meetupEventRepository.findByMeetupId(meetupEventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found: " + meetupEventId));
        
        return syncMembersForEvent(event);
    }

    @Override
    @Transactional
    public Participant markParticipantEnteredRaffle(Long participantId) {
        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new IllegalArgumentException("Participant not found: " + participantId));
        
        participant.setHasEnteredRaffle(true);
        participant.setLastUpdated(OffsetDateTime.now());
        return participantRepository.save(participant);
    }
    
    @Override
    @Transactional
    public Participant markParticipantAttendedAndEnteredRaffle(Long participantId) {
        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new IllegalArgumentException("Participant not found: " + participantId));
        
        participant.setAttendanceStatus(Participant.AttendanceStatus.ATTENDED);
        participant.setHasEnteredRaffle(true);
        participant.setLastUpdated(OffsetDateTime.now());
        return participantRepository.save(participant);
    }
    
    @Override
    @Transactional
    public Participant markParticipantNoShowAndEnteredRaffle(Long participantId) {
        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new IllegalArgumentException("Participant not found: " + participantId));
        
        participant.setAttendanceStatus(Participant.AttendanceStatus.NO_SHOW);
        participant.setHasEnteredRaffle(true);
        participant.setLastUpdated(OffsetDateTime.now());
        return participantRepository.save(participant);
    }

    @Override
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
     * Sync members for an event from the Meetup API
     * @param event The event to sync members for
     * @return The number of members synced
     */
    private int syncMembersForEvent(MeetupEvent event) {
        // Get the event details with RSVPs from the Meetup.com API
        Optional<MeetupEventWithRSVPs> apiEventOpt = meetupApiClient.getEventWithRSVPs(event.getMeetupId());
        
        if (apiEventOpt.isEmpty()) {
            throw new IllegalArgumentException("Meetup event not found in API: " + event.getMeetupId());
        }
        
        MeetupEventWithRSVPs apiEvent = apiEventOpt.get();
        
        return updateParticipantsFromRSVPs(event, apiEvent);
    }
    
    /**
     * Helper method to update participants from API event with RSVPs data
     * @return Number of participants updated or created
     */
    private int updateParticipantsFromRSVPs(MeetupEvent event, MeetupEventWithRSVPs apiEvent) {
        if (apiEvent.rsvps() == null) {
            return 0;
        }
        
        int count = 0;
        
        // Get all existing participants for this event
        List<Participant> existingParticipants = participantRepository.findByMeetupEvent(event);
        Set<String> rsvpMemberIds = new HashSet<>();
        
        // Process RSVPs from API
        for (com.vaadin.demo.application.services.meetup.MeetupService.RSVP rsvp : apiEvent.rsvps()) {
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
            participant.setRsvpId(rsvp.id());
            
            // For now, we're assuming everyone in the RSVP list is a YES
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
}