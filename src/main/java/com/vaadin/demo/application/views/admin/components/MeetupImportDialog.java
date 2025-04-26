package com.vaadin.demo.application.views.admin.components;

import com.vaadin.demo.application.domain.port.MeetupPort;
import com.vaadin.demo.application.domain.model.EventRecord;
import com.vaadin.demo.application.services.meetup.MeetupService;
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
import java.util.Set;

/**
 * Dialog for importing Meetup events from the Meetup.com API
 */
@Slf4j
public class MeetupImportDialog extends Dialog {

    private final MeetupService meetupApiClient;
    private final MeetupPort meetupService;
    private final Runnable onImportComplete;

    private final Grid<MeetupService.MeetupEvent> meetupGrid = new Grid<>();
    private final ProgressBar progressBar = new ProgressBar();
    private final Button importButton = new Button("Import Selected");
    private final Paragraph statusText = new Paragraph("Select events to import");

    /**
     * Create a new Meetup import dialog
     * 
     * @param meetupApiClient External API client to fetch Meetup data
     * @param meetupService Domain service to store Meetup data locally
     * @param onImportComplete Callback to run when import is complete
     */
    public MeetupImportDialog(MeetupService meetupApiClient, 
                             MeetupPort meetupService,
                             Runnable onImportComplete) {
        this.meetupApiClient = meetupApiClient;
        this.meetupService = meetupService;
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
        meetupGrid.addColumn(MeetupService.MeetupEvent::id).setHeader("ID").setWidth("100px");
        meetupGrid.addColumn(MeetupService.MeetupEvent::title).setHeader("Title").setAutoWidth(true).setFlexGrow(1);
        meetupGrid.addColumn(e -> {
            if (e.dateTime() != null) {
                return e.dateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            } else {
                return "N/A";
            }
        }).setHeader("Date").setWidth("150px");
        meetupGrid.addColumn(MeetupService.MeetupEvent::status).setHeader("Status").setWidth("100px");
        
        // Add a column to show if the event is already imported
        meetupGrid.addComponentColumn(event -> {
            boolean exists = meetupService.getEventByMeetupId(event.id()).isPresent();
            if (exists) {
                Button updateButton = new Button("Update");
                updateButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
                updateButton.addClickListener(e -> importEvent(event));
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
            Set<MeetupService.MeetupEvent> events = meetupApiClient.getEvents();
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
        Set<MeetupService.MeetupEvent> selectedEvents = meetupGrid.getSelectedItems();
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
            for (MeetupService.MeetupEvent event : selectedEvents) {
                try {
                    counter[0]++;
                    final int currentCount = counter[0];
                    
                    // Update progress UI
                    progressBar.setValue((float) currentCount / total);
                    statusText.setText("Importing " + currentCount + " of " + total + ": " + event.title());
                    
                    // Import the event - this preserves the security context
                    importEvent(event);
                } catch (Exception ex) {
                    log.error("Error importing event: " + event.id(), ex);
                    Notification.show("Error importing event " + event.id() + ": " + ex.getMessage(),
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
    
    private EventRecord importEvent(MeetupService.MeetupEvent event) {
        return meetupService.importEvent(event.id());
    }
}