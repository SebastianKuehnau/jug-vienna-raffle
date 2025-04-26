package com.vaadin.demo.application.views.admin.details;

import com.vaadin.demo.application.domain.model.PrizeRecord;
import com.vaadin.demo.application.domain.model.RaffleRecord;
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

    private final Grid<PrizeRecord> prizeGrid = new Grid<>(PrizeRecord.class);

    private final RafflePort raffleService;

    private RaffleRecord raffle;

    public PrizesCrudSubView(RafflePort raffleService) {
        this.raffleService = raffleService;

        prizeGrid.setColumns("name", "winner");
        prizeGrid.asSingleSelect().addValueChangeListener(this::selectPrize);
        prizeGrid.addComponentColumn(this::createRaffleButton).setHeader("Raffle").setSortable(false);
        prizeGrid.setWidthFull();

        var addButton = new IconButton(VaadinIcon.PLUS.create(), this::addPrize);
        setAlignSelf(Alignment.END, addButton);

        add(prizeGrid, addButton);
    }

    private Component createRaffleButton(PrizeRecord prize) {
        var button = new Button("Start Raffle", VaadinIcon.SPINNER.create());
        button.addClickListener(event -> event.getSource().getUI()
                .ifPresent(ui -> ui.navigate(SpinWheelView.class, prize.id())));
        button.setEnabled(prize.winner() == null);
        button.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        return button;
    }

    private void selectPrize(AbstractField.ComponentValueChangeEvent<Grid<PrizeRecord>, PrizeRecord> event) {
        // We need to adapt domain records to JPA entities for the PrizeDialog
        // This should be refactored in future to use domain records directly
        var prizeDialog = new PrizeDialog(this::savePrize, this::deletePrize);
        prizeDialog.setPrize(event.getValue());
        prizeDialog.open();
    }

    private void addPrize(ClickEvent<Button> buttonClickEvent) {
        // For future: enhance PrizeDialog to work with domain records
        var prizeDialog = new PrizeDialog(this::savePrize, this::deletePrize);
        // Create an empty PrizeRecord
        prizeDialog.setPrize(null); // This needs further adaptation
        prizeDialog.open();
    }

    private void deletePrize(Prize prizeEntity) {
        // Convert entity-based call to domain-based call
        raffleService.deletePrize(prizeEntity.getId());
        refreshPrizes();
    }

    private void savePrize(Prize prizeEntity) {
        // Create a PrizeRecord from the entity (simplified)
        // In a full implementation, we would need a proper mapper
        PrizeRecord prizeRecord = new PrizeRecord(
            prizeEntity.getId(),
            prizeEntity.getName(),
            null, // No winner mapping yet
            this.raffle
        );
        
        raffleService.savePrize(prizeRecord);
        refreshPrizes();
    }
    
    private void refreshPrizes() {
        if (raffle != null) {
            prizeGrid.setItems(raffleService.getPrizesForRaffle(raffle));
        }
    }

    public void setRaffle(RaffleRecord raffle) {
        this.raffle = raffle;
        refreshPrizes();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        RouteParameters parameters = event.getRouteParameters();
        parameters.get(DetailsMainLayout.RAFFLE_ID_PARAMETER)
                .flatMap(raffleId -> raffleService.getRaffleById(Long.parseLong(raffleId)))
                .ifPresent(this::setRaffle);
    }
}
