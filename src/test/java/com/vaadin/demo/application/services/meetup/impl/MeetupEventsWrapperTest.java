package com.vaadin.demo.application.services.meetup.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

class MeetupEventsWrapperTest {

  @Test
  void parse() throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    String payload = new String(Files.readAllBytes(Paths.get("src/test/resources/getEvents.json")));

    var events = MeetupEventsWrapper.parse(payload, mapper);

    assertNotNull(events);
  }
}