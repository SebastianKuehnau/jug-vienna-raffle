package com.vaadin.demo.application.views.admin;

import com.vaadin.demo.application.data.Raffle;
import com.vaadin.demo.application.services.PrizeService;
import com.vaadin.demo.application.services.RaffleService;
import com.vaadin.demo.application.services.meetup.MeetupService;
import com.vaadin.demo.application.views.admin.components.IconButton;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.vaadin.lineawesome.LineAwesomeIconUrl;


@Menu(order = 5, icon = LineAwesomeIconUrl.TOOLS_SOLID, title = "Raffle Admin View")
@AnonymousAllowed
@Route("raffle-admin")
public class RaffleAdminView extends VerticalLayout {

    public RaffleAdminView(RaffleService raffleService) {
        add(new H1("Admin View"));

        var raffleGrid = new Grid<>(Raffle.class);
        raffleGrid.setItemsPageable(raffleService::list);
        raffleGrid.asSingleSelect().addValueChangeListener(this::raffleItemSelected);
        add(raffleGrid);

        IconButton addButton = new IconButton(VaadinIcon.PLUS_CIRCLE.create(), this::addButtonClicked);
        add(addButton);
        setAlignSelf(Alignment.END, addButton);
    }

    private void raffleItemSelected(AbstractField.ComponentValueChangeEvent<Grid<Raffle>, Raffle> gridRaffleComponentValueChangeEvent) {
        UI.getCurrent().navigate(RaffleDetailsView.class, gridRaffleComponentValueChangeEvent.getValue().getId());

    }

    private void addButtonClicked(ClickEvent<Button> buttonClickEvent) {
        UI.getCurrent().navigate(RaffleDetailsView.class);
    }
}
