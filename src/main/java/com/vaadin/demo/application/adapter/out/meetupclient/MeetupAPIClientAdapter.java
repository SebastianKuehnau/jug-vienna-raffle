package com.vaadin.demo.application.adapter.out.meetupclient;

import com.vaadin.demo.application.adapter.Mapper;
import com.vaadin.demo.application.adapter.out.meetupclient.MeetupAPIClient.MeetupEventWithRSVPs;
import com.vaadin.demo.application.domain.model.EventRecord;
import com.vaadin.demo.application.domain.model.EventRecordWithRSVPs;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class MeetupAPIClientAdapter {

  private final MeetupAPIClient client;

  public MeetupAPIClientAdapter(MeetupAPIClient client) {
    this.client = client;
  }


  public Optional<EventRecord> getEvent(String meetupEventId) {
    return client.getEvent(meetupEventId).map(v -> Mapper.toEventRecord(v));
  }

  public Optional<EventRecordWithRSVPs> getEventWithRSVPs(String meetupId) {
    return client.getEventWithRSVPs(meetupId).map(v -> Mapper.toEventRecordWithRSVPs(v));
  }
}
