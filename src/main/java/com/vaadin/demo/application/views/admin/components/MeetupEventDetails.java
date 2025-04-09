package com.vaadin.demo.application.views.admin.components;

import com.vaadin.demo.application.data.Prize;
import com.vaadin.demo.application.services.PrizeService;
import com.vaadin.demo.application.services.meetup.MeetupService;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.vaadin.firitin.components.RichText;

import java.time.format.DateTimeFormatter;

public class MeetupEventDetails extends VerticalLayout {
    private final RichText descriptionTextArea = new RichText();
    private final TextField titleField = new TextField("Title");
    private final TextField dateTimeField = new TextField("Datetime");
    private final TextField tokenField = new TextField("Token");
    private final Grid<Prize> prizeGrid = new Grid<>(Prize.class);

    private final PrizeService prizeService;

    public MeetupEventDetails(PrizeService prizeService) {
        this.prizeService = prizeService;
        descriptionTextArea.setSizeFull();

        var descriptionTitle = new Span("Description");
        descriptionTitle.addClassNames(LumoUtility.FontWeight.NORMAL, LumoUtility.FontSize.SMALL,
                LumoUtility.TextColor.BODY);
        Scroller scroller = new Scroller(descriptionTextArea);
        scroller.setHeight(200, Unit.PIXELS);
        scroller.setWidthFull();

        scroller.addClassNames(LumoUtility.Border.ALL,
                LumoUtility.BorderColor.CONTRAST_30,
                LumoUtility.BorderRadius.MEDIUM);

        titleField.setReadOnly(true);
        titleField.setWidth(400, Unit.PIXELS);
        dateTimeField.setReadOnly(true);
        tokenField.setReadOnly(true);

        var componentLayout = new HorizontalLayout();
        componentLayout.add(titleField, dateTimeField, tokenField);
        componentLayout.setPadding(false);

        prizeGrid.setItemsPageable(prizeService::findAll);
        prizeGrid.asSingleSelect().addValueChangeListener(this::selectPrize);
        prizeGrid.setWidthFull();
        var addButton = new IconButton(VaadinIcon.PLUS.create(), this::addPrize);

        add(componentLayout, descriptionTitle, scroller, prizeGrid, addButton);
        setJustifyContentMode(JustifyContentMode.START);
        setAlignSelf(Alignment.END, addButton);
        setPadding(false);
        setSizeFull();
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
        prizeService.save(prize);
        prizeGrid.getDataProvider().refreshAll();
    }


    public void update(MeetupService.MeetupEvent event) {
        titleField.setValue(event.title());
        dateTimeField.setValue(event.dateTime().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        descriptionTextArea.withMarkDown(event.description());
        tokenField.setValue(event.token());
    }
}
