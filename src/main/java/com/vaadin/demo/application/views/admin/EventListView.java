package com.vaadin.demo.application.views.admin;

import com.vaadin.demo.application.domain.model.EventRecord;
import com.vaadin.demo.application.domain.model.RaffleRecord;
import com.vaadin.demo.application.application.service.MeetupApplicationService;
import com.vaadin.demo.application.application.service.RaffleApplicationService;
import com.vaadin.demo.application.services.meetup.MeetupClient;
import com.vaadin.demo.application.views.MainLayout;
import com.vaadin.demo.application.views.admin.components.MeetupImportDialog;
import com.vaadin.demo.application.views.admin.components.SyncMembersButton;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Admin view that shows all Meetup events and their raffle status
 */
@AnonymousAllowed
@PageTitle("All Events")
@Route(value = "events", layout = MainLayout.class)
@Menu(order = 4, icon = LineAwesomeIconUrl.CALENDAR_ALT_SOLID, title = "All Events")
@SuppressWarnings("serial")
public class EventListView extends VerticalLayout {

    private final MeetupApplicationService meetupService;
    private final RaffleApplicationService raffleService;
    private final MeetupClient meetupApiClient;

    private final Grid<EventRecord> eventGrid = new Grid<>();

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public EventListView(MeetupApplicationService meetupService,
                        RaffleApplicationService raffleService,
                        MeetupClient meetupApiClient) {
        this.meetupService = meetupService;
        this.raffleService = raffleService;
        this.meetupApiClient = meetupApiClient;

        setSizeFull();
        setPadding(true);

        add(new H1("All Events"));

        configureGrid();

        // Create button layout
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setWidthFull();
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        // Import Meetup events button
        Button importMeetupButton = new Button("Import Meetup Events", new Icon(VaadinIcon.DOWNLOAD));
        importMeetupButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        importMeetupButton.addClickListener(this::importMeetupButtonClicked);

        // Refresh button
        Button refreshButton = new Button("Refresh", new Icon(VaadinIcon.REFRESH));
        refreshButton.addClickListener(e -> refreshEvents());

        buttonLayout.add(importMeetupButton, refreshButton);
        add(buttonLayout);

        // Load initial data
        refreshEvents();
    }

    private void configureGrid() {
        eventGrid.addColumn(EventRecord::meetupId).setHeader("Meetup ID").setWidth("120px").setFlexGrow(0);
        eventGrid.addColumn(EventRecord::title).setHeader("Title").setAutoWidth(true).setFlexGrow(1);

        // Date/time column
        eventGrid.addColumn(event -> {
            if (event.eventDate() != null) {
                return event.eventDate().format(DATE_FORMATTER);
            } else {
                return "N/A";
            }
        }).setHeader("Date/Time").setWidth("150px").setFlexGrow(0);

        // No status column in the domain model, this is based on JPA entity fields

        // Action column with buttons
        eventGrid.addComponentColumn(event -> {
            HorizontalLayout buttonLayout = new HorizontalLayout();

            // Sync Members button
            SyncMembersButton syncButton = new SyncMembersButton(meetupService, event.meetupId());
            buttonLayout.add(syncButton);

            // Raffle button
            Optional<RaffleRecord> raffle = raffleService.getRaffleByMeetupEventId(event.meetupId());
            if (raffle.isPresent()) {
                Button viewButton = new Button("View Raffle", e -> {
                    getUI().ifPresent(ui -> ui.navigate("raffle-admin/" + raffle.get().id() + "/details"));
                });
                viewButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
                buttonLayout.add(viewButton);
            } else {
                Button createButton = new Button("Create Raffle", e -> createRaffle(event));
                createButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
                buttonLayout.add(createButton);
            }

            return buttonLayout;
        }).setHeader("Actions").setWidth("380px").setFlexGrow(0);

        eventGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        eventGrid.setHeightFull();

        add(eventGrid);
    }

    private void refreshEvents() {
        List<EventRecord> events = meetupService.getAllEvents();
        eventGrid.setItems(events);
    }

    private void importMeetupButtonClicked(ClickEvent<Button> event) {
        MeetupImportDialog importDialog = new MeetupImportDialog(
                meetupApiClient,
                meetupService,
                this::refreshEvents
        );
        importDialog.open();
    }

    private void createRaffle(EventRecord event) {
        try {
            // Check if raffle already exists
            if (raffleService.getRaffleByMeetupEventId(event.meetupId()).isPresent()) {
                Notification.show("Raffle already exists for this event",
                        3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_WARNING);
                return;
            }

            // Create new raffle using the service
            RaffleRecord raffle = raffleService.createRaffle(event);

            // Show success notification
            Notification.show("Raffle created for " + event.title(),
                    3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

            // Refresh grid to update status
            refreshEvents();

            // Navigate to raffle details
            getUI().ifPresent(ui -> ui.navigate("raffle-admin/" + raffle.id() + "/details"));

        } catch (Exception e) {
            Notification.show("Error creating raffle: " + e.getMessage(),
                    5000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}