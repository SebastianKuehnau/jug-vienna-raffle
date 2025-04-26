package com.vaadin.demo.application.services;

import com.vaadin.demo.application.data.Raffle;
import com.vaadin.demo.application.repository.RaffleRepository;
import java.util.HashMap;
import java.util.Map;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// Old service - now replaced by hexagonal architecture implementation
@Service(value = "oldRaffleService")
public class RaffleService {

    private final RaffleRepository repository;

    public RaffleService(RaffleRepository repository) {
        this.repository = repository;
    }

    public Optional<Raffle> get(Long id) {
        return repository.findById(id);
    }

    public Raffle save(Raffle entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public List<Raffle> list(Pageable pageable) {
        return repository.findAll(pageable).stream().toList();
    }

    public List<Raffle> list(Pageable pageable, Specification<Raffle> filter) {
        return repository.findAll(filter, pageable).stream().toList();
    }

    public long count() {
        return repository.count();
    }

    public long count(Specification<Raffle> filter) {
        return repository.count(filter);
    }
    
    /**
     * Find a raffle by its Meetup event ID
     */
    public Optional<Raffle> getRaffleByMeetupEventId(String meetupEventId) {
        // Try both methods for compatibility
        Optional<Raffle> result = repository.findByMeetupEventId(meetupEventId);
        if (result.isEmpty()) {
            result = repository.findByEvent_MeetupId(meetupEventId);
        }
        return result;
    }
    
    /**
     * Get a map of meetup event IDs to raffle status
     * @return Map where key is meetup event ID and value is the associated raffle if it exists
     */
    public Map<String, Optional<Raffle>> getRaffleStatusMap(List<String> meetupEventIds) {
        Map<String, Optional<Raffle>> result = new HashMap<>();
        
        // Initialize all with empty
        for (String id : meetupEventIds) {
            result.put(id, Optional.empty());
        }
        
        // Get all existing raffles
        List<Raffle> raffles = repository.findByMeetupEventIdIn(meetupEventIds);
        
        // Update map with found raffles
        for (Raffle raffle : raffles) {
            result.put(raffle.getMeetup_event_id(), Optional.of(raffle));
        }
        
        return result;
    }
}
