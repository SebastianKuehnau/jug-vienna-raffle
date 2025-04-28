package com.vaadin.demo.application.adapter.out.meetupclient.impl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.demo.application.adapter.out.meetupclient.MeetupClient.MeetupEventWithRSVPs;
import com.vaadin.demo.application.adapter.out.meetupclient.MeetupClient.MemberPhoto;
import com.vaadin.demo.application.adapter.out.meetupclient.MeetupClient.RSVP;
import java.time.OffsetDateTime;
import java.util.List;

public class MeetupEventWithRsvpWrapper {
  public EventData data;

  public static MeetupEventWithRSVPs parse(String body, ObjectMapper mapper) {
    try {
      MeetupEventWithRsvpWrapper wrapper = mapper.readValue(body, MeetupEventWithRsvpWrapper.class);

      return wrapper.getEvent();
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  public MeetupEventWithRSVPs getEvent() {
    return data.event.toRecord();
  }

  public static class EventData {
    public EventWithNestedRSVP event;
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class EventWithNestedRSVP {
    public String id;
    public String token;
    public String title;
    public OffsetDateTime dateTime;
    public String description;

    @JsonProperty("rsvps")
    public RSVPWrapper rsvps;

    public MeetupEventWithRSVPs toRecord() {
      List<RSVP> rsvpList = rsvps.edges.stream()
          .map(edge -> edge.node.member.toRecord())
          .toList();

      return new MeetupEventWithRSVPs(id, token, title, dateTime, description, rsvpList);
    }
  }

  public static class RSVPWrapper {
    public List<RSVPEdge> edges;
  }

  public static class RSVPEdge {
    public RSVPNode node;
  }

  public static class RSVPNode {
    public String id;
    public boolean isFirstEvent;
    public boolean isHost;
    public RSVPMember member;
  }

  public static class RSVPMember {
    public String id;
    public String email;
    public String gender;
    public String memberUrl;
    public String name;
    public String state;
    public String status;
    public String username;
    public MemberPhoto memberPhoto;

    public RSVP toRecord() {
      return new RSVP(id, email, gender, memberUrl, name, state, status, username, memberPhoto);
    }
  }
}