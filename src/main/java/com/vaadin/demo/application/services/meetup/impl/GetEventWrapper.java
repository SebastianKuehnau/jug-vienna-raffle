package com.vaadin.demo.application.services.meetup.impl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.demo.application.services.meetup.MeetupService.MeetupEvent;

public class GetEventWrapper {
  public static MeetupEvent parse(String body, ObjectMapper mapper) {
    try {
      GetEventWrapper wrapper = mapper.readValue(body, GetEventWrapper.class);

      return wrapper.getEvent();
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  @JsonProperty("data")
  private EventData data;

  public MeetupEvent getEvent() {
    return data.event;
  }

  private static class EventData {
    @JsonProperty("event")
    @JsonIgnoreProperties(ignoreUnknown = true)
    private MeetupEvent event;
  }
}