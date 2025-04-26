package com.vaadin.demo.application.data;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.OffsetDateTime;

/**
 * Entity to store Meetup member information for a specific event
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"meetup_event_id", "meetup_id"}))
public class MeetupMember extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meetup_event_id")
    @ToString.Exclude
    private MeetupEvent meetupEvent;

    private String meetupId;
    private String name;
    private String email;
    private String rsvpId;
    private Boolean isOrganizer;
    private Boolean hasEnteredRaffle;

    /**
     * RSVP status for this event (YES or NO)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "rsvp_status")
    private RSVPStatus rsvpStatus = RSVPStatus.YES;

    /**
     * Attendance status for this event
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "attendance_status")
    private AttendanceStatus attendanceStatus = AttendanceStatus.UNKNOWN;

    @Column(name = "last_updated")
    private OffsetDateTime lastUpdated;

    /**
     * Updates the member data from a Meetup API response
     */
    public void updateFromApiResponse(com.vaadin.demo.application.services.meetup.MeetupService.Member apiMember) {
        this.meetupId = apiMember.id();
        this.name = apiMember.name();
        this.email = apiMember.email();
        this.rsvpId = apiMember.rsvp_id();
        this.isOrganizer = apiMember.isOrganizer();
        this.hasEnteredRaffle = apiMember.hasEnteredRaffle();
        // Don't update RSVP status or attendance info as they might have been manually set
        this.lastUpdated = OffsetDateTime.now();
    }

    /**
     * Mark this member as having attended the event
     */
    public void markAsAttended() {
        this.attendanceStatus = AttendanceStatus.ATTENDED;
        this.lastUpdated = OffsetDateTime.now();
    }

    /**
     * Mark this member as a no-show
     */
    public void markAsNoShow() {
        this.attendanceStatus = AttendanceStatus.NO_SHOW;
        this.lastUpdated = OffsetDateTime.now();
    }

    /**
     * Reset attendance information
     */
    public void resetAttendanceInfo() {
        this.attendanceStatus = AttendanceStatus.UNKNOWN;
        this.lastUpdated = OffsetDateTime.now();
    }

    /**
     * Enum representing RSVP status (simplified to YES/NO)
     */
    public enum RSVPStatus {
        YES,    // Confirmed attendance
        NO,      // Declined attendance
    }

    /**
     * Enum representing attendance status
     */
    public enum AttendanceStatus {
        UNKNOWN,    // Attendance status is unknown
        ATTENDED,   // Member attended the event
        NO_SHOW     // Member did not show up
    }
}