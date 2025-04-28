package com.vaadin.demo.application.adapter.in.views.admin.components;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class IconButton extends Button {
    public IconButton(Icon icon, ComponentEventListener<ClickEvent<Button>> addButtonClicked) {
        super(icon, addButtonClicked);
        addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addClassNames(LumoUtility.Width.LARGE, LumoUtility.Height.LARGE, LumoUtility.BorderRadius.FULL);
    }
}
