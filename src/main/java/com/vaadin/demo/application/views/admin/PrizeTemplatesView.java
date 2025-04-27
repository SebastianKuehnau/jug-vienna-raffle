package com.vaadin.demo.application.views.admin;

import com.vaadin.demo.application.application.service.RaffleApplicationService;
import com.vaadin.demo.application.data.Prize;
import com.vaadin.demo.application.data.PrizeTemplate;
import com.vaadin.demo.application.domain.model.PrizeRecord;
import com.vaadin.demo.application.domain.model.PrizeTemplateRecord;
import com.vaadin.demo.application.views.MainLayout;
import com.vaadin.demo.application.views.admin.components.IconButton;
import com.vaadin.demo.application.views.admin.components.PrizeDialog;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.time.LocalDate;
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
            PrizeDialog previewDialog = new PrizeDialog(
                // No-op functions since this is read-only
                prize -> {}, 
                prize -> {}
            );
            Prize viewPrize = new Prize();
            viewPrize.setId(template.id());
            viewPrize.setName(template.name());
            viewPrize.setDescription(template.description());
            viewPrize.setTemplateText(template.templateText());
            viewPrize.setVoucherCode(template.voucherCode());
            viewPrize.setValidUntil(template.validUntil());
            viewPrize.setTemplate(true);
            
            previewDialog.setPrize(viewPrize);
            // Set dialog to read-only mode
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
        
        // Convert to regular JPA entity
        Prize templateEntity = new Prize();
        templateEntity.setId(template.id());
        templateEntity.setName(template.name());
        templateEntity.setDescription(template.description());
        templateEntity.setTemplateText(template.templateText());
        templateEntity.setVoucherCode(template.voucherCode());
        templateEntity.setValidUntil(template.validUntil());
        templateEntity.setTemplate(true);
        
        PrizeDialog dialog = new PrizeDialog(this::saveTemplate, this::deleteTemplate);
        dialog.setPrize(templateEntity);
        dialog.open();
    }

    private void addTemplate(ClickEvent<Button> event) {
        // Create a new template record
        PrizeTemplate newTemplate = new PrizeTemplate();
        newTemplate.setName("");
        newTemplate.setDescription("");
        newTemplate.setTemplateText("");
        
        // Convert to Prize for dialog (temporary solution until UI is updated)
        Prize uiPrize = new Prize();
        uiPrize.setName("");
        uiPrize.setDescription("");
        uiPrize.setTemplateText("");
        uiPrize.setTemplate(true);
        
        PrizeDialog dialog = new PrizeDialog(this::saveTemplate, this::deleteTemplate);
        dialog.setPrize(uiPrize);
        dialog.setTemplateMode(true);
        dialog.open();
    }

    private void saveTemplate(Prize templateEntity) {
        // Convert UI Prize to domain PrizeTemplateRecord
        PrizeTemplateRecord templateRecord = new PrizeTemplateRecord(
            templateEntity.getId(),
            templateEntity.getName(),
            templateEntity.getDescription(),
            templateEntity.getTemplateText(),
            templateEntity.getVoucherCode(),
            templateEntity.getValidUntil()
        );
        
        raffleService.savePrizeTemplateRecord(templateRecord);
        refreshTemplates();
    }

    private void deleteTemplate(Prize templateEntity) {
        raffleService.deletePrizeTemplate(templateEntity.getId());
        refreshTemplates();
    }

    private void refreshTemplates() {
        templateGrid.setItems(raffleService.getAllPrizeTemplateRecords());
    }
}