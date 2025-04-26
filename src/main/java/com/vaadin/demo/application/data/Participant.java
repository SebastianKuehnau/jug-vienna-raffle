package com.vaadin.demo.application.data;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.OffsetDateTime;

/**
 * Entity representing a participant in a specific Meetup event
 * This is the join table between Member and MeetupEvent
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"meetup_event_id", "member_id"}))
public class Participant extends AbstractEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "meetup_event_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private MeetupEvent meetupEvent;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "member_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Member member;

    private String rsvpId;
    private Boolean isOrganizer = false;
    private Boolean hasEnteredRaffle = false;

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
    private OffsetDateTime lastUpdated = OffsetDateTime.now();

    @OneToOne(mappedBy = "winner", fetch = FetchType.EAGER)
    private Prize wonPrize;

    /**
     * Mark this participant as having attended the event
     */
    public void markAsAttended() {
        this.attendanceStatus = AttendanceStatus.ATTENDED;
        this.lastUpdated = OffsetDateTime.now();
    }

    /**
     * Mark this participant as a no-show
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
        NO      // Declined attendance
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