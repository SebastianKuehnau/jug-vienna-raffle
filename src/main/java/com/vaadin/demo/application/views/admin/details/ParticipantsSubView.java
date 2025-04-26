package com.vaadin.demo.application.views.admin.details;

import com.vaadin.demo.application.data.Member;
import com.vaadin.demo.application.data.MeetupEvent;
import com.vaadin.demo.application.data.Participant;
import com.vaadin.demo.application.domain.port.MeetupPort;
import com.vaadin.demo.application.domain.port.RafflePort;
import com.vaadin.demo.application.views.admin.components.SyncMembersButton;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Route(value = "participants", layout = DetailsMainLayout.class)
@com.vaadin.flow.server.auth.AnonymousAllowed
public class ParticipantsSubView extends VerticalLayout implements BeforeEnterObserver {

    private final Grid<ParticipantViewModel> grid;
    private final RafflePort raffleService;
    private final MeetupPort meetupService;
    private String currentMeetupEventId;
    private Long currentRaffleId;

    // View model for participants
    public static class ParticipantViewModel {
        private final String name;
        private final String email;
        private final boolean organizer;  // Note: field name matches method without "is" prefix
        private final boolean enteredRaffle;  // Note: field name matches method without "has" prefix
        private final Participant.RSVPStatus rsvpStatus;
        private final Participant.AttendanceStatus attendanceStatus;

        public ParticipantViewModel(Participant participant) {
            Member member = participant.getMember();
            this.name = member != null ? member.getName() : "";
            this.email = member != null ? member.getEmail() : "";
            this.organizer = participant.getIsOrganizer() != null && participant.getIsOrganizer();
            this.enteredRaffle = participant.getHasEnteredRaffle() != null && participant.getHasEnteredRaffle();
            this.rsvpStatus = participant.getRsvpStatus();
            this.attendanceStatus = participant.getAttendanceStatus();
        }

        public String getName() { return name; }
        public String getEmail() { return email; }
        public boolean isOrganizer() { return organizer; }
        public boolean hasEnteredRaffle() { return enteredRaffle; }
        public Participant.RSVPStatus getRsvpStatus() { return rsvpStatus; }
        public Participant.AttendanceStatus getAttendanceStatus() { return attendanceStatus; }
    }

    public ParticipantsSubView(RafflePort raffleService, MeetupPort meetupService) {
        this.raffleService = raffleService;
        this.meetupService = meetupService;
        
        // Create header layout with buttons
        HorizontalLayout buttonLayout = new HorizontalLayout();
        
        SyncMembersButton syncButton = new SyncMembersButton(meetupService, "");
        syncButton.setVisible(false); // Hide until we have a meetup ID
        
        Button refreshButton = new Button("Refresh");
        refreshButton.addClickListener(e -> refreshParticipants());
        refreshButton.setVisible(false); // Hide until we have data to refresh
        
        buttonLayout.add(syncButton, refreshButton);
        
        grid = new Grid<>(ParticipantViewModel.class);
        
        // Define columns explicitly to match the exact property names
        grid.addColumn(ParticipantViewModel::getName).setHeader("Name");
        grid.addColumn(ParticipantViewModel::getEmail).setHeader("Email");
        grid.addColumn(ParticipantViewModel::isOrganizer).setHeader("Organizer");
        grid.addColumn(ParticipantViewModel::hasEnteredRaffle).setHeader("Entered Raffle");
        grid.addColumn(ParticipantViewModel::getRsvpStatus).setHeader("RSVP Status");
        grid.addColumn(ParticipantViewModel::getAttendanceStatus).setHeader("Attendance");
        
        add(buttonLayout, grid);
        setSizeFull();
    }

    private void refreshParticipants() {
        if (currentMeetupEventId != null) {
            meetupService.getEventByMeetupId(currentMeetupEventId)
                .ifPresent(event -> {
                    List<Participant> participants = meetupService.getParticipantsForEvent(event);
                    updateContent(participants);
                    
                    // Update the button
                    getChildren()
                        .filter(c -> c instanceof HorizontalLayout)
                        .findFirst()
                        .ifPresent(layout -> {
                            ((HorizontalLayout)layout).getChildren()
                                .filter(c -> c instanceof Button && !(c instanceof SyncMembersButton))
                                .findFirst()
                                .ifPresent(c -> c.setVisible(true));
                        });
                });
        }
    }

    public void updateContent(List<Participant> participants) {
        List<ParticipantViewModel> viewModels = participants.stream()
            .map(ParticipantViewModel::new)
            .toList();
        grid.setItems(viewModels);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        RouteParameters parameters = event.getRouteParameters();
        parameters.get(DetailsMainLayout.RAFFLE_ID_PARAMETER)
                .flatMap(raffleId -> {
                    this.currentRaffleId = Long.parseLong(raffleId);
                    return raffleService.getRaffleById(this.currentRaffleId);
                })
                .ifPresent(raffle -> {
                    // Store Meetup ID for sync button
                    this.currentMeetupEventId = raffle.getMeetup_event_id();
                    
                    // Update the sync button
                    getChildren()
                        .filter(c -> c instanceof HorizontalLayout)
                        .findFirst()
                        .ifPresent(layout -> {
                            HorizontalLayout buttonLayout = (HorizontalLayout) layout;
                            buttonLayout.getChildren()
                                .filter(c -> c instanceof SyncMembersButton)
                                .findFirst()
                                .ifPresent(c -> {
                                    buttonLayout.remove(c);
                                    SyncMembersButton newSyncButton = new SyncMembersButton(
                                            meetupService, 
                                            currentMeetupEventId
                                    );
                                    buttonLayout.addComponentAtIndex(0, newSyncButton);
                                });
                        });
                    
                    // Get button for refresh
                    getChildren()
                        .filter(c -> c instanceof HorizontalLayout)
                        .findFirst()
                        .ifPresent(layout -> {
                            ((HorizontalLayout)layout).getChildren()
                                .filter(c -> c instanceof Button && !(c instanceof SyncMembersButton))
                                .findFirst()
                                .ifPresent(c -> c.setVisible(true));
                        });
                    
                    // Refresh participants from database
                    refreshParticipants();
                });
    }
}