package com.vaadin.demo.application.views.admin.details;

import com.vaadin.demo.application.services.RaffleService;
import com.vaadin.demo.application.services.meetup.MeetupService;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;

import java.util.Set;

@Route(value = "participants", layout = DetailsMainLayout.class)
public class ParticipantsSubView extends VerticalLayout implements BeforeEnterObserver {

    private final Grid<MeetupService.Member> grid;
    private final RaffleService raffleService;
    private MeetupService meetupService;

    public ParticipantsSubView(RaffleService raffleService, MeetupService meetupService) {
        this.raffleService = raffleService;
        this.meetupService = meetupService;
        grid = new Grid<>(MeetupService.Member.class);
        grid.setColumns("name", "email", "isOrganizer", "hasEnteredRaffle");
        add(grid);
    }

    public void updateContent(Set<MeetupService.Member> members) {
        grid.setItems(members);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        RouteParameters parameters = event.getRouteParameters();
        parameters.get(DetailsMainLayout.RAFFLE_ID_PARAMETER)
                .flatMap(raffleId -> raffleService.get(Long.parseLong(raffleId)))
                .flatMap(raffle -> meetupService.getEvent(raffle.getMeetup_event_id()))
                .map(meetupEvent -> meetupEvent.members())
                .ifPresent(this::updateContent);
    }
}
