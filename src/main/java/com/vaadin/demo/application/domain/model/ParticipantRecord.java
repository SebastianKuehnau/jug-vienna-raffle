package com.vaadin.demo.application.domain.model;

/**
 * Immutable domain object representing a participant
 */
public record ParticipantRecord(
    Long id,
    MemberRecord member,
    EventRecord event,
    String rsvpId,
    boolean isOrganizer,
    boolean hasEnteredRaffle,
    RsvpStatus rsvpStatus,
    AttendanceStatus attendanceStatus
) {
    /**
     * Enum representing RSVP status (simplified to YES/NO)
     */
    public enum RsvpStatus {
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

    /**
     * Create a participant with updated attendance status
     */
    public ParticipantRecord withAttendanceStatus(AttendanceStatus newStatus) {
        return new ParticipantRecord(
            this.id,
            this.member,
            this.event,
            this.rsvpId,
            this.isOrganizer,
            this.hasEnteredRaffle,
            this.rsvpStatus,
            newStatus
        );
    }

    /**
     * Create a participant marked as entered in raffle
     */
    public ParticipantRecord withEnteredRaffle() {
        return new ParticipantRecord(
            this.id,
            this.member,
            this.event,
            this.rsvpId,
            this.isOrganizer,
            true, // Has entered raffle
            this.rsvpStatus,
            this.attendanceStatus
        );
    }
}