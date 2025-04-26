package com.vaadin.demo.application.views.admin.details;

import com.vaadin.demo.application.domain.port.MeetupPort;
import com.vaadin.demo.application.domain.port.RafflePort;
import com.vaadin.demo.application.services.meetup.MeetupService;
import com.vaadin.demo.application.domain.model.EventRecord;
import com.vaadin.demo.application.views.admin.components.SyncMembersButton;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.vaadin.firitin.components.RichText;

import java.time.format.DateTimeFormatter;

@Route(value = "details", layout = DetailsMainLayout.class)
@com.vaadin.flow.server.auth.AnonymousAllowed
public class DetailsSubView extends VerticalLayout implements BeforeEnterObserver {

    private final RichText descriptionTextArea = new RichText();
    private final TextField titleField = new TextField("Title");
    private final TextField dateTimeField = new TextField("Datetime");
    private final TextField tokenField = new TextField("Token");
    private final RafflePort raffleService;
    private final MeetupService meetupService;
    private final MeetupPort meetupPort;
    private String currentMeetupEventId;

    public DetailsSubView(RafflePort raffleService, MeetupService meetupService, MeetupPort meetupPort) {
        this.raffleService = raffleService;
        this.meetupService = meetupService;
        this.meetupPort = meetupPort;

        var descriptionTitle = new Span("Description");
        descriptionTitle.addClassNames(LumoUtility.FontWeight.NORMAL, LumoUtility.FontSize.SMALL,
                LumoUtility.TextColor.BODY);
        var scroller = new Scroller(descriptionTextArea);
        scroller.addClassNames(LumoUtility.Border.ALL,
                LumoUtility.BorderColor.CONTRAST_30,
                LumoUtility.BorderRadius.MEDIUM,
                LumoUtility.Padding.MEDIUM);

        titleField.setReadOnly(true);
        titleField.setWidth(100, Unit.PERCENTAGE);
        add(titleField);

        dateTimeField.setReadOnly(true);
        tokenField.setReadOnly(true);

        var componentLayout = new HorizontalLayout();
        componentLayout.add(dateTimeField, tokenField);
        componentLayout.setPadding(false);
        
        // Add Sync Members button
        SyncMembersButton syncButton = new SyncMembersButton(meetupPort, "");
        syncButton.setVisible(false); // Hide until we have a meetup ID
        
        add(componentLayout, syncButton, descriptionTitle);
        addAndExpand(scroller);
        setJustifyContentMode(JustifyContentMode.START);

        setPadding(false);
    }

    private void updateContent(EventRecord event) {
        titleField.setValue(event.title());
        dateTimeField.setValue(event.eventDate().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        descriptionTextArea.withMarkDown(event.description());
        tokenField.setValue(""); // No token field in the domain model
        
        // Update meetup ID and sync button
        this.currentMeetupEventId = event.meetupId();
        
        // Find the sync button and update its meetup ID
        getChildren().forEach(component -> {
            if (component instanceof SyncMembersButton) {
                SyncMembersButton syncButton = (SyncMembersButton) component;
                // We need to remove the old button and add a new one
                remove(syncButton);
                SyncMembersButton newSyncButton = new SyncMembersButton(meetupPort, currentMeetupEventId);
                addComponentAtIndex(2, newSyncButton); // Same position as before
            }
        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Optional: Zugriff auf Route-Parameter, wenn benötigt
        RouteParameters parameters = event.getRouteParameters();
        parameters.get(DetailsMainLayout.RAFFLE_ID_PARAMETER)
                .flatMap(raffleId -> raffleService.getRaffleById(Long.parseLong(raffleId)))
                .flatMap(raffle -> meetupPort.getEventByMeetupId(raffle.meetupId()))
                .ifPresent(this::updateContent);
    }
}
