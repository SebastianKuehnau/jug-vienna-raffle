package com.vaadin.demo.application.adapter.meetupclient.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

class GetEventWrapperTest {

  @Test
  void parse() throws Exception{
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    String payload = new String(Files.readAllBytes(Paths.get("src/test/resources/getEvent.json")));

    var event = GetEventWrapper.parse(payload, mapper);

    assertNotNull(event);
    assertEquals("305897281", event.id());
    assertEquals("305897281", event.token());
    assertEquals("Java on AWS Special", event.title());
    assertEquals("https://www.meetup.com/java-vienna/events/305897281/", event.eventUrl());
    assertEquals("ACTIVE", event.status());
  }
}