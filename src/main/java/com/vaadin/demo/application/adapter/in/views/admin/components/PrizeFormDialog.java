package com.vaadin.demo.application.adapter.in.views.admin.components;

import com.vaadin.demo.application.domain.model.PrizeFormRecord;
import com.vaadin.demo.application.domain.model.PrizeRecord;
import com.vaadin.demo.application.domain.model.PrizeTemplateRecord;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.List;
import java.util.function.Supplier;

/**
 * Dialog component for creating and editing prizes This version uses PrizeFormRecord instead of JPA
 * entities
 */
public class PrizeFormDialog extends Dialog {

  private final Binder<PrizeFormRecord> binder;
  private final SerializableConsumer<PrizeFormRecord> saveConsumer;
  private final SerializableConsumer<Long> deleteConsumer;
  private final TextField winnerField;
  private final TextField nameField;
  private final TextArea descriptionField;
  private final TextArea templateTextField;
  private final TextField voucherCodeField;
  private final DatePicker validUntilField;
  private final Checkbox useTemplateCheck;
  private final ComboBox<PrizeTemplateRecord> prizeTemplateComboBox;
  private final Div basicInfoTab;
  private final Div templateTab;
  private final Div voucherTab;
  private final VerticalLayout rootLayout;
  private boolean isTemplateMode = false;
  private Supplier<List<PrizeRecord>> templatesSupplier;
  private Supplier<List<PrizeTemplateRecord>> prizeTemplateSupplier;

  public PrizeFormDialog(SerializableConsumer<PrizeFormRecord> saveConsumer,
      SerializableConsumer<Long> deleteConsumer) {
    super("Prize Dialog");
    this.saveConsumer = saveConsumer;
    this.deleteConsumer = deleteConsumer;

    nameField = new TextField("Name");
    nameField.setWidthFull();

    descriptionField = new TextArea("Description");
    descriptionField.setWidthFull();
    descriptionField.setHeight("100px");

    winnerField = new TextField("Winner");
    winnerField.setWidthFull();
    winnerField.setReadOnly(true);

    voucherCodeField = new TextField("Voucher Code");
    voucherCodeField.setWidthFull();
    voucherCodeField.setHelperText("If this prize has a voucher code, enter it here");

    validUntilField = new DatePicker("Valid Until");
    validUntilField.setWidthFull();
    validUntilField.setHelperText("Optional date until when the prize is valid");

    prizeTemplateComboBox = new ComboBox<>("Select Template");
    prizeTemplateComboBox.setVisible(false);
    prizeTemplateComboBox.setEnabled(false);
    prizeTemplateComboBox.setWidthFull();
    prizeTemplateComboBox.setItemLabelGenerator(PrizeTemplateRecord::name);

    templateTextField = new TextArea("Template Text");
    templateTextField.setWidthFull();
    templateTextField.setHeight("400px");
    templateTextField.setHelperText(
        "Available placeholders: {{PRIZE_NAME}}, {{WINNER_NAME}}, {{RAFFLE_DATE}}, {{VOUCHER_CODE}}, {{VALID_UNTIL}}");

    useTemplateCheck = new Checkbox("Use Template");
    useTemplateCheck.addValueChangeListener(e -> {
      boolean useTemplate = e.getValue();
      boolean usePrizeTemplate = prizeTemplateSupplier != null;


      prizeTemplateComboBox.setVisible(useTemplate && usePrizeTemplate);
      prizeTemplateComboBox.setEnabled(useTemplate && usePrizeTemplate);

      templateTextField.setEnabled(!useTemplate || isTemplateMode);

      // Update the display based on selected template
      if (useTemplate) {
        if (usePrizeTemplate && prizeTemplateComboBox.getValue() != null) {
          updateTemplateDisplay(prizeTemplateComboBox.getValue());
        }
      }
    });

    prizeTemplateComboBox.addValueChangeListener(e -> {
      if (e.getValue() != null) {
        updateTemplateDisplay(e.getValue());
      }
    });

    // Create tabs
    basicInfoTab = new Div();
    basicInfoTab.add(new VerticalLayout(nameField, descriptionField, winnerField));

    voucherTab = new Div();
    voucherTab.add(new VerticalLayout(
        new H4("Voucher Details"),
        voucherCodeField,
        validUntilField
    ));

    templateTab = new Div();
    templateTab.add(new VerticalLayout(
        useTemplateCheck,
        prizeTemplateComboBox,
        new H4("Template Text"),
        templateTextField
    ));

    Tab tab1 = new Tab("Basic Info");
    Tab tab2 = new Tab("Voucher Details");
    Tab tab3 = new Tab("Template & Text");

    Tabs tabs = new Tabs(tab1, tab2, tab3);

    tabs.addSelectedChangeListener(event -> {
      hideAllTabs();
      if (event.getSelectedTab().equals(tab1)) {
        basicInfoTab.setVisible(true);
      } else if (event.getSelectedTab().equals(tab2)) {
        voucherTab.setVisible(true);
      } else {
        templateTab.setVisible(true);
      }
    });

    hideAllTabs();
    basicInfoTab.setVisible(true);

    binder = new Binder<>(PrizeFormRecord.class);

    binder.forField(nameField).bind(PrizeFormRecord::name, PrizeFormRecord::withName);
    binder.forField(descriptionField).bind(PrizeFormRecord::description, PrizeFormRecord::withDescription);
    binder.forField(templateTextField).bind(PrizeFormRecord::templateText, PrizeFormRecord::withTemplateText);
    binder.forField(voucherCodeField).bind(PrizeFormRecord::voucherCode, PrizeFormRecord::withVoucherCode);
    binder.forField(validUntilField).bind(PrizeFormRecord::validUntil, PrizeFormRecord::withValidUntil);
    binder.forField(winnerField).bind(PrizeFormRecord::winnerName, PrizeFormRecord::withWinnerName);

    var cancelButton = new Button("Cancel", this::cancel);
    var saveButton = new Button("Save", this::save);
    saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    var deleteButton = new Button("Delete", this::delete);
    deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
    var buttonLayout = new HorizontalLayout(deleteButton, cancelButton, saveButton);
    buttonLayout.addClassName(LumoUtility.Padding.Top.LARGE);

    rootLayout = new VerticalLayout();
    rootLayout.add(
        tabs,
        basicInfoTab,
        voucherTab,
        templateTab,
        buttonLayout
    );
    rootLayout.setPadding(false);
    rootLayout.setSizeFull();
    add(rootLayout);

    setModal(true);
    setWidth("800px");
    setHeight("600px");
  }

  private void hideAllTabs() {
    basicInfoTab.setVisible(false);
    voucherTab.setVisible(false);
    templateTab.setVisible(false);
  }

  private void updateTemplateDisplay(PrizeRecord template) {
    if (template != null) {
      nameField.setValue(template.name() != null ? template.name() : "");
      descriptionField.setValue(template.description() != null ? template.description() : "");
      templateTextField.setValue(template.templateText() != null ? template.templateText() : "");

      // Set new fields if they exist
      if (template.voucherCode() != null) {
        voucherCodeField.setValue(template.voucherCode());
      }

      if (template.validUntil() != null) {
        validUntilField.setValue(template.validUntil());
      }
    }
  }

  private void updateTemplateDisplay(PrizeTemplateRecord template) {
    if (template != null) {
      nameField.setValue(template.name() != null ? template.name() : "");
      descriptionField.setValue(template.description() != null ? template.description() : "");
      templateTextField.setValue(template.templateText() != null ? template.templateText() : "");

      // Set voucher code if it exists in the template
      if (template.voucherCode() != null) {
        voucherCodeField.setValue(template.voucherCode());
      }

      // Set valid until date if it exists in the template
      if (template.validUntil() != null) {
        validUntilField.setValue(template.validUntil());
      }
    }
  }

  @Override
  protected void onAttach(AttachEvent attachEvent) {
    super.onAttach(attachEvent);


    if (prizeTemplateSupplier != null) {
      prizeTemplateComboBox.setItems(prizeTemplateSupplier.get());
    }

    nameField.focus();
  }

  private void delete(ClickEvent<Button> buttonClickEvent) {
    if (originalForm != null && originalForm.id() != null) {
      this.deleteConsumer.accept(originalForm.id());
    }
    close();
  }

  private void cancel(ClickEvent<Button> buttonClickEvent) {
    close();
  }

  private void save(ClickEvent<Button> buttonClickEvent) {
    // Create a new form with the values from the binder
    PrizeFormRecord form = createFormFromFields();
    this.saveConsumer.accept(form);
    close();
  }

  private PrizeFormRecord createFormFromFields() {
    return new PrizeFormRecord(
        originalForm != null ? originalForm.id() : null,
        nameField.getValue(),
        descriptionField.getValue(),
        templateTextField.getValue(),
        voucherCodeField.getValue(),
        validUntilField.getValue(),
        winnerField.getValue()
    );
  }

  private PrizeFormRecord originalForm;

  public void setPrizeForm(PrizeFormRecord prizeForm) {
    this.originalForm = prizeForm;
    binder.readBean(prizeForm);

    isTemplateMode = false;
    useTemplateCheck.setVisible(true);
    useTemplateCheck.setValue(false);
    setHeaderTitle("Edit Prize");
  }

  public void setTemplateSupplier(Supplier<List<PrizeRecord>> supplier) {
    this.templatesSupplier = supplier;
  }

  public void setPrizeTemplateSupplier(Supplier<List<PrizeTemplateRecord>> supplier) {
    this.prizeTemplateSupplier = supplier;
  }
}