package com.vaadin.demo.application.application.service;

import com.vaadin.demo.application.adapter.persistence.data.MeetupEvent;
import com.vaadin.demo.application.adapter.persistence.data.Participant;
import com.vaadin.demo.application.adapter.persistence.data.Prize;
import com.vaadin.demo.application.adapter.persistence.data.Raffle;
import com.vaadin.demo.application.domain.model.EventRecord;
import com.vaadin.demo.application.adapter.persistence.repository.MeetupEventRepository;
import com.vaadin.demo.application.adapter.persistence.repository.ParticipantRepository;
import com.vaadin.demo.application.adapter.persistence.repository.PrizeRepository;
import com.vaadin.demo.application.adapter.persistence.repository.RaffleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of the RafflePort interface
 * This service is responsible for all Raffle-related operations
 */
// Keeping this for backward compatibility
// The new service implementation is in the adapter package
@Service
@RequiredArgsConstructor
@Slf4j
public class RaffleService {

    private final RaffleRepository raffleRepository;
    private final PrizeRepository prizeRepository;
    private final com.vaadin.demo.application.adapter.MeetupServiceAdapter meetupService;
    private final MeetupEventRepository meetupEventRepository;
    private final ParticipantRepository participantRepository;

    @Transactional(readOnly = true)
    public Optional<Raffle> getRaffleById(Long id) {
        return raffleRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Prize> getPrizeById(Long id) {
        return prizeRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Raffle> getRaffleByMeetupEventId(String meetupEventId) {
        // Try both methods for compatibility
        Optional<Raffle> result = raffleRepository.findByMeetupEventId(meetupEventId);
        if (result.isEmpty()) {
            result = raffleRepository.findByEvent_MeetupId(meetupEventId);
        }
        return result;
    }

    @Transactional(readOnly = true)
    public List<Prize> getPrizesForRaffle(Raffle raffle) {
        return prizeRepository.findByRaffle(raffle);
    }

    @Transactional(readOnly = true)
    public List<Participant> getEligibleParticipants(Raffle raffle) {
        MeetupEvent event = raffle.getEvent();
        if (event != null) {
            // Convert MeetupEvent to EventRecord first
            EventRecord eventRecord = new EventRecord(
                event.getId(),
                event.getMeetupId(),
                event.getTitle(),
                event.getDescription(),
                event.getDateTime(),
                null, // No venue in MeetupEvent
                event.getEventUrl()
            );
            return meetupService.getRaffleEligibleParticipants(eventRecord).stream()
                .map(record -> {
                    // Find the actual Participant entity in the database
                    return participantRepository.findById(record.id())
                        .orElseThrow(() -> new IllegalArgumentException("Participant not found: " + record.id()));
                })
                .collect(Collectors.toList());
        }
        return List.of();
    }

    @Transactional
    public Raffle createRaffle(EventRecord eventRecord) {
        // First convert the domain eventRecord to a JPA entity
        MeetupEvent meetupEvent = meetupEventRepository.findByMeetupId(eventRecord.meetupId())
                .orElseThrow(() -> new IllegalArgumentException("MeetupEvent not found with ID: " + eventRecord.meetupId()));

        // Check if a raffle already exists for this event
        if (getRaffleByMeetupEventId(meetupEvent.getMeetupId()).isPresent()) {
            throw new IllegalStateException("A raffle already exists for this event");
        }

        Raffle raffle = new Raffle();
        raffle.setMeetup_event_id(meetupEvent.getMeetupId());
        raffle.setEvent(meetupEvent);
        return saveRaffle(raffle);
    }

    @Transactional
    public Raffle saveRaffle(Raffle raffle) {
        return raffleRepository.save(raffle);
    }

    @Transactional
    public Prize savePrize(Prize prize) {
        return prizeRepository.save(prize);
    }

    @Transactional
    public Prize awardPrize(Prize prize, Participant participant) {
        prize.setWinner(participant);
        // Mark participant as attended and having entered the raffle
        meetupService.markParticipantAttendedAndEnteredRaffle(participant.getId());
        return savePrize(prize);
    }

    @Transactional
    public void deletePrize(Long prizeId) {
        prizeRepository.deleteById(prizeId);
    }

    // Methods to make it compatible with the views
    public List<Raffle> list(Pageable pageable) {
        return raffleRepository.findAll(pageable).stream().toList();
    }

    public List<Raffle> list(Pageable pageable, Specification<Raffle> filter) {
        return raffleRepository.findAll(filter, pageable).stream().toList();
    }

    public long count() {
        return raffleRepository.count();
    }

    public long count(Specification<Raffle> filter) {
        return raffleRepository.count(filter);
    }

    public Optional<Raffle> get(Long id) {
        return raffleRepository.findById(id);
    }

    public Raffle save(Raffle entity) {
        return raffleRepository.save(entity);
    }

    public void delete(Long id) {
        raffleRepository.deleteById(id);
    }
}