package com.vaadin.demo.application.services.meetup.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.vaadin.demo.application.services.meetup.MeetupClient.MeetupEventWithRSVPs;
import com.vaadin.demo.application.services.meetup.MeetupClient.MemberPhoto;
import com.vaadin.demo.application.services.meetup.MeetupClient.RSVP;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

public class MeetupEventWithRsvpWrapperTest {

  @Test
  public void parse() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    String payload = new String(Files.readAllBytes(Paths.get("src/test/resources/getEventWithRSVP.json")));

    MeetupEventWithRSVPs event = MeetupEventWithRsvpWrapper.parse(payload, mapper);

    assertNotNull(event, "Parsed event object should not be null");

    // Check top-level fields
    assertEquals("000000001", event.id());
    assertEquals("000000001", event.token());
    assertEquals("Java on AWS Special", event.title());
    assertNotNull(event.dateTime());
    assertTrue(event.dateTime().isAfter(OffsetDateTime.now().minusYears(1)), "Event date should be recent");
    assertTrue(event.description().contains("Agenda"), "Description should contain 'Agenda'");

    // Check RSVPs
    List<RSVP> rsvps = event.rsvps();
    assertNotNull(rsvps, "RSVP list should not be null");
    assertFalse(rsvps.isEmpty(), "There should be at least one RSVP");

    for (RSVP rsvp : rsvps) {
      assertNotNull(rsvp.id(), "RSVP id should not be null");
      assertNotNull(rsvp.email(), "Email should not be null");
      assertTrue(rsvp.email().contains("@example.com"), "Email should be a dummy email");

      assertNotNull(rsvp.name(), "Name should not be null");
      assertNotNull(rsvp.username(), "Username should not be null");
      assertNotNull(rsvp.status(), "Status should not be null");

      // Check optional fields
      assertNotNull(rsvp.memberUrl(), "Member URL should not be null");
      if (rsvp.memberPhoto() != null) {
        MemberPhoto photo = rsvp.memberPhoto();
        assertNotNull(photo.id(), "Photo ID should not be null");
        assertTrue(photo.highResUrl().contains("dummyimage.com"), "High-res URL should be a dummy image link");
      }
    }
  }
}