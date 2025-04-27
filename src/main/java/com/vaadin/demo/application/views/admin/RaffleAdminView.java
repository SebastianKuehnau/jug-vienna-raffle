package com.vaadin.demo.application.views.admin;

import com.vaadin.demo.application.application.service.MeetupApplicationService;
import com.vaadin.demo.application.application.service.RaffleApplicationService;
import com.vaadin.demo.application.domain.model.RaffleRecord;
import com.vaadin.demo.application.services.meetup.MeetupClient;
import com.vaadin.demo.application.views.admin.components.IconButton;
import com.vaadin.demo.application.views.admin.components.MeetupImportDialog;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.util.List;

/**
 * Raffle Admin View using domain records instead of JPA entities
 */
@Menu(order = 5, icon = LineAwesomeIconUrl.TOOLS_SOLID, title = "Raffle Admin")
@AnonymousAllowed
@PageTitle("Raffle Admin")
@Route("raffle-admin")
@SuppressWarnings("serial")
public class RaffleAdminView extends VerticalLayout {

    private final RaffleApplicationService raffleService;
    private final MeetupClient meetupClient;
    private final Grid<RaffleRecord> raffleGrid;
    private final MeetupApplicationService meetupApplicationService;

    public RaffleAdminView(
            RaffleApplicationService raffleService,
            MeetupClient meetupClient,
            MeetupApplicationService meetupApplicationService) {
        this.raffleService = raffleService;
        this.meetupClient = meetupClient;
        this.meetupApplicationService = meetupApplicationService;

        add(new H1("Raffle Administration"));

        // Add "All Events" link
        Button viewAllEventsButton = new Button("View All Events", VaadinIcon.LIST.create());
        viewAllEventsButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("events")));
        add(viewAllEventsButton);

        // Create grid showing raffle domain records
        raffleGrid = new Grid<>(RaffleRecord.class, false);
        raffleGrid.addColumn(RaffleRecord::meetupId).setHeader("Meetup Event ID");
        raffleGrid.addColumn(raffle -> {
            if (raffle.event() != null) {
                return raffle.event().title();
            } else {
                try {
                    return meetupClient.getEvent(raffle.meetupId()).get().title();
                } catch (Exception e) {
                    return "Unknown Event";
                }
            }
        }).setHeader("Meetup Event Name");
        raffleGrid.addColumn(createPrizeList()).setHeader("Prizes");

        // Get all raffles from the RaffleApplicationService
        refreshGrid();

        raffleGrid.asSingleSelect().addValueChangeListener(this::raffleItemSelected);
        add(raffleGrid);

        // Create button layout
        var buttonLayout = new HorizontalLayout();
        buttonLayout.setWidthFull();
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        // Add raffle button
        IconButton addButton = new IconButton(VaadinIcon.PLUS_CIRCLE.create(), this::addButtonClicked);

        // Import Meetup events button
        Button importMeetupButton = new Button("Import Meetup Events", VaadinIcon.DOWNLOAD.create());
        importMeetupButton.addClickListener(this::importMeetupButtonClicked);

        buttonLayout.add(importMeetupButton, addButton);
        add(buttonLayout);
    }

    private LitRenderer<RaffleRecord> createPrizeList() {
        ValueProvider<RaffleRecord, Object> prizeNamesProvider = raffle ->
                raffle.prizes() != null ?
                    raffle.prizes().stream()
                        .map(prize -> prize.name())
                        .toList() :
                    List.of();

        return LitRenderer.<RaffleRecord>of("""
                        <ul style="padding: 0; margin: 0">
                            ${item.prizeNames.map((name) => html`<li>${name}</li>`)}
                        </ul>
                    """)
                .withProperty("prizeNames", prizeNamesProvider);
    }

    private void raffleItemSelected(AbstractField.ComponentValueChangeEvent<Grid<RaffleRecord>, RaffleRecord> event) {
        RaffleRecord raffle = event.getValue();
        if (raffle != null && raffle.id() != null) {
            UI.getCurrent().navigate(String.format("raffle-admin/%s/details", raffle.id()));
        }
    }

    private void addButtonClicked(ClickEvent<Button> buttonClickEvent) {
        var dialog = new Dialog();
        dialog.setHeaderTitle("Add Raffle Event");

        // Get existing raffle meetup IDs
        List<String> existingRaffleMeetupIds = raffleService.getAllRaffles().stream()
                .map(RaffleRecord::meetupId)
                .toList();

        // Create select for meetup event
        var meetupEventIdSelect = new Select<MeetupClient.MeetupEvent>();
        meetupEventIdSelect.setLabel("Meetup Event ID");
        meetupEventIdSelect.setItems(meetupClient.getEvents());
        meetupEventIdSelect.setItemLabelGenerator(MeetupClient.MeetupEvent::id);
        meetupEventIdSelect.setTextRenderer(this::createTextRenderer);
        meetupEventIdSelect.setItemEnabledProvider(meetupEvent -> !existingRaffleMeetupIds.contains(meetupEvent.id()));
        meetupEventIdSelect.setWidth(300, Unit.PIXELS);

        var addButton = new Button("Add Raffle", VaadinIcon.PLUS.create());
        addButton.setEnabled(false);
        meetupEventIdSelect.addValueChangeListener(event -> {
            addButton.setEnabled(event.getValue() != null);
        });

        addButton.addClickListener(event -> {
            String meetupEventId = meetupEventIdSelect.getValue().id();

            // Create new raffle using RaffleApplicationService
            raffleService.createRaffleFromForm(meetupEventId);

            refreshGrid();
            dialog.close();
        });

        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        VerticalLayout verticalLayout = new VerticalLayout(meetupEventIdSelect, addButton);
        verticalLayout.setPadding(false);
        dialog.add(verticalLayout);
        dialog.open();
    }

    private void importMeetupButtonClicked(ClickEvent<Button> event) {
        MeetupImportDialog importDialog = new MeetupImportDialog(
            meetupClient,
                meetupApplicationService,
                this::refreshGrid
        );
        importDialog.open();
    }

    private void refreshGrid() {
        List<RaffleRecord> raffles = raffleService.getAllRaffles();
        raffleGrid.setItems(raffles);
    }

    private String createTextRenderer(MeetupClient.MeetupEvent meetupEvent) {
        return String.format("%s (%s)", meetupEvent.id(), meetupEvent.title());
    }
}
