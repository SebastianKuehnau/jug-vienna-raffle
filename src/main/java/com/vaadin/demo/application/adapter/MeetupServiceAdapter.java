package com.vaadin.demo.application.adapter;

import com.vaadin.demo.application.adapter.out.meetupclient.MeetupAPIClientAdapter;
import com.vaadin.demo.application.adapter.out.persistence.data.MeetupEvent;
import com.vaadin.demo.application.adapter.out.persistence.data.Member;
import com.vaadin.demo.application.adapter.out.persistence.data.Participant;
import com.vaadin.demo.application.domain.model.*;
import com.vaadin.demo.application.application.port.out.MeetupPort;
import com.vaadin.demo.application.adapter.out.persistence.repository.MeetupEventRepository;
import com.vaadin.demo.application.adapter.out.persistence.repository.MemberRepository;
import com.vaadin.demo.application.adapter.out.persistence.repository.ParticipantRepository;
import com.vaadin.demo.application.adapter.out.meetupclient.MeetupAPIClient;
import com.vaadin.demo.application.adapter.out.meetupclient.MeetupAPIClient.MeetupEventWithRSVPs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Adapter implementation of the MeetupPort interface
 * This service is responsible for all Meetup-related operations and synchronization
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MeetupServiceAdapter implements MeetupPort {

    // External service client
    private final MeetupAPIClientAdapter meetupApiClientAdapter;

    // Repositories
    private final MeetupEventRepository meetupEventRepository;
    private final MemberRepository memberRepository;
    private final ParticipantRepository participantRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<EventRecord> getEventByMeetupId(String meetupId) {
        return meetupEventRepository.findByMeetupId(meetupId)
            .map(Mapper::toEventRecord);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventRecord> getAllEvents() {
        return meetupEventRepository.findAll().stream()
            .map(Mapper::toEventRecord)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipantRecord> getParticipantsForEvent(EventRecord event) {
        MeetupEvent entityEvent = meetupEventRepository.findById(event.id())
            .orElseThrow(() -> new IllegalArgumentException("Event not found: " + event.id()));

        return participantRepository.findByMeetupEvent(entityEvent).stream()
            .map(Mapper::toParticipantRecord)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipantRecord> getRaffleEligibleParticipants(EventRecord event) {
        MeetupEvent entityEvent = meetupEventRepository.findById(event.id())
            .orElseThrow(() -> new IllegalArgumentException("Event not found: " + event.id()));

        return participantRepository.findByMeetupEventAndRsvpStatus(
                entityEvent,
                Participant.RSVPStatus.YES).stream()
            .filter(p -> !p.getIsOrganizer())
            .map(Mapper::toParticipantRecord)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventRecord importEvent(String meetupEventId) {
        // Fetch event data from external API
        Optional<EventRecord> apiEventOpt =
                meetupApiClientAdapter.getEvent(meetupEventId);

        if (apiEventOpt.isEmpty()) {
            throw new IllegalArgumentException("Meetup event not found: " + meetupEventId);
        }

        EventRecord apiEvent = apiEventOpt.get();

        // Find or create the event in our database
        MeetupEvent event = meetupEventRepository.findByMeetupId(meetupEventId)
                .orElseGet(() -> {
                    MeetupEvent newEvent = new MeetupEvent();
                    newEvent.setMeetupId(meetupEventId);
                    return newEvent;
                });

        // Update the event with data from the API
        Mapper.updateFromApiResponse(event, apiEvent);
        MeetupEvent savedEvent = meetupEventRepository.save(event);

        // Sync members for the event
        syncEventMembersByMeetupId(meetupEventId);

        return Mapper.toEventRecord(savedEvent);
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
    public ParticipantRecord markParticipantEnteredRaffle(Long participantId) {
        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new IllegalArgumentException("Participant not found: " + participantId));

        participant.setHasEnteredRaffle(true);
        participant.setLastUpdated(OffsetDateTime.now());
        Participant saved = participantRepository.save(participant);

        return Mapper.toParticipantRecord(saved);
    }

    @Override
    @Transactional
    public ParticipantRecord markParticipantAttendedAndEnteredRaffle(Long participantId) {
        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new IllegalArgumentException("Participant not found: " + participantId));

        participant.setAttendanceStatus(Participant.AttendanceStatus.ATTENDED);
        participant.setHasEnteredRaffle(true);
        participant.setLastUpdated(OffsetDateTime.now());
        Participant saved = participantRepository.save(participant);

        return Mapper.toParticipantRecord(saved);
    }

    @Override
    @Transactional
    public ParticipantRecord markParticipantNoShowAndEnteredRaffle(Long participantId) {
        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new IllegalArgumentException("Participant not found: " + participantId));

        participant.setAttendanceStatus(Participant.AttendanceStatus.NO_SHOW);
        participant.setHasEnteredRaffle(true);
        participant.setLastUpdated(OffsetDateTime.now());
        Participant saved = participantRepository.save(participant);

        return Mapper.toParticipantRecord(saved);
    }

    @Override
    @Transactional
    public void resetRaffleEntryForEvent(EventRecord event) {
        MeetupEvent entityEvent = meetupEventRepository.findById(event.id())
            .orElseThrow(() -> new IllegalArgumentException("Event not found: " + event.id()));

        List<Participant> participants = participantRepository.findByMeetupEvent(entityEvent);
        for (Participant participant : participants) {
            participant.setHasEnteredRaffle(false);
            participant.setLastUpdated(OffsetDateTime.now());
        }
        participantRepository.saveAll(participants);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EventRecord> getEventById(Long id) {
        return meetupEventRepository.findById(id)
            .map(Mapper::toEventRecord);
    }

    @Override
    @Transactional
    public EventRecord saveEvent(EventRecord event) {
        // Convert from domain record to JPA entity
        MeetupEvent entityToSave = event.id() != null ?
            meetupEventRepository.findById(event.id())
                .orElse(new MeetupEvent()) :
            new MeetupEvent();

        // Update fields from domain record
        entityToSave.setMeetupId(event.meetupId());
        entityToSave.setTitle(event.title());
        entityToSave.setDescription(event.description());
        entityToSave.setDateTime(event.eventDate());
        entityToSave.setEventUrl(event.link());

        // Save entity and convert back to domain record
        MeetupEvent savedEntity = meetupEventRepository.save(entityToSave);
        return Mapper.toEventRecord(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ParticipantRecord> getParticipantById(Long id) {
        return participantRepository.findById(id)
            .map(Mapper::toParticipantRecord);
    }

    @Override
    @Transactional
    public ParticipantRecord markParticipantNotEnteredRaffle(Long participantId) {
        Participant participant = participantRepository.findById(participantId)
            .orElseThrow(() -> new IllegalArgumentException("Participant not found: " + participantId));

        participant.setHasEnteredRaffle(false);
        participant.setLastUpdated(OffsetDateTime.now());

        Participant savedParticipant = participantRepository.save(participant);
        return Mapper.toParticipantRecord(savedParticipant);
    }

    @Override
    @Transactional
    public ParticipantRecord markParticipantAttended(Long participantId) {
        Participant participant = participantRepository.findById(participantId)
            .orElseThrow(() -> new IllegalArgumentException("Participant not found: " + participantId));

        participant.setAttendanceStatus(Participant.AttendanceStatus.ATTENDED);
        participant.setLastUpdated(OffsetDateTime.now());

        Participant savedParticipant = participantRepository.save(participant);
        return Mapper.toParticipantRecord(savedParticipant);
    }

    @Override
    @Transactional
    public ParticipantRecord markParticipantNoShow(Long participantId) {
        Participant participant = participantRepository.findById(participantId)
            .orElseThrow(() -> new IllegalArgumentException("Participant not found: " + participantId));

        participant.setAttendanceStatus(Participant.AttendanceStatus.NO_SHOW);
        participant.setLastUpdated(OffsetDateTime.now());

        Participant savedParticipant = participantRepository.save(participant);
        return Mapper.toParticipantRecord(savedParticipant);
    }

    @Override
    @Transactional
    public ParticipantRecord resetParticipantAttendanceStatus(Long participantId) {
        Participant participant = participantRepository.findById(participantId)
            .orElseThrow(() -> new IllegalArgumentException("Participant not found: " + participantId));

        participant.setAttendanceStatus(Participant.AttendanceStatus.UNKNOWN);
        participant.setLastUpdated(OffsetDateTime.now());

        Participant savedParticipant = participantRepository.save(participant);
        return Mapper.toParticipantRecord(savedParticipant);
    }

    /**
     * Sync members for an event from the Meetup API
     * @param event The event to sync members for
     * @return The number of members synced
     */
    private int syncMembersForEvent(MeetupEvent event) {
        // Get the event details with RSVPs from the Meetup.com API
        Optional<EventRecordWithRSVPs> apiEventOpt = meetupApiClientAdapter.getEventWithRSVPs(event.getMeetupId());

        if (apiEventOpt.isEmpty()) {
            throw new IllegalArgumentException("Meetup event not found in API: " + event.getMeetupId());
        }

        EventRecordWithRSVPs apiEvent = apiEventOpt.get();

        return updateParticipantsFromRSVPs(event, apiEvent);
    }

    /**
     * Helper method to update participants from API event with RSVPs data
     * @return Number of participants updated or created
     */
    @Deprecated
    private int updateParticipantsFromRSVPs(MeetupEvent event, MeetupEventWithRSVPs apiEvent) {
        if (apiEvent.rsvps() == null) {
            return 0;
        }

        int count = 0;

        // Get all existing participants for this event
        List<Participant> existingParticipants = participantRepository.findByMeetupEvent(event);
        Set<String> rsvpMemberIds = new HashSet<>();

        // Process RSVPs from API
        for (MeetupAPIClient.RSVP rsvp : apiEvent.rsvps()) {
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
            participant.setRsvpStatus(Participant.RSVPStatus.YES);

            participant.setLastUpdated(OffsetDateTime.now());

            participantRepository.save(participant);
            count++;
        }

        // Mark participants as NO if they're in the database but not in the API response
        List<Participant> missingParticipants = existingParticipants.stream()
                .filter(p -> p.getMember() != null && !rsvpMemberIds.contains(p.getMember().getMeetupId()))
                .toList();

        for (Participant missingParticipant : missingParticipants) {
            missingParticipant.setRsvpStatus(Participant.RSVPStatus.NO);
            missingParticipant.setLastUpdated(OffsetDateTime.now());
            participantRepository.save(missingParticipant);
            count++;
        }

        return count;
    }

    private int updateParticipantsFromRSVPs(MeetupEvent event, EventRecordWithRSVPs apiEvent) {
        if (apiEvent.members() == null) {
            return 0;
        }

        int count = 0;

        // Get all existing participants for this event
        List<Participant> existingParticipants = participantRepository.findByMeetupEvent(event);
        Set<String> rsvpMemberIds = new HashSet<>();

        // Process RSVPs from API
        for (EventRecordWithRSVPs.RSVPMember rsvp : apiEvent.members()) {
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
            participant.setRsvpStatus(Participant.RSVPStatus.YES);

            participant.setLastUpdated(OffsetDateTime.now());

            participantRepository.save(participant);
            count++;
        }

        // Mark participants as NO if they're in the database but not in the API response
        List<Participant> missingParticipants = existingParticipants.stream()
                .filter(p -> p.getMember() != null && !rsvpMemberIds.contains(p.getMember().getMeetupId()))
                .toList();

        for (Participant missingParticipant : missingParticipants) {
            missingParticipant.setRsvpStatus(Participant.RSVPStatus.NO);
            missingParticipant.setLastUpdated(OffsetDateTime.now());
            participantRepository.save(missingParticipant);
            count++;
        }

        return count;
    }
}