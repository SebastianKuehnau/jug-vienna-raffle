package com.vaadin.demo.application.adapter.out.persistence.repository;

import com.vaadin.demo.application.adapter.out.persistence.data.MeetupEvent;
import com.vaadin.demo.application.adapter.out.persistence.data.MeetupMember;
import com.vaadin.demo.application.adapter.out.persistence.data.MeetupMember.AttendanceStatus;
import com.vaadin.demo.application.adapter.out.persistence.data.MeetupMember.RSVPStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MeetupMemberRepository extends JpaRepository<MeetupMember, Long> {

    /**
     * Find a member by meetup event and meetup ID
     */
    Optional<MeetupMember> findByMeetupEventAndMeetupId(MeetupEvent meetupEvent, String meetupId);

    /**
     * Find all members for a specific meetup event
     */
    List<MeetupMember> findByMeetupEvent(MeetupEvent meetupEvent);

    /**
     * Find all members for a specific meetup event with a specific RSVP status
     */
    List<MeetupMember> findByMeetupEventAndRsvpStatus(MeetupEvent meetupEvent, RSVPStatus rsvpStatus);

    /**
     * Find all members for a specific meetup event with a specific attendance status
     */
    List<MeetupMember> findByMeetupEventAndAttendanceStatus(MeetupEvent meetupEvent, AttendanceStatus attendanceStatus);

    /**
     * Find all members eligible for a raffle (RSVP=YES, not organizers, and attended)
     */
    List<MeetupMember> findByMeetupEventAndRsvpStatusAndIsOrganizerAndAttendanceStatus(
            MeetupEvent meetupEvent,
            RSVPStatus rsvpStatus,
            Boolean isOrganizer,
            AttendanceStatus attendanceStatus);
}