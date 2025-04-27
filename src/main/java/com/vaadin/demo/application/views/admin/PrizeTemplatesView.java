package com.vaadin.demo.application.views.admin;

import com.vaadin.demo.application.application.service.RaffleApplicationService;
import com.vaadin.demo.application.domain.model.PrizeDialogFormRecord;
import com.vaadin.demo.application.domain.model.PrizeTemplateRecord;
import com.vaadin.demo.application.views.MainLayout;
import com.vaadin.demo.application.views.admin.components.IconButton;
import com.vaadin.demo.application.views.admin.components.PrizeDialog;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.time.format.DateTimeFormatter;

@Route(value = "prize-templates", layout = MainLayout.class)
@PageTitle("Prize Templates")
@com.vaadin.flow.server.auth.AnonymousAllowed
public class PrizeTemplatesView extends VerticalLayout {

    private final Grid<PrizeTemplateRecord> templateGrid = new Grid<>(PrizeTemplateRecord.class);
    private final RaffleApplicationService raffleService;

    public PrizeTemplatesView(RaffleApplicationService raffleService) {
        this.raffleService = raffleService;
        
        H2 header = new H2("Prize Templates");
        header.addClassNames(LumoUtility.Margin.Top.MEDIUM, LumoUtility.Margin.Bottom.MEDIUM);
        
        configureGrid();
        
        Button addButton = new IconButton(VaadinIcon.PLUS.create(), this::addTemplate);
        addButton.setText("Add Template");
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        
        Button legacyTemplatesButton = new Button("Legacy Templates", VaadinIcon.TIME_BACKWARD.create(), e -> {
            // Navigate to legacy template view if needed
        });
        
        HorizontalLayout toolbar = new HorizontalLayout(legacyTemplatesButton, addButton);
        toolbar.setWidthFull();
        toolbar.setJustifyContentMode(JustifyContentMode.END);
        
        add(header, toolbar, templateGrid);
        setSizeFull();
        
        refreshTemplates();
    }
    
    private void configureGrid() {
        templateGrid.setColumns("id", "name", "description");
        templateGrid.getColumnByKey("id").setHeader("ID").setAutoWidth(true).setFlexGrow(0);
        templateGrid.getColumnByKey("name").setHeader("Template Name").setAutoWidth(true);
        templateGrid.getColumnByKey("description").setHeader("Description").setAutoWidth(true);
        
        // Add custom columns for voucher and valid until
        templateGrid.addColumn(template -> template.voucherCode() != null ? 
                "Yes" : "No")
            .setHeader("Has Voucher")
            .setAutoWidth(true);
            
        templateGrid.addColumn(template -> template.validUntil() != null ? 
                template.validUntil().format(DateTimeFormatter.ISO_LOCAL_DATE) : "")
            .setHeader("Valid Until")
            .setAutoWidth(true);
        
        templateGrid.addComponentColumn(this::createPreviewButton)
            .setHeader("Preview")
            .setAutoWidth(true)
            .setFlexGrow(0);
            
        templateGrid.asSingleSelect().addValueChangeListener(this::selectTemplate);
        templateGrid.setWidthFull();
    }
    
    private Button createPreviewButton(PrizeTemplateRecord template) {
        Button button = new Button("Preview", VaadinIcon.EYE.create());
        button.addClickListener(event -> {
            // Create a form record for preview (read-only)
            PrizeDialogFormRecord previewForm = PrizeDialogFormRecord.fromPrizeTemplateRecord(template);
            
            PrizeDialog previewDialog = new PrizeDialog(
                // No-op functions since this is read-only
                form -> {}, 
                form -> {}
            );
            
            previewDialog.setPrizeForm(previewForm);
            previewDialog.setHeaderTitle("Preview Template");
            previewDialog.open();
        });
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        return button;
    }

    private void selectTemplate(AbstractField.ComponentValueChangeEvent<Grid<PrizeTemplateRecord>, PrizeTemplateRecord> event) {
        PrizeTemplateRecord template = event.getValue();
        if (template == null) {
            return;
        }
        
        // Convert directly to form record
        PrizeDialogFormRecord formRecord = PrizeDialogFormRecord.fromPrizeTemplateRecord(template);
        
        PrizeDialog dialog = new PrizeDialog(this::saveTemplate, this::deleteTemplate);
        dialog.setPrizeForm(formRecord);
        dialog.open();
    }

    private void addTemplate(ClickEvent<Button> event) {
        // Create an empty template form
        PrizeDialogFormRecord emptyForm = PrizeDialogFormRecord.emptyTemplate();
        
        PrizeDialog dialog = new PrizeDialog(this::saveTemplate, this::deleteTemplate);
        dialog.setPrizeForm(emptyForm);
        dialog.setTemplateMode(true);
        dialog.open();
    }

    private void saveTemplate(PrizeDialogFormRecord formRecord) {
        // Save directly using the application service
        raffleService.savePrizeDialogForm(formRecord, null);
        refreshTemplates();
    }

    private void deleteTemplate(PrizeDialogFormRecord formRecord) {
        raffleService.deletePrizeDialogForm(formRecord);
        refreshTemplates();
    }

    private void refreshTemplates() {
        templateGrid.setItems(raffleService.getAllPrizeTemplateRecords());
    }
}