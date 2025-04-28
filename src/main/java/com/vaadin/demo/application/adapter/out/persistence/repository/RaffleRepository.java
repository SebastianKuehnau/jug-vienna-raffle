package com.vaadin.demo.application.adapter.out.persistence.repository;

import com.vaadin.demo.application.adapter.out.persistence.data.Raffle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RaffleRepository
        extends JpaRepository<Raffle, Long>,
        JpaSpecificationExecutor<Raffle> {

    /**
     * Find a raffle by its Meetup event ID
     */
    @Query("SELECT r FROM Raffle r WHERE r.meetup_event_id = :eventId")
    Optional<Raffle> findByMeetupEventId(@Param("eventId") String meetupEventId);

    /**
     * Find a raffle by its MeetupEvent entity
     */
    Optional<Raffle> findByEvent_MeetupId(String meetupId);

    /**
     * Find all raffles for a list of Meetup event IDs
     */
    @Query("SELECT r FROM Raffle r WHERE r.meetup_event_id IN :eventIds")
    List<Raffle> findByMeetupEventIdIn(@Param("eventIds") List<String> meetupEventIds);
}
