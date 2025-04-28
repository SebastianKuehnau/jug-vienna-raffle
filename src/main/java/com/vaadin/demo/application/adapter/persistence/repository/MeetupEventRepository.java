package com.vaadin.demo.application.adapter.persistence.repository;

import com.vaadin.demo.application.adapter.persistence.data.MeetupEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MeetupEventRepository extends JpaRepository<MeetupEvent, Long> {

    /**
     * Find a meetup event by its Meetup.com ID
     */
    Optional<MeetupEvent> findByMeetupId(String meetupId);

    /**
     * Check if an event with the given Meetup.com ID exists
     */
    boolean existsByMeetupId(String meetupId);

    /**
     * Find all meetup events with their participants loaded
     * (to avoid LazyInitializationException)
     */
    @Query("SELECT DISTINCT e FROM MeetupEvent e LEFT JOIN FETCH e.participants")
    List<MeetupEvent> findAllWithParticipants();

    /**
     * Find a specific meetup event with its participants loaded
     * (to avoid LazyInitializationException)
     */
    @Query("SELECT e FROM MeetupEvent e LEFT JOIN FETCH e.participants WHERE e.id = :id")
    Optional<MeetupEvent> findByIdWithParticipants(Long id);

    /**
     * Find a specific meetup event by Meetup ID with its participants loaded
     * (to avoid LazyInitializationException)
     */
    @Query("SELECT e FROM MeetupEvent e LEFT JOIN FETCH e.participants WHERE e.meetupId = :meetupId")
    Optional<MeetupEvent> findByMeetupIdWithParticipants(String meetupId);
}