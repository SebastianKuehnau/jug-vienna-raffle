package com.vaadin.demo.application.data;

import com.vaadin.demo.application.repository.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for migrating data from the old model to the new model
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DataMigrationService {

    private final MeetupEventRepository eventRepository;
    private final MeetupMemberRepository oldMemberRepository;
    private final MemberRepository memberRepository;
    private final ParticipantRepository participantRepository;
    private final PrizeRepository prizeRepository;
    private final RaffleRepository raffleRepository;
    
    /**
     * Migrates data after application startup
     */
    @PostConstruct
    public void migrate() {
        // Check if we need to migrate (if there are any MeetupMembers but no Members)
        if (oldMemberRepository.count() > 0 && memberRepository.count() == 0) {
            log.info("Starting data migration from old model to new model");
            try {
                migrateData();
                log.info("Data migration completed successfully");
            } catch (Exception e) {
                log.error("Error during data migration", e);
            }
        } else {
            log.info("No data migration needed");
        }
    }
    
    /**
     * Perform the data migration
     */
    @Transactional
    public void migrateData() {
        log.info("Migrating MeetupMembers to Members and Participants...");
        
        // Get all existing MeetupMembers
        List<MeetupMember> oldMembers = oldMemberRepository.findAll();
        
        // Map to track created Members by meetupId
        Map<String, Member> createdMembers = new HashMap<>();
        
        for (MeetupMember oldMember : oldMembers) {
            // Skip if missing data
            if (oldMember.getMeetupId() == null || oldMember.getMeetupEvent() == null) {
                log.warn("Skipping invalid MeetupMember: {}", oldMember.getId());
                continue;
            }
            
            // Find or create Member
            Member member = createdMembers.computeIfAbsent(
                oldMember.getMeetupId(),
                meetupId -> {
                    Optional<Member> existingMember = memberRepository.findByMeetupId(meetupId);
                    if (existingMember.isPresent()) {
                        return existingMember.get();
                    } else {
                        Member newMember = new Member();
                        newMember.setMeetupId(meetupId);
                        newMember.setName(oldMember.getName());
                        newMember.setEmail(oldMember.getEmail());
                        newMember.setLastUpdated(oldMember.getLastUpdated() != null ? 
                                oldMember.getLastUpdated() : OffsetDateTime.now());
                        return memberRepository.save(newMember);
                    }
                }
            );
            
            // Create Participant for this Member in this Event
            MeetupEvent event = oldMember.getMeetupEvent();
            
            // Skip if already created
            if (participantRepository.findByMeetupEventAndMember(event, member).isPresent()) {
                continue;
            }
            
            Participant participant = new Participant();
            participant.setMeetupEvent(event);
            participant.setMember(member);
            participant.setRsvpId(oldMember.getRsvpId());
            participant.setIsOrganizer(oldMember.getIsOrganizer());
            participant.setHasEnteredRaffle(oldMember.getHasEnteredRaffle());
            participant.setRsvpStatus(
                oldMember.getRsvpStatus() == MeetupMember.RSVPStatus.YES ? 
                    Participant.RSVPStatus.YES : Participant.RSVPStatus.NO
            );
            
            // Convert attendance status
            switch (oldMember.getAttendanceStatus()) {
                case ATTENDED -> participant.setAttendanceStatus(Participant.AttendanceStatus.ATTENDED);
                case NO_SHOW -> participant.setAttendanceStatus(Participant.AttendanceStatus.NO_SHOW);
                default -> participant.setAttendanceStatus(Participant.AttendanceStatus.UNKNOWN);
            }
            
            participant.setLastUpdated(oldMember.getLastUpdated() != null ? 
                oldMember.getLastUpdated() : OffsetDateTime.now());
            
            participantRepository.save(participant);
        }
        
        log.info("Migrating Raffle event relationships...");
        
        // Update Raffle events based on meetup_event_id
        List<Raffle> raffles = raffleRepository.findAll();
        Map<String, MeetupEvent> eventsByMeetupId = eventRepository.findAll().stream()
            .filter(e -> e.getMeetupId() != null && !e.getMeetupId().isEmpty())
            .collect(Collectors.toMap(MeetupEvent::getMeetupId, e -> e));
            
        for (Raffle raffle : raffles) {
            if (raffle.getEvent() == null && raffle.getMeetup_event_id() != null && !raffle.getMeetup_event_id().isEmpty()) {
                MeetupEvent event = eventsByMeetupId.get(raffle.getMeetup_event_id());
                if (event != null) {
                    raffle.setEvent(event);
                    raffleRepository.save(raffle);
                }
            }
        }
        
        log.info("Migrating Prize winners...");
        
        // Update Prize winners to point to Participants
        List<Prize> prizes = prizeRepository.findAll();
        for (Prize prize : prizes) {
            // Skip if already migrated or no winner
            if (prize.getWinner() != null || prize.getWinnerName() == null || prize.getWinnerName().isEmpty()) {
                continue;
            }
            
            // Try to find a participant with this name
            participantRepository.findAll().stream()
                .filter(p -> p.getMember() != null && 
                             prize.getWinnerName() != null &&
                             prize.getWinnerName().equals(p.getMember().getName()) && 
                             prize.getRaffle() != null && 
                             prize.getRaffle().getEvent() != null && 
                             p.getMeetupEvent() != null && 
                             p.getMeetupEvent().getId().equals(prize.getRaffle().getEvent().getId()))
                .findFirst()
                .ifPresent(prize::setWinner);
            
            prizeRepository.save(prize);
        }
    }
}