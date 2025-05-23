package com.vaadin.demo.application.adapter.in.views.admin.components;

import com.vaadin.demo.application.application.port.in.MeetupApplicationService;
import com.vaadin.demo.application.application.port.in.MeetupAPIService;
import com.vaadin.demo.application.domain.model.EventRecord;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import lombok.extern.slf4j.Slf4j;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Dialog for importing Meetup events from the Meetup.com API
 * Using proper hexagonal architecture with application services
 */
@Slf4j
public class MeetupImportDialog extends Dialog {

    private final MeetupAPIService meetupAPIService;
    private final MeetupApplicationService meetupApplicationService;
    private final Runnable onImportComplete;

    private final Grid<EventRecord> meetupGrid = new Grid<>();
    private final ProgressBar progressBar = new ProgressBar();
    private final Button importButton = new Button("Import Selected");
    private final Paragraph statusText = new Paragraph("Select events to import");

    /**
     * Create a new Meetup import dialog
     *
     * @param meetupAPIService The service for fetching external meetup data
     * @param meetupApplicationService The application service for handling domain operations
     * @param onImportComplete Callback to run when import is complete
     */
    public MeetupImportDialog(MeetupAPIService meetupAPIService,
                             MeetupApplicationService meetupApplicationService,
                             Runnable onImportComplete) {
        this.meetupAPIService = meetupAPIService;
        this.meetupApplicationService = meetupApplicationService;
        this.onImportComplete = onImportComplete;

        setHeaderTitle("Import Meetup Events");
        setWidth("800px");

        configureGrid();
        loadMeetupEvents();

        VerticalLayout layout = new VerticalLayout();
        layout.add(
            new H2("Select Meetup Events to Import"),
            meetupGrid,
            progressBar,
            statusText,
            createButtonLayout()
        );
        layout.setPadding(false);
        layout.setSpacing(true);

        progressBar.setVisible(false);
        progressBar.setWidth("100%");

        add(layout);
    }

    private void configureGrid() {
        meetupGrid.addColumn(EventRecord::meetupId).setHeader("ID").setWidth("100px");
        meetupGrid.addColumn(EventRecord::title).setHeader("Title").setAutoWidth(true).setFlexGrow(1);
        meetupGrid.addColumn(e -> {
            if (e.eventDate() != null) {
                return e.eventDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            } else {
                return "N/A";
            }
        }).setHeader("Date").setWidth("150px");

        // Add a column to show if the event is already imported
        meetupGrid.addComponentColumn(event -> {
            boolean exists = meetupApplicationService.getEventByMeetupId(event.meetupId()).isPresent();
            if (exists) {
                Button updateButton = new Button("Update");
                updateButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
                updateButton.addClickListener(e -> importEvent(event.meetupId()));
                return updateButton;
            } else {
                return new Paragraph("New");
            }
        }).setHeader("Status").setWidth("80px");

        meetupGrid.setSelectionMode(Grid.SelectionMode.MULTI);
        meetupGrid.setHeight("300px");
    }

    private void loadMeetupEvents() {
        try {
            List<EventRecord> events = meetupAPIService.getExternalEvents();
            meetupGrid.setItems(events);
            statusText.setText("Found " + events.size() + " events. Select events to import.");
        } catch (Exception e) {
            log.error("Error loading Meetup events", e);
            statusText.setText("Error loading Meetup events: " + e.getMessage());
            Notification.show("Error loading Meetup events: " + e.getMessage(),
                    5000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private HorizontalLayout createButtonLayout() {
        importButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        importButton.addClickListener(e -> importSelectedEvents());

        Button cancelButton = new Button("Cancel");
        cancelButton.addClickListener(e -> close());

        Button refreshButton = new Button("Refresh");
        refreshButton.addClickListener(e -> loadMeetupEvents());

        HorizontalLayout layout = new HorizontalLayout(importButton, refreshButton, cancelButton);
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        layout.setPadding(true);
        layout.setWidthFull();

        return layout;
    }

    private void importSelectedEvents() {
        var selectedEvents = meetupGrid.getSelectedItems();
        if (selectedEvents.isEmpty()) {
            Notification.show("No events selected", 3000, Notification.Position.MIDDLE);
            return;
        }

        progressBar.setVisible(true);
        importButton.setEnabled(false);
        statusText.setText("Importing " + selectedEvents.size() + " events...");

        // Execute import in a Vaadin background job instead of a raw thread
        // This preserves the security context
        getUI().ifPresent(ui -> ui.access(() -> {
            int total = selectedEvents.size();
            final int[] counter = new int[1];
            counter[0] = 0;

            // Process each event one by one
            for (EventRecord event : selectedEvents) {
                try {
                    counter[0]++;
                    final int currentCount = counter[0];

                    // Update progress UI
                    progressBar.setValue((float) currentCount / total);
                    statusText.setText("Importing " + currentCount + " of " + total + ": " + event.title());

                    // Import the event - this preserves the security context
                    importEvent(event.meetupId());
                } catch (Exception ex) {
                    log.error("Error importing event: " + event.meetupId(), ex);
                    Notification.show("Error importing event " + event.meetupId() + ": " + ex.getMessage(),
                            5000, Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                }

                // Push updates to client after each event
                ui.push();
            }

            // Update UI when complete
            progressBar.setVisible(false);
            importButton.setEnabled(true);
            statusText.setText("Import complete. Imported " + counter[0] + " events.");

            // Show success notification
            Notification.show("Import complete. Imported " + counter[0] + " events.",
                    3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

            // Run callback
            onImportComplete.run();

            // Close dialog
            close();

            // Push final updates to client
            ui.push();
        }));
    }

    private EventRecord importEvent(String meetupEventId) {
        return meetupApplicationService.importEvent(meetupEventId);
    }
}