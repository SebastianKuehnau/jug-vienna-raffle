package com.vaadin.demo.application.views.admin.details;

import com.vaadin.demo.application.data.Prize;
import com.vaadin.demo.application.data.Raffle;
import com.vaadin.demo.application.domain.port.RafflePort;
import com.vaadin.demo.application.services.PrizeService;
import com.vaadin.demo.application.views.admin.SpinWheelView;
import com.vaadin.demo.application.views.admin.components.IconButton;
import com.vaadin.demo.application.views.admin.components.PrizeDialog;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;

@Route(value = "prizes", layout = DetailsMainLayout.class)
@com.vaadin.flow.server.auth.AnonymousAllowed
public class PrizesCrudSubView extends VerticalLayout implements BeforeEnterObserver {

    private final Grid<Prize> prizeGrid = new Grid<>(Prize.class);

    private final PrizeService prizeService;

    private Raffle raffle;
    private final RafflePort raffleService;

    public PrizesCrudSubView(PrizeService prizeService, RafflePort raffleService) {
        this.prizeService = prizeService;
        this.raffleService = raffleService;

        prizeGrid.setColumns("name", "winner");
        prizeGrid.asSingleSelect().addValueChangeListener(this::selectPrize);
        prizeGrid.addComponentColumn(this::createRaffleButton).setHeader("Raffle").setSortable(false);
        prizeGrid.setWidthFull();

        var addButton = new IconButton(VaadinIcon.PLUS.create(), this::addPrize);
        setAlignSelf(Alignment.END, addButton);

        add(prizeGrid, addButton);
    }

    private Component createRaffleButton(Prize prize) {
        var button = new Button("Start Raffle", VaadinIcon.SPINNER.create());
        button.addClickListener(event -> event.getSource().getUI()
                .ifPresent(ui -> ui.navigate(SpinWheelView.class, prize.getId())));
        button.setEnabled(prize.getWinnerName() == null || prize.getWinnerName().isBlank());
        button.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        return button;
    }

    private void selectPrize(AbstractField.ComponentValueChangeEvent<Grid<Prize>, Prize> event) {
        var prizeDialog = new PrizeDialog(this::savePrize, this::deletePrize);
        prizeDialog.setPrize(event.getValue());
        prizeDialog.open();
    }

    private void addPrize(ClickEvent<Button> buttonClickEvent) {
        var prizeDialog = new PrizeDialog(this::savePrize, this::deletePrize);
        prizeDialog.setPrize(new Prize());
        prizeDialog.open();
    }

    private void deletePrize(Prize prize) {
        prizeService.delete(prize);
        prizeGrid.getDataProvider().refreshAll();
    }

    private void savePrize(Prize prize) {
        prize.setRaffle(this.raffle);
        prizeService.save(prize);
        prizeGrid.setItems(prizeService.findByRaffle(raffle));
    }

    public void setRaffle(Raffle raffle) {
        this.raffle = raffle;
        prizeGrid.setItems(prizeService.findByRaffle(raffle));
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        RouteParameters parameters = event.getRouteParameters();
        parameters.get(DetailsMainLayout.RAFFLE_ID_PARAMETER)
                .flatMap(raffleId -> raffleService.getRaffleById(Long.parseLong(raffleId)))
                .ifPresent(this::setRaffle);
    }
}
