package com.vaadin.demo.application.views.admin.components;

import com.vaadin.demo.application.data.Prize;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class PrizeDialog extends Dialog {

    private final Binder<Prize> binder;
    private final SerializableConsumer<Prize> saveConsumer;
    private final SerializableConsumer<Prize> deleteConsumer;
    private final TextField nameField;

    public PrizeDialog(SerializableConsumer<Prize> saveConsumer, SerializableConsumer<Prize> deleteConsumer) {
        super("Prize Dialog");
        this.saveConsumer = saveConsumer;
        this.deleteConsumer = deleteConsumer;

        nameField = new TextField("Name");
        nameField.setWidthFull();
        var winnerField = new TextField("Winner");
        winnerField.setWidthFull();
        winnerField.setReadOnly(true);

        binder = new Binder<>(Prize.class);

        binder.forField(nameField).bind(Prize::getName, Prize::setName);
        binder.forField(winnerField).bind(Prize::getWinnerName, Prize::setWinnerName);

        var cancelButton = new Button("Cancel", this::cancel);
        var saveButton = new Button("Save", this::save);
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        var deleteButton = new Button("Delete", this::delete);
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        var buttonLayout = new HorizontalLayout(deleteButton, cancelButton, saveButton);
        buttonLayout.addClassName(LumoUtility.Padding.Top.LARGE);

        var rootLayout = new VerticalLayout();
        rootLayout.addAndExpand(nameField, winnerField, buttonLayout);
        rootLayout.setPadding(false);
        rootLayout.setSizeFull();
        add(rootLayout);

        setModal(true);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        nameField.focus();
    }

    private void delete(ClickEvent<Button> buttonClickEvent) {
        this.deleteConsumer.accept(binder.getBean());
        close();
    }

    private void cancel(ClickEvent<Button> buttonClickEvent) {
        close();
    }

    private void save(ClickEvent<Button> buttonClickEvent) {
        var prize = binder.getBean();
        this.saveConsumer.accept(prize);
        close();
    }

    public void setPrize(Prize prize) {
        binder.setBean(prize);
    }
}
