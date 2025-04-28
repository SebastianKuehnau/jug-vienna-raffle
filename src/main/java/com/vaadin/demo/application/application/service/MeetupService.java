package com.vaadin.demo.application.application.service;

import com.vaadin.demo.application.domain.model.EventRecord;

import java.util.List;
import java.util.Optional;

/**
 * Application service interface for fetching external Meetup data
 * This is separate from MeetupApplicationService, which focuses on domain operations
 */
public interface MeetupService {
    
    /**
     * Fetch a list of available external events from Meetup API
     * @return List of event records representing available external events
     */
    List<EventRecord> getExternalEvents();
    
    /**
     * Get details of a specific external event from Meetup API
     * @param meetupId The external Meetup event ID
     * @return The event details or empty if not found
     */
    Optional<EventRecord> getExternalEvent(String meetupId);
    
    /**
     * Get detailed event information with RSVPs for the specified Meetup event ID
     * @param meetupId The Meetup event ID
     * @return The event details with RSVPs, or empty if not found
     */
    Optional<EventRecord> getExternalEventWithRSVPs(String meetupId);
}