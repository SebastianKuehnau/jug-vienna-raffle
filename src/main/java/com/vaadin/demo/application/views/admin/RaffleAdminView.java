package com.vaadin.demo.application.views.admin;

import com.vaadin.demo.application.data.Prize;
import com.vaadin.demo.application.data.Raffle;
import com.vaadin.demo.application.services.MeetupDataService;
import com.vaadin.demo.application.services.RaffleService;
import com.vaadin.demo.application.services.meetup.MeetupService;
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
import org.springframework.data.domain.Pageable;
import org.vaadin.lineawesome.LineAwesomeIconUrl;


@Menu(order = 5, icon = LineAwesomeIconUrl.TOOLS_SOLID, title = "Raffle Admin View")
@AnonymousAllowed
@PageTitle( "Raffle Admin View")
@Route("raffle-admin")
@SuppressWarnings("serial")
public class RaffleAdminView extends VerticalLayout {

    private final RaffleService raffleService;
    private final MeetupService meetupService;
    private final Grid<Raffle> raffleGrid;

    private final MeetupDataService meetupDataService;

    public RaffleAdminView(RaffleService raffleService, MeetupService meetupService, MeetupDataService meetupDataService) {
        this.raffleService = raffleService;
        this.meetupService = meetupService;
        this.meetupDataService = meetupDataService;
        add(new H1("Admin View"));

        // Add "All Events" link
        Button viewAllEventsButton = new Button("View All Events", VaadinIcon.LIST.create());
        viewAllEventsButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("events")));
        add(viewAllEventsButton);
        
        raffleGrid = new Grid<>();
        raffleGrid.addColumn(Raffle::getMeetup_event_id).setHeader("Meetup Event ID");
        raffleGrid.addColumn(raffle -> {
            try {
                return meetupService.getEvent(raffle.getMeetup_event_id()).get().title();
            } catch (Exception e) {
                return "Unknown Event";
            }
        }).setHeader("Meetup Event Name");
        raffleGrid.addColumn(createPrizeList()).setHeader("Prizes");
        raffleGrid.setItemsPageable(raffleService::list);
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

    private LitRenderer<Raffle> createPrizeList() {
        ValueProvider<Raffle, Object> raffleObjectValueProvider = raffle ->
                raffle.getPrizes().stream()
                        .map(Prize::getName)
                        .toList();

        return LitRenderer.<Raffle>of("""
                        <ul style="padding: 0; margin: 0">
                            ${item.prizeNames.map((name) => html`<li>${name}</li>`)}
                        </ul>
                    """)
                .withProperty("prizeNames", raffleObjectValueProvider);
    }

    private void raffleItemSelected(AbstractField.ComponentValueChangeEvent<Grid<Raffle>, Raffle> gridRaffleComponentValueChangeEvent) {
        UI.getCurrent().navigate(String.format("raffle-admin/%s/details", gridRaffleComponentValueChangeEvent.getValue().getId()));
    }

    private void addButtonClicked(ClickEvent<Button> buttonClickEvent) {
        var dialog = new Dialog();
        dialog.setHeaderTitle("Add Raffle Event");

        var raffleMeetupIdList = raffleService.list(Pageable.unpaged()).stream().map(Raffle::getMeetup_event_id).toList();
        var meetupEventIdSelect = new Select<MeetupService.MeetupEvent>();
        meetupEventIdSelect.setLabel("Meetup Event ID");
        meetupEventIdSelect.setItems(meetupService.getEvents());
        meetupEventIdSelect.setItemLabelGenerator(MeetupService.MeetupEvent::id);
        meetupEventIdSelect.setTextRenderer(this::createTextRenderer);
        meetupEventIdSelect.setItemEnabledProvider(meetupEvent -> !raffleMeetupIdList.contains(meetupEvent.id()));
        meetupEventIdSelect.setOverlayWidth(300, Unit.PIXELS);

        var addButton = new Button("Add Raffle", VaadinIcon.PLUS.create());
        addButton.setEnabled(false);
        meetupEventIdSelect.addValueChangeListener(event -> {
            event.getSource().getUI().ifPresent(
                    ui -> ui.access(() -> addButton.setEnabled(event.getValue() != null))
            );
        });

        addButton.addClickListener(event -> {
            var newRaffle = new Raffle();
            newRaffle.setMeetup_event_id(meetupEventIdSelect.getValue().id());
            raffleService.save(newRaffle);
            raffleGrid.getDataProvider().refreshAll();
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
                meetupService, 
                meetupDataService, 
                this::refreshGrid
        );
        importDialog.open();
    }
    
    private void refreshGrid() {
        raffleGrid.getDataProvider().refreshAll();
    }

    private String createTextRenderer(MeetupService.MeetupEvent meetupEvent) {
        return String.format("%s (%s)", meetupEvent.id(), meetupEvent.title());
    }
}
