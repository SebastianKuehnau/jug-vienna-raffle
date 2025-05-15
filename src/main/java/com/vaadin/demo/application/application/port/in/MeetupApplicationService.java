package com.vaadin.demo.application.application.port.in;

import com.vaadin.demo.application.domain.model.EventFormRecord;
import com.vaadin.demo.application.domain.model.EventRecord;
import com.vaadin.demo.application.domain.model.ParticipantFormRecord;
import com.vaadin.demo.application.domain.model.ParticipantRecord;
import java.util.List;
import java.util.Optional;

public interface MeetupApplicationService {

  Optional<EventRecord> getEventByMeetupId(String meetupId);

  List<EventRecord> getAllEvents();

  List<ParticipantRecord> getParticipantsForEvent(EventRecord event);

  List<ParticipantRecord> getRaffleEligibleParticipants(EventRecord event);

  EventRecord importEvent(String meetupId);

  int syncEventMembers(Long eventId);

  int syncEventMembersByMeetupId(String meetupId);

  ParticipantRecord markParticipantEnteredRaffle(Long participantId);

  ParticipantRecord markParticipantAttendedAndEnteredRaffle(Long participantId);

  ParticipantRecord markParticipantNoShowAndEnteredRaffle(Long participantId);

  void resetRaffleEntryForEvent(EventRecord event);

  Optional<EventFormRecord> getEventFormById(Long id);

  List<EventFormRecord> getAllEventForms();

  EventFormRecord saveEventForm(EventFormRecord eventForm);

  List<ParticipantFormRecord> getParticipantFormsForEvent(Long eventId);

  Optional<ParticipantFormRecord> getParticipantFormById(Long id);

  ParticipantFormRecord updateParticipantRaffleStatus(Long participantId, boolean hasEnteredRaffle);

  ParticipantFormRecord updateParticipantAttendanceStatus(Long participantId,
      String attendanceStatus);
}
