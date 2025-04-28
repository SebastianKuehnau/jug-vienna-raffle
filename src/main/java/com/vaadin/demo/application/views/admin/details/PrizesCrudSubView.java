package com.vaadin.demo.application.views.admin.details;

import com.vaadin.demo.application.domain.model.PrizeFormRecord;
import com.vaadin.demo.application.domain.model.PrizeRecord;
import com.vaadin.demo.application.domain.model.PrizeTemplateRecord;
import com.vaadin.demo.application.domain.model.RaffleRecord;
import com.vaadin.demo.application.application.service.RaffleApplicationService;
import com.vaadin.demo.application.views.admin.PrizeTemplatesView;
import com.vaadin.demo.application.views.admin.SpinWheelView;
import com.vaadin.demo.application.views.admin.components.IconButton;
import com.vaadin.demo.application.views.admin.components.PrizeFormDialog;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.List;

@Route(value = "prizes", layout = DetailsMainLayout.class)
@com.vaadin.flow.server.auth.AnonymousAllowed
public class PrizesCrudSubView extends VerticalLayout implements BeforeEnterObserver {

    private final Grid<PrizeRecord> prizeGrid = new Grid<>(PrizeRecord.class);

    private final RaffleApplicationService raffleService;

    private RaffleRecord raffle;

    public PrizesCrudSubView(RaffleApplicationService raffleService) {
        this.raffleService = raffleService;

        prizeGrid.setColumns("name", "description", "winner", "voucherCode", "validUntil");
        prizeGrid.getColumnByKey("name").setHeader("Prize").setAutoWidth(true);
        prizeGrid.getColumnByKey("description").setHeader("Description").setAutoWidth(true);
        prizeGrid.getColumnByKey("winner").setHeader("Winner").setAutoWidth(true);
        prizeGrid.getColumnByKey("voucherCode").setHeader("Voucher Code").setAutoWidth(true);
        prizeGrid.getColumnByKey("validUntil").setHeader("Valid Until").setAutoWidth(true);
        prizeGrid.asSingleSelect().addValueChangeListener(this::selectPrize);
        prizeGrid.addComponentColumn(this::createRaffleButton).setHeader("Raffle").setSortable(false);
        prizeGrid.setWidthFull();

        Button addButton = new IconButton(VaadinIcon.PLUS.create(), this::addPrize);
        addButton.setText("Add Prize");

        Button fromTemplateButton = new Button("From Template", VaadinIcon.COPY.create());
        fromTemplateButton.addClickListener(this::addPrizeFromTemplate);

        Button manageTemplatesButton = new Button("Manage Templates", VaadinIcon.LIST.create());
        manageTemplatesButton.addClickListener(e -> {
            getUI().ifPresent(ui -> ui.navigate("prize-templates"));
        });

        HorizontalLayout buttonLayout = new HorizontalLayout(manageTemplatesButton, fromTemplateButton, addButton);
        buttonLayout.setWidthFull();
        buttonLayout.setJustifyContentMode(JustifyContentMode.END);

        add(prizeGrid, buttonLayout);
        setSizeFull();
    }

    private Component createRaffleButton(PrizeRecord prize) {
        var button = new Button("Start Raffle", VaadinIcon.SPINNER.create());
        button.addClickListener(event -> {
            if (prize != null && prize.id() != null) {
                String url = "raffle-admin/spin-wheel/" + prize.id();
                event.getSource().getUI().ifPresent(ui -> ui.navigate(url));
            } else {
                Notification.show("Prize ID is missing or invalid", 3000, Notification.Position.MIDDLE);
            }
        });
        button.setEnabled(prize.winner() == null);
        button.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        return button;
    }

    private void selectPrize(AbstractField.ComponentValueChangeEvent<Grid<PrizeRecord>, PrizeRecord> event) {
        PrizeRecord record = event.getValue();
        if (record == null) {
            return;
        }

        // Convert domain PrizeRecord to PrizeFormRecord for the UI
        PrizeFormRecord formRecord = PrizeFormRecord.fromPrizeRecord(record);

        var prizeDialog = new PrizeFormDialog(this::savePrizeForm, this::deletePrize);
        prizeDialog.setPrizeForm(formRecord);
        prizeDialog.open();
    }

    private void addPrize(ClickEvent<Button> buttonClickEvent) {
        PrizeFormRecord emptyForm = PrizeFormRecord.empty();

        var prizeDialog = new PrizeFormDialog(this::savePrizeForm, this::deletePrize);
        prizeDialog.setPrizeForm(emptyForm);
//        prizeDialog.setTemplateSupplier(() -> raffleService.getAllPrizeTemplates());
        prizeDialog.open();
    }

    private void addPrizeFromTemplate(ClickEvent<Button> event) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Create Prize from Template");

        // Try to get PrizeTemplateRecords first, if that list is empty, fall back to legacy templates
        List<PrizeTemplateRecord> prizeTemplates = raffleService.getAllPrizeTemplateRecords();

        if (prizeTemplates.isEmpty() ) {
            dialog.add(new Span("No templates available. Please create a template first."));
            dialog.add(new Button("Close", e -> dialog.close()));
            dialog.open();
            return;
        }


            // We have new templates, use them
            ComboBox<PrizeTemplateRecord> templateCombo = new ComboBox<>("Select Template");
            templateCombo.setItems(prizeTemplates);
            templateCombo.setItemLabelGenerator(PrizeTemplateRecord::name);
            templateCombo.setWidthFull();

            TextField voucherField = new TextField("Voucher Code (optional)");
            voucherField.setWidthFull();
            voucherField.setHelperText("If this prize requires a voucher code, enter it here");

            DatePicker validUntilField = new DatePicker("Valid Until (optional)");
            validUntilField.setWidthFull();
            validUntilField.setHelperText("If this prize has an expiration date, enter it here");

            Button cancelButton = new Button("Cancel", e -> dialog.close());
            Button createButton = new Button("Create Prize", e -> {
                PrizeTemplateRecord template = templateCombo.getValue();
                if (template != null) {
                    String voucherCode = voucherField.getValue();
                    // Use the new template-based method
                    PrizeRecord newPrize = raffleService.createPrizeFromTemplateRecord(
                        template.id(),
                        this.raffle,
                        voucherCode
                    );
                    refreshPrizes();
                    dialog.close();
                }
            });
            createButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            createButton.setEnabled(false);

            templateCombo.addValueChangeListener(e -> {
                createButton.setEnabled(e.getValue() != null);

                // Auto-fill voucher code if template has one
                if (e.getValue() != null && e.getValue().voucherCode() != null) {
                    voucherField.setValue(e.getValue().voucherCode());
                }

                // Auto-fill valid until date if template has one
                if (e.getValue() != null && e.getValue().validUntil() != null) {
                    validUntilField.setValue(e.getValue().validUntil());
                }
            });

            HorizontalLayout buttons = new HorizontalLayout(cancelButton, createButton);
            buttons.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
            buttons.setWidthFull();

            VerticalLayout layout = new VerticalLayout(
                new H3("Use an existing template to create a prize"),
                templateCombo,
                voucherField,
                validUntilField,
                buttons
            );
            dialog.add(layout);


        dialog.setWidth("500px");
        dialog.open();
    }

    private void deletePrize(Long prizeId) {
        raffleService.deletePrize(prizeId);
        refreshPrizes();
    }

    private void savePrizeForm(PrizeFormRecord prizeForm) {
        raffleService.savePrizeForm(prizeForm, this.raffle);
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
