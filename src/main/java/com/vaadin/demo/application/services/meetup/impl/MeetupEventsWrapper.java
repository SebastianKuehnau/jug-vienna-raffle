package com.vaadin.demo.application.services.meetup.impl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.demo.application.services.meetup.MeetupService.MeetupEvent;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MeetupEventsWrapper {

  public static Set<MeetupEvent> parse(String json, ObjectMapper mapper) {
    try {
      MeetupEventsWrapper wrapper = mapper.readValue(json, MeetupEventsWrapper.class);

      Set<MeetupEvent> events = wrapper.getEvents();

      return events;
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
  @JsonProperty("data")
  private DataWrapper data;

  public Set<MeetupEvent> getEvents() {
    return data.groupByUrlname.events.edges.stream()
        .map(edge -> edge.node.toRecord())
        .collect(Collectors.toSet());
  }

  private static class DataWrapper {
    @JsonProperty("groupByUrlname")
    private Group groupByUrlname;
  }

  private static class Group {
    public String id;
    public EventList events;
  }

  private static class EventList {
    public List<EventEdge> edges;
  }

  private static class EventEdge {
    public EventNode node;
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  private static class EventNode {
    public String id;
    public String token;
    public String title;
    public OffsetDateTime dateTime;
    public String description;
    public String eventUrl;
    public String status;

    public MeetupEvent toRecord() {

      //TODO: members needs to be fetched as well
      return new MeetupEvent(id, token, title, dateTime, description ,eventUrl, status, Set.of());
    }
  }
}