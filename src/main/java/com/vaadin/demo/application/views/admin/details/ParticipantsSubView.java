package com.vaadin.demo.application.views.admin.details;

import com.vaadin.demo.application.domain.model.MemberRecord;
import com.vaadin.demo.application.domain.model.EventRecord;
import com.vaadin.demo.application.domain.model.ParticipantRecord;
import com.vaadin.demo.application.application.service.MeetupApplicationService;
import com.vaadin.demo.application.application.service.RaffleApplicationService;
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
    private final RaffleApplicationService raffleService;
    private final MeetupApplicationService meetupService;
    private String currentMeetupEventId;
    private Long currentRaffleId;

    // View model for participants
    public static class ParticipantViewModel {
        private final String name;
        private final String email;
        private final boolean organizer;
        private final boolean enteredRaffle;
        private final ParticipantRecord.RsvpStatus rsvpStatus;
        private final ParticipantRecord.AttendanceStatus attendanceStatus;

        public ParticipantViewModel(ParticipantRecord participant) {
            MemberRecord member = participant.member();
            this.name = member != null ? member.name() : "";
            this.email = member != null ? member.email() : "";
            this.organizer = participant.isOrganizer();
            this.enteredRaffle = participant.hasEnteredRaffle();
            this.rsvpStatus = participant.rsvpStatus();
            this.attendanceStatus = participant.attendanceStatus();
        }

        public String getName() { return name; }
        public String getEmail() { return email; }
        public boolean isOrganizer() { return organizer; }
        public boolean hasEnteredRaffle() { return enteredRaffle; }
        public ParticipantRecord.RsvpStatus getRsvpStatus() { return rsvpStatus; }
        public ParticipantRecord.AttendanceStatus getAttendanceStatus() { return attendanceStatus; }
    }

    public ParticipantsSubView(RaffleApplicationService raffleService, MeetupApplicationService meetupService) {
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
                    List<ParticipantRecord> participants = meetupService.getParticipantsForEvent(event);
                    updateParticipantGrid(participants);
                    
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

    public void updateParticipantGrid(List<ParticipantRecord> participants) {
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
                    this.currentMeetupEventId = raffle.meetupId();
                    
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