package com.vaadin.demo.application.data;

import com.vaadin.demo.application.repository.MeetupEventRepository;
import com.vaadin.demo.application.repository.RaffleRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service to migrate existing Raffle data to use new relationships
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RaffleMigrationService {

    private final RaffleRepository raffleRepository;
    private final MeetupEventRepository eventRepository;
    
    /**
     * Migrates data after application startup
     */
    @PostConstruct
    public void migrate() {
        log.info("Checking if Raffle-Event relationships need to be migrated...");
        
        try {
            migrateRaffleEvents();
        } catch (Exception e) {
            log.error("Error during raffle-event migration", e);
        }
    }
    
    /**
     * Update Raffle records to populate the event relationship
     */
    @Transactional
    public void migrateRaffleEvents() {
        // Get all raffles
        List<Raffle> raffles = raffleRepository.findAll();
        
        // Count how many need updating
        long needsUpdate = raffles.stream()
                .filter(r -> r.getEvent() == null && r.getMeetup_event_id() != null && !r.getMeetup_event_id().isEmpty())
                .count();
                
        if (needsUpdate == 0) {
            log.info("No Raffle records need event relationship updates");
            return;
        }
        
        log.info("Updating event relationships for {} Raffle records...", needsUpdate);
        
        // Get all events indexed by meetupId
        Map<String, MeetupEvent> eventsByMeetupId = eventRepository.findAll().stream()
                .filter(e -> e.getMeetupId() != null && !e.getMeetupId().isEmpty())
                .collect(Collectors.toMap(MeetupEvent::getMeetupId, e -> e));
                
        // Update raffle records
        int updated = 0;
        for (Raffle raffle : raffles) {
            if (raffle.getEvent() == null && raffle.getMeetup_event_id() != null && !raffle.getMeetup_event_id().isEmpty()) {
                MeetupEvent event = eventsByMeetupId.get(raffle.getMeetup_event_id());
                if (event != null) {
                    raffle.setEvent(event);
                    raffleRepository.save(raffle);
                    updated++;
                }
            }
        }
        
        log.info("Successfully updated {} Raffle records with event relationships", updated);
    }
}