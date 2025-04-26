package com.vaadin.demo.application.domain.model;

import com.vaadin.demo.application.data.Participant;

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
     * Convert JPA AttendanceStatus to domain model AttendanceStatus
     */
    public static AttendanceStatus fromJpaAttendanceStatus(Participant.AttendanceStatus status) {
        if (status == null) return AttendanceStatus.UNKNOWN;
        
        return switch (status) {
            case ATTENDED -> AttendanceStatus.ATTENDED;
            case NO_SHOW -> AttendanceStatus.NO_SHOW;
            case UNKNOWN -> AttendanceStatus.UNKNOWN;
        };
    }
    
    /**
     * Convert domain model AttendanceStatus to JPA AttendanceStatus
     */
    public static Participant.AttendanceStatus toJpaAttendanceStatus(AttendanceStatus status) {
        if (status == null) return Participant.AttendanceStatus.UNKNOWN;
        
        return switch (status) {
            case ATTENDED -> Participant.AttendanceStatus.ATTENDED;
            case NO_SHOW -> Participant.AttendanceStatus.NO_SHOW;
            case UNKNOWN -> Participant.AttendanceStatus.UNKNOWN;
        };
    }
    
    /**
     * Convert JPA RsvpStatus to domain model RsvpStatus
     */
    public static RsvpStatus fromJpaRsvpStatus(Participant.RSVPStatus status) {
        if (status == null) return RsvpStatus.NO;
        
        return switch (status) {
            case YES -> RsvpStatus.YES;
            case NO -> RsvpStatus.NO;
        };
    }
    
    /**
     * Convert domain model RsvpStatus to JPA RsvpStatus
     */
    public static Participant.RSVPStatus toJpaRsvpStatus(RsvpStatus status) {
        if (status == null) return Participant.RSVPStatus.NO;
        
        return switch (status) {
            case YES -> Participant.RSVPStatus.YES;
            case NO -> Participant.RSVPStatus.NO;
        };
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