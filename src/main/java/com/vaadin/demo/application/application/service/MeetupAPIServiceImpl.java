package com.vaadin.demo.application.application.service;

import com.vaadin.demo.application.domain.model.EventRecord;
import com.vaadin.demo.application.adapter.Mapper;
import com.vaadin.demo.application.adapter.out.meetupclient.MeetupClient;
import com.vaadin.demo.application.application.port.in.MeetupAPIService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of the MeetupService that communicates with the external Meetup API
 * while adhering to hexagonal architecture principles
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MeetupAPIServiceImpl implements MeetupAPIService {

    private final MeetupClient meetupApiClient;

    @Override
    public List<EventRecord> getExternalEvents() {
        return meetupApiClient.getEvents().stream()
                .map(Mapper::toEventRecord)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<EventRecord> getExternalEvent(String meetupId) {
        return meetupApiClient.getEvent(meetupId)
                .map(Mapper::toEventRecord);
    }

    @Override
    public Optional<EventRecord> getExternalEventWithRSVPs(String meetupId) {
        return meetupApiClient.getEventWithRSVPs(meetupId)
                .map(eventWithRSVPs -> {
                    // Convert to EventRecord with additional participants info
                    EventRecord baseEvent = new EventRecord(
                            null, // No ID since this is from external API
                            eventWithRSVPs.id(),
                            eventWithRSVPs.title(),
                            eventWithRSVPs.description(),
                            eventWithRSVPs.dateTime(),
                            null, // No venue info
                            null  // No URL in this API response
                    );

                    // Here you could add participant info if needed
                    return baseEvent;
                });
    }
}