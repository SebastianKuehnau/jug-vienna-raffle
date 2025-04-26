package com.vaadin.demo.application.views.admin;

import com.vaadin.demo.application.data.Member;
import com.vaadin.demo.application.data.Participant;
import com.vaadin.demo.application.data.Participant.RSVPStatus;
import com.vaadin.demo.application.data.Participant.AttendanceStatus;
import com.vaadin.demo.application.data.Prize;
import com.vaadin.demo.application.domain.port.MeetupPort;
import com.vaadin.demo.application.domain.port.RafflePort;
import com.vaadin.demo.application.views.admin.details.DetailsMainLayout;
import com.vaadin.demo.application.views.admin.details.PrizesCrudSubView;
import com.vaadin.demo.application.views.spinwheel.component.ReactSpinWheel;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@PageTitle("JUG Vienna Raffle Spin Wheel")
@Route("raffle-admin/spin-wheel")
@com.vaadin.flow.server.auth.AnonymousAllowed
public class SpinWheelView extends Div implements HasUrlParameter<Long> {

    private final RafflePort raffleService;
    private final MeetupPort meetupService;
    private final ReactSpinWheel reactSpinWheel;

    private Map<String, Participant> participants = new HashMap<>();
    private Optional<Prize> currentPrize;

    public SpinWheelView(RafflePort raffleService, MeetupPort meetupService) {
        this.raffleService = raffleService;
        this.meetupService = meetupService;
        
        reactSpinWheel = new ReactSpinWheel();
        reactSpinWheel.addOnFinishSpin(this::handleSpinResult);
        reactSpinWheel.setSizeFull();
        add(reactSpinWheel);
    }
    
    private void handleSpinResult(String participantId) {
        var winner = participants.get(participantId);
        if (winner != null) {
            var winnerDialog = new WinnerDialog(winner);
            winnerDialog.open();
        }
    }

    public class WinnerDialog extends Dialog {
        public WinnerDialog(Participant winner) {
            String memberName = winner.getMember() != null ? winner.getMember().getName() : "Unknown";
            String memberId = winner.getMember() != null ? winner.getMember().getMeetupId() : "Unknown";
            
            add(new H1("Winner: " + memberName + " (" + memberId + ")"));
            
            var acceptButton = new Button("Accept Prize", e -> {
                try {
                    System.out.println("DEBUG: Accept Prize - Participant ID: " + winner.getId());
                    System.out.println("DEBUG: Before update - Attendance status: " + winner.getAttendanceStatus());
                    
                    // Set the participant status and flags
                    winner.setAttendanceStatus(AttendanceStatus.ATTENDED);
                    winner.setHasEnteredRaffle(true);
                    
                    // Award prize - this should also save the participant
                    currentPrize.ifPresent(prize -> {
                        System.out.println("DEBUG: Awarding prize ID " + prize.getId() + " to participant");
                        Prize updatedPrize = raffleService.awardPrize(prize, winner);
                        System.out.println("DEBUG: Prize updated, winner attendance status: " + 
                                (updatedPrize.getWinner() != null ? updatedPrize.getWinner().getAttendanceStatus() : "null"));
                    });
                    
                    System.out.println("DEBUG: After update - Attendance status: " + winner.getAttendanceStatus());
                    
                    close();
                    RouteParam routeParam = new RouteParam(DetailsMainLayout.RAFFLE_ID_PARAMETER, currentPrize.get().getRaffle().getId());
                    UI.getCurrent().navigate(PrizesCrudSubView.class, routeParam);
                } catch (Exception ex) {
                    System.err.println("ERROR when accepting prize: " + ex.getMessage());
                    ex.printStackTrace();
                    Notification.show("Error awarding prize: " + ex.getMessage(), 
                                    5000, Notification.Position.MIDDLE);
                }
            });
            acceptButton.addThemeVariants(
                    ButtonVariant.LUMO_PRIMARY,
                    ButtonVariant.LUMO_SUCCESS,
                    ButtonVariant.LUMO_LARGE);
                    
            var declineButton = new Button("Decline Prize", e -> {
                try {
                    System.out.println("DEBUG: Decline Prize - Participant ID: " + winner.getId());
                    System.out.println("DEBUG: Before update - Attendance status: " + winner.getAttendanceStatus());
                    
                    // Update attendance status in the database
                    winner.setAttendanceStatus(AttendanceStatus.ATTENDED);
                    winner.setHasEnteredRaffle(true);
                    Participant updated = meetupService.markParticipantAttendedAndEnteredRaffle(winner.getId());
                    
                    System.out.println("DEBUG: After update - Attendance status: " + updated.getAttendanceStatus());
                    close();
                } catch (Exception ex) {
                    System.err.println("ERROR when declining prize: " + ex.getMessage());
                    ex.printStackTrace();
                    Notification.show("Error updating participant: " + ex.getMessage(), 
                                    5000, Notification.Position.MIDDLE);
                }
            });
            declineButton.addThemeVariants(
                    ButtonVariant.LUMO_ERROR,
                    ButtonVariant.LUMO_PRIMARY,
                    ButtonVariant.LUMO_LARGE);

            var noShowButton = new Button("No show", e -> {
                try {
                    System.out.println("DEBUG: No Show - Participant ID: " + winner.getId());
                    System.out.println("DEBUG: Before update - Attendance status: " + winner.getAttendanceStatus());
                    
                    // Update attendance status in the database
                    winner.setAttendanceStatus(AttendanceStatus.NO_SHOW);
                    winner.setHasEnteredRaffle(true);
                    Participant updated = meetupService.markParticipantNoShowAndEnteredRaffle(winner.getId());
                    
                    System.out.println("DEBUG: After update - Attendance status: " + updated.getAttendanceStatus());
                    close();
                } catch (Exception ex) {
                    System.err.println("ERROR when marking no-show: " + ex.getMessage());
                    ex.printStackTrace();
                    Notification.show("Error updating participant: " + ex.getMessage(), 
                                    5000, Notification.Position.MIDDLE);
                }
            });
            noShowButton.addThemeVariants(
                ButtonVariant.LUMO_WARNING,
                ButtonVariant.LUMO_LARGE
            );

            var doesntMeetRequirementsButton = new Button("Doesn't meet requirements", e -> {
                try {
                    System.out.println("DEBUG: Doesn't meet requirements - Participant ID: " + winner.getId());
                    System.out.println("DEBUG: Before update - Attendance status: " + winner.getAttendanceStatus());
                    
                    // Update attendance status in the database
                    winner.setAttendanceStatus(AttendanceStatus.ATTENDED);
                    winner.setHasEnteredRaffle(true);
                    Participant updated = meetupService.markParticipantAttendedAndEnteredRaffle(winner.getId());
                    
                    System.out.println("DEBUG: After update - Attendance status: " + updated.getAttendanceStatus());
                    close();
                } catch (Exception ex) {
                    System.err.println("ERROR when marking doesn't meet requirements: " + ex.getMessage());
                    ex.printStackTrace();
                    Notification.show("Error updating participant: " + ex.getMessage(), 
                                    5000, Notification.Position.MIDDLE);
                }
            });
            doesntMeetRequirementsButton.addThemeVariants(
                ButtonVariant.LUMO_WARNING,
                ButtonVariant.LUMO_LARGE
            );
            
            add(new HorizontalLayout(declineButton, acceptButton, noShowButton, doesntMeetRequirementsButton));
            setModal(true);
            setCloseOnEsc(true);
            setCloseOnOutsideClick(true);
        }
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, Long prizeId) {
        // Get the prize from the database
        Optional<Prize> optionalPrize = raffleService.getPrizeById(prizeId);
        this.currentPrize = optionalPrize;

        // Load raffle participants from the database
        optionalPrize.flatMap(prize -> {
                if (prize.getRaffle() == null) {
                    System.out.println("DEBUG: Prize has no raffle associated with it: " + prize.getId());
                    return Optional.empty();
                }
                System.out.println("DEBUG: Found raffle: " + prize.getRaffle().getId());
                return Optional.of(prize.getRaffle());
            })
            .ifPresent(raffle -> {
                // Get eligible participants for the raffle
                if (raffle.getEvent() != null) {
                    System.out.println("DEBUG: Raffle has event: " + raffle.getEvent().getId() + " / " + raffle.getEvent().getMeetupId());
                    
                    // For testing, let's get all participants first regardless of eligibility
                    List<Participant> allParticipants = meetupService.getParticipantsForEvent(raffle.getEvent());
                    System.out.println("DEBUG: Total participants for event: " + allParticipants.size());
                    
                    // Now get eligible participants
                    List<Participant> eligibleParticipants = meetupService.getRaffleEligibleParticipants(raffle.getEvent());
                    System.out.println("DEBUG: Eligible participants: " + eligibleParticipants.size());
                    
                    // Clear previous participants
                    this.participants.clear();
                    
                    // Add all participants to the map for now (temporarily for testing)
                    for (Participant participant : allParticipants) {
                        if (participant.getMember() != null && participant.getMember().getMeetupId() != null) {
                            this.participants.put(participant.getMember().getMeetupId(), participant);
                            System.out.println("DEBUG: Added participant: " + participant.getMember().getName() + 
                                ", RSVP: " + participant.getRsvpStatus() + 
                                ", Attendance: " + participant.getAttendanceStatus() + 
                                ", Organizer: " + participant.getIsOrganizer() + 
                                ", Entered Raffle: " + participant.getHasEnteredRaffle());
                        }
                    }
                    
                    // Filter participants for the raffle - only those who RSVP'd YES and haven't been in raffle already
                    List<String> eligibleMemberIds = allParticipants.stream()
                        .filter(p -> p.getMember() != null)
                        .filter(p -> p.getRsvpStatus() == RSVPStatus.YES) // Only include YES RSVPs
                        .filter(p -> !Boolean.TRUE.equals(p.getIsOrganizer())) // Exclude organizers
                        .filter(p -> !Boolean.TRUE.equals(p.getHasEnteredRaffle())) // Exclude those who already participated
                        .map(p -> p.getMember().getMeetupId())
                        .collect(Collectors.toList());
                    
                    System.out.println("DEBUG: Setting spin wheel with " + eligibleMemberIds.size() + " eligible participants");
                    
                    // Set the wheel with our eligible participants
                    reactSpinWheel.setItems(eligibleMemberIds);
                } else {
                    System.out.println("DEBUG: Raffle has no event associated with it: " + raffle.getId());
                }
            });
    }
}