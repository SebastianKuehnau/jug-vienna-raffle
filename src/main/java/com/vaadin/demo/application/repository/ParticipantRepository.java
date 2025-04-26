package com.vaadin.demo.application.repository;

import com.vaadin.demo.application.data.MeetupEvent;
import com.vaadin.demo.application.data.Member;
import com.vaadin.demo.application.data.Participant;
import com.vaadin.demo.application.data.Participant.AttendanceStatus;
import com.vaadin.demo.application.data.Participant.RSVPStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    
    /**
     * Find a participant by meetup event and member
     */
    Optional<Participant> findByMeetupEventAndMember(MeetupEvent meetupEvent, Member member);
    
    /**
     * Find all participants for a specific meetup event
     */
    List<Participant> findByMeetupEvent(MeetupEvent meetupEvent);
    
    /**
     * Find all participants for a specific meetup event with a specific RSVP status
     */
    List<Participant> findByMeetupEventAndRsvpStatus(MeetupEvent meetupEvent, RSVPStatus rsvpStatus);
    
    /**
     * Find all participants for a specific meetup event with a specific attendance status
     */
    List<Participant> findByMeetupEventAndAttendanceStatus(MeetupEvent meetupEvent, AttendanceStatus attendanceStatus);
    
    /**
     * Find all participants eligible for a raffle (RSVP=YES, not organizers, and attended)
     */
    List<Participant> findByMeetupEventAndRsvpStatusAndIsOrganizerAndAttendanceStatus(
            MeetupEvent meetupEvent, 
            RSVPStatus rsvpStatus, 
            Boolean isOrganizer, 
            AttendanceStatus attendanceStatus);
}