package com.vaadin.demo.application.adapter.in.views.admin;

import com.vaadin.demo.application.domain.model.ParticipantRecord;
import com.vaadin.demo.application.domain.model.ParticipantRecord.RsvpStatus;
import com.vaadin.demo.application.domain.model.ParticipantRecord.AttendanceStatus;
import com.vaadin.demo.application.domain.model.PrizeRecord;
import com.vaadin.demo.application.application.port.in.MeetupApplicationService;
import com.vaadin.demo.application.application.port.in.RaffleApplicationService;
import com.vaadin.demo.application.adapter.in.views.admin.details.DetailsMainLayout;
import com.vaadin.demo.application.adapter.in.views.admin.details.PrizesCrudSubView;
import com.vaadin.demo.application.adapter.in.views.spinwheel.component.ReactSpinWheel;
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
@Route("raffle-admin/spin-wheel/:prizeId([0-9]+)")
@com.vaadin.flow.server.auth.AnonymousAllowed
public class SpinWheelView extends Div implements BeforeEnterObserver {

    private final RaffleApplicationService raffleService;
    private final MeetupApplicationService meetupService;
    private final ReactSpinWheel reactSpinWheel;

    private Map<String, ParticipantRecord> participants = new HashMap<>();
    private Optional<PrizeRecord> currentPrize;

    public SpinWheelView(RaffleApplicationService raffleService, MeetupApplicationService meetupService) {
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
        public WinnerDialog(ParticipantRecord winner) {
            String memberName = winner.member() != null ? winner.member().name() : "Unknown";
            String memberId = winner.member() != null ? winner.member().meetupId() : "Unknown";

            add(new H1("Winner: " + memberName + " (" + memberId + ")"));

            var acceptButton = new Button("Accept Prize", e -> {
                try {
                    System.out.println("DEBUG: Accept Prize - Participant ID: " + winner.id());
                    System.out.println("DEBUG: Before update - Attendance status: " + winner.attendanceStatus());

                    // Note: Records are immutable so we use withX methods instead
                    // The updatedWinner won't be used here directly as the service call below will handle
                    // the actual database update with the participant ID
                    var updatedWinner = winner.withAttendanceStatus(AttendanceStatus.ATTENDED).withEnteredRaffle();

                    // Award prize - this should also save the participant
                    currentPrize.ifPresent(prize -> {
                        System.out.println("DEBUG: Awarding prize ID " + prize.id() + " to participant");
                        PrizeRecord updatedPrize = raffleService.awardPrize(prize, winner);
                        System.out.println("DEBUG: Prize updated, winner attendance status: " +
                                (updatedPrize.winner() != null ? updatedPrize.winner().attendanceStatus() : "null"));
                    });

                    System.out.println("DEBUG: After update - Attendance status: " + winner.attendanceStatus());

                    close();
                    RouteParam routeParam = new RouteParam(DetailsMainLayout.RAFFLE_ID_PARAMETER, currentPrize.get().raffle().id());
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
                    System.out.println("DEBUG: Decline Prize - Participant ID: " + winner.id());
                    System.out.println("DEBUG: Before update - Attendance status: " + winner.attendanceStatus());

                    // Records are immutable - we'll let the service call below
                    // handle the actual database update
                    ParticipantRecord updated = meetupService.markParticipantAttendedAndEnteredRaffle(winner.id());

                    System.out.println("DEBUG: After update - Attendance status: " + updated.attendanceStatus());
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
                    System.out.println("DEBUG: No Show - Participant ID: " + winner.id());
                    System.out.println("DEBUG: Before update - Attendance status: " + winner.attendanceStatus());

                    // Records are immutable - we'll let the service call below
                    // handle the actual database update
                    ParticipantRecord updated = meetupService.markParticipantNoShowAndEnteredRaffle(winner.id());

                    System.out.println("DEBUG: After update - Attendance status: " + updated.attendanceStatus());
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
                    System.out.println("DEBUG: Doesn't meet requirements - Participant ID: " + winner.id());
                    System.out.println("DEBUG: Before update - Attendance status: " + winner.attendanceStatus());

                    // Records are immutable - we'll let the service call below
                    // handle the actual database update
                    ParticipantRecord updated = meetupService.markParticipantAttendedAndEnteredRaffle(winner.id());

                    System.out.println("DEBUG: After update - Attendance status: " + updated.attendanceStatus());
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
    public void beforeEnter(BeforeEnterEvent event) {
        // Get the prize ID from the URL parameter
        Optional<String> prizeIdParam = event.getRouteParameters().get("prizeId");

        if (prizeIdParam.isEmpty()) {
            // If no prize ID is provided, show an error and redirect back
            Notification.show("No prize selected for raffle", 3000, Notification.Position.MIDDLE);
            event.forwardTo("raffle-admin");
            return;
        }

        try {
            Long prizeId = Long.parseLong(prizeIdParam.get());

            // Get the prize from the database
            Optional<PrizeRecord> optionalPrize = raffleService.getPrizeById(prizeId);
            if (optionalPrize.isEmpty()) {
                Notification.show("Prize not found: " + prizeId, 3000, Notification.Position.MIDDLE);
                event.forwardTo("raffle-admin");
                return;
            }

            this.currentPrize = optionalPrize;

            // Load raffle participants from the database
            optionalPrize.flatMap(prize -> {
                if (prize.raffle() == null) {
                    System.out.println("DEBUG: Prize has no raffle associated with it: " + prize.id());
                    return Optional.empty();
                }
                System.out.println("DEBUG: Found raffle: " + prize.raffle().id());
                return Optional.of(prize.raffle());
            })
            .ifPresent(raffle -> {
                // Get eligible participants for the raffle
                if (raffle.event() != null) {
                    System.out.println("DEBUG: Raffle has event: " + raffle.event().id() + " / " + raffle.event().meetupId());

                    // For testing, let's get all participants first regardless of eligibility
                    List<ParticipantRecord> allParticipants = meetupService.getParticipantsForEvent(raffle.event());
                    System.out.println("DEBUG: Total participants for event: " + allParticipants.size());

                    // Now get eligible participants
                    List<ParticipantRecord> eligibleParticipants = meetupService.getRaffleEligibleParticipants(raffle.event());
                    System.out.println("DEBUG: Eligible participants: " + eligibleParticipants.size());

                    // Clear previous participants
                    this.participants.clear();

                    // Add all participants to the map for now (temporarily for testing)
                    for (ParticipantRecord participant : allParticipants) {
                        if (participant.member() != null && participant.member().meetupId() != null) {
                            this.participants.put(participant.member().meetupId(), participant);
                            System.out.println("DEBUG: Added participant: " + participant.member().name() +
                                ", RSVP: " + participant.rsvpStatus() +
                                ", Attendance: " + participant.attendanceStatus() +
                                ", Organizer: " + participant.isOrganizer() +
                                ", Entered Raffle: " + participant.hasEnteredRaffle());
                        }
                    }

                    // Filter participants for the raffle - only those who RSVP'd YES and haven't been in raffle already
                    List<String> eligibleMemberIds = allParticipants.stream()
                        .filter(p -> p.member() != null)
                        .filter(p -> p.rsvpStatus() == RsvpStatus.YES) // Only include YES RSVPs
                        .filter(p -> !p.isOrganizer()) // Exclude organizers
                        .filter(p -> !p.hasEnteredRaffle()) // Exclude those who already participated
                        .map(p -> p.member().meetupId())
                        .collect(Collectors.toList());

                    System.out.println("DEBUG: Setting spin wheel with " + eligibleMemberIds.size() + " eligible participants");

                    // Set the wheel with our eligible participants
                    reactSpinWheel.setItems(eligibleMemberIds);
                } else {
                    System.out.println("DEBUG: Raffle has no event associated with it: " + raffle.id());
                }
            });

        } catch (NumberFormatException e) {
            Notification.show("Invalid prize ID: " + prizeIdParam.get(), 3000, Notification.Position.MIDDLE);
            event.forwardTo("raffle-admin");
        }
    }
}