package com.vaadin.demo.application.views.admin;

import com.vaadin.demo.application.services.PrizeService;
import com.vaadin.demo.application.services.RaffleService;
import com.vaadin.demo.application.services.meetup.MeetupService;
import com.vaadin.demo.application.views.admin.components.MeetupEventDetails;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.Route;

import java.util.Optional;

@Route("raffle-details")
public class RaffleDetailsView extends VerticalLayout implements HasUrlParameter<Long> {

    private final MeetupService meetupService;
    private final RaffleService raffleService;

    private final ComboBox<MeetupService.MeetupEvent> meetupEventIdComboBox;
    private final MeetupEventDetails meetupEventDetails;

    public RaffleDetailsView(MeetupService meetupService, PrizeService prizeService, RaffleService raffleService) {
        this.meetupService = meetupService;
        this.raffleService = raffleService;

        this.meetupEventDetails = new MeetupEventDetails(prizeService);

        meetupEventIdComboBox = new ComboBox<>("Meetup Event ID");
        meetupEventIdComboBox.setItems(meetupService.getEvents());
        meetupEventIdComboBox.setItemLabelGenerator(MeetupService.MeetupEvent::id);
        meetupEventIdComboBox.setRenderer(createMeetupEventIdRenderer());
        meetupEventIdComboBox.setOverlayWidth(300, Unit.PIXELS);
        meetupEventIdComboBox.addValueChangeListener(event -> meetupEventDetails.update(event.getValue()));

        add(meetupEventIdComboBox, meetupEventDetails);
    }

    private Renderer<MeetupService.MeetupEvent> createMeetupEventIdRenderer() {
        var itemString = """
                <div>
                    <span>${item.title}</span>
                </div>    
            """;

        return LitRenderer.<MeetupService.MeetupEvent> of(itemString)
                .withProperty("title", MeetupService.MeetupEvent::title)
                .withProperty("description", MeetupService.MeetupEvent::description);
    }


    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter Long parameter) {
        Optional<Long> raffleIdOptional = Optional.ofNullable(parameter);

        if (raffleIdOptional.isPresent()) {
            raffleService.get(raffleIdOptional.get()).ifPresent(raffle -> {
                Optional<MeetupService.MeetupEvent> meetupEventOptional = meetupService.getEvent(raffle.getMeetup_event_id());

                meetupEventOptional.ifPresent(meetupEvent -> {
                    meetupEventIdComboBox.setValue(meetupEvent);
                    meetupEventIdComboBox.setReadOnly(true);
                    meetupEventDetails.update(meetupEvent);
                });
            });
        } else {

        }
    }
}
