package com.vaadin.demo.application.views.admin.components;

import com.vaadin.demo.application.data.Prize;
import com.vaadin.demo.application.services.PrizeService;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.function.SerializableConsumer;

public class PrizeDialog extends Dialog {

    private final Binder<Prize> binder;
    private final SerializableConsumer<Prize> saveConsumer;
    private final SerializableConsumer<Prize> deleteConsumer;

    public PrizeDialog(SerializableConsumer<Prize> saveConsumer, SerializableConsumer<Prize> deleteConsumer) {
        super("Prize Dialog");
        this.saveConsumer = saveConsumer;
        this.deleteConsumer = deleteConsumer;

        var nameField = new TextField("Name");
        var winnerField = new TextField("Winner");
        winnerField.setReadOnly(true);

        binder = new Binder<>(Prize.class);

        binder.forField(nameField).bind(Prize::getName, Prize::setName);

        var cancelButton = new Button("Cancel", this::cancel);
        var saveButton = new Button("Save", this::save);
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        var deleteButton = new Button("Delete", this::delete);
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        var buttonLayout = new HorizontalLayout(deleteButton, cancelButton, saveButton);

        var rootLayout = new VerticalLayout(nameField, winnerField, buttonLayout);
        add(rootLayout);

        setModal(true);
    }

    private void delete(ClickEvent<Button> buttonClickEvent) {
        this.deleteConsumer.accept(binder.getBean());
        close();
    }

    private void cancel(ClickEvent<Button> buttonClickEvent) {
        close();
    }

    private void save(ClickEvent<Button> buttonClickEvent) {
        this.saveConsumer.accept(binder.getBean());
        close();
    }

    public void setPrize(Prize prize) {
        binder.setBean(prize);
    }
}
