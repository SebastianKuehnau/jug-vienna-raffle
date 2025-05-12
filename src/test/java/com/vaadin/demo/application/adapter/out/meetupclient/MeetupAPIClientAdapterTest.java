package com.vaadin.demo.application.adapter.out.meetupclient;

import com.vaadin.demo.application.domain.model.EventRecord;
import com.vaadin.demo.application.domain.model.EventRecordWithRSVPs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MeetupAPIClientAdapterTest {

    @Mock
    private MeetupAPIClient meetupAPIClient;

    private MeetupAPIClientAdapter meetupAPIClientAdapter;

    // Test data
    private final String testMeetupId = "event123";
    private final OffsetDateTime testDateTime = OffsetDateTime.now();
    private MeetupAPIClient.MeetupEvent testApiEvent;
    private MeetupAPIClient.MeetupEventWithRSVPs testApiEventWithRSVPs;

    @BeforeEach
    void setUp() {
        meetupAPIClientAdapter = new MeetupAPIClientAdapter(meetupAPIClient);

        // Create test data
        testApiEvent = new MeetupAPIClient.MeetupEvent(
            testMeetupId,
            "token123",
            "Test Event",
            testDateTime,
            "Test Description",
            "http://test.url",
            "active",
            Set.of(new MeetupAPIClient.Member("member1", "John Doe", "john@example.com", "rsvp1", false, false))
        );

        testApiEventWithRSVPs = new MeetupAPIClient.MeetupEventWithRSVPs(
            testMeetupId,
            "token123",
            "Test Event",
            testDateTime,
            "Test Description",
            List.of(new MeetupAPIClient.RSVP(
                "member1",
                "john@example.com",
                "male",
                "http://member.url",
                "John Doe",
                "active",
                "yes",
                "johndoe",
                new MeetupAPIClient.MemberPhoto("photo1", "baseUrl", "highRes", "standard", "thumb")
            ))
        );
    }

    @Test
    void getEventShouldReturnMappedEvent() {
        // Given
        when(meetupAPIClient.getEvent(testMeetupId)).thenReturn(Optional.of(testApiEvent));

        // When
        Optional<EventRecord> result = meetupAPIClientAdapter.getEvent(testMeetupId);

        // Then
        assertTrue(result.isPresent());
        EventRecord eventRecord = result.get();
        assertEquals(testMeetupId, eventRecord.meetupId());
        assertEquals(testApiEvent.title(), eventRecord.title());
        assertEquals(testApiEvent.description(), eventRecord.description());
        assertEquals(testApiEvent.dateTime(), eventRecord.eventDate());
        assertEquals(testApiEvent.eventUrl(), eventRecord.link());
        verify(meetupAPIClient).getEvent(testMeetupId);
    }

    @Test
    void getEventShouldReturnEmptyWhenNotFound() {
        // Given
        when(meetupAPIClient.getEvent(testMeetupId)).thenReturn(Optional.empty());

        // When
        Optional<EventRecord> result = meetupAPIClientAdapter.getEvent(testMeetupId);

        // Then
        assertFalse(result.isPresent());
        verify(meetupAPIClient).getEvent(testMeetupId);
    }

    @Test
    void getEventWithRSVPsShouldReturnMappedEventWithRSVPs() {
        // Given
        when(meetupAPIClient.getEventWithRSVPs(testMeetupId)).thenReturn(Optional.of(testApiEventWithRSVPs));

        // When
        Optional<EventRecordWithRSVPs> result = meetupAPIClientAdapter.getEventWithRSVPs(testMeetupId);

        // Then
        assertTrue(result.isPresent());
        EventRecordWithRSVPs eventRecord = result.get();
        assertEquals(testMeetupId, eventRecord.meetupId());
        assertEquals(testApiEventWithRSVPs.title(), eventRecord.title());
        assertEquals(testApiEventWithRSVPs.description(), eventRecord.description());
        assertEquals(testApiEventWithRSVPs.dateTime(), eventRecord.eventDate());

        // Verify RSVP members
        assertNotNull(eventRecord.members());
        assertEquals(1, eventRecord.members().size());

        EventRecordWithRSVPs.RSVPMember member = eventRecord.members().get(0);
        assertEquals("member1", member.id());
        assertEquals("John Doe", member.name());
        assertEquals("john@example.com", member.email());

        verify(meetupAPIClient).getEventWithRSVPs(testMeetupId);
    }

    @Test
    void getEventWithRSVPsShouldReturnEmptyWhenNotFound() {
        // Given
        when(meetupAPIClient.getEventWithRSVPs(testMeetupId)).thenReturn(Optional.empty());

        // When
        Optional<EventRecordWithRSVPs> result = meetupAPIClientAdapter.getEventWithRSVPs(testMeetupId);

        // Then
        assertFalse(result.isPresent());
        verify(meetupAPIClient).getEventWithRSVPs(testMeetupId);
    }
}