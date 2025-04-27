package com.vaadin.demo.application.views.masterdetail;

import com.vaadin.demo.application.application.service.MemberApplicationService;
import com.vaadin.demo.application.domain.model.MemberFormRecord;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import jakarta.annotation.security.PermitAll;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.util.Optional;

@PageTitle("Member Management")
@Route("members/:memberId?/:action?(edit)")
@Menu(order = 1, icon = LineAwesomeIconUrl.USERS_SOLID, title = "Members")
@Uses(Icon.class)
@PermitAll
public class MasterDetailView extends Div implements BeforeEnterObserver {

    private final String MEMBER_ID = "memberId";
    private final String MEMBER_EDIT_ROUTE_TEMPLATE = "members/%s/edit";

    private final Grid<MemberFormRecord> grid = new Grid<>(MemberFormRecord.class, false);

    private TextField meetupId;
    private TextField name;
    private TextField email;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    private final BeanValidationBinder<MemberFormRecord> binder;

    private MemberFormRecord memberForm;

    private final MemberApplicationService memberApplicationService;

    public MasterDetailView(MemberApplicationService memberApplicationService) {
        this.memberApplicationService = memberApplicationService;
        addClassNames("master-detail-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn(MemberFormRecord::meetupId).setHeader("Meetup ID").setAutoWidth(true);
        grid.addColumn(MemberFormRecord::name).setHeader("Name").setAutoWidth(true);
        grid.addColumn(MemberFormRecord::email).setHeader("Email").setAutoWidth(true);
        grid.addColumn(member -> member.lastUpdated() != null ? 
                member.lastUpdated().toLocalDate().toString() : "")
            .setHeader("Last Updated").setAutoWidth(true);

        grid.setItems(query -> memberApplicationService.listMemberForms(
                VaadinSpringDataHelpers.toSpringPageRequest(query)).stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(MEMBER_EDIT_ROUTE_TEMPLATE, event.getValue().id()));
            } else {
                clearForm();
                UI.getCurrent().navigate(MasterDetailView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(MemberFormRecord.class);

        // Bind fields. This is where you'd define e.g. validation rules
        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.memberForm == null) {
                    this.memberForm = MemberFormRecord.empty();
                }
                
                // Create an updated copy of the form with the field values
                MemberFormRecord updatedForm = new MemberFormRecord(
                    this.memberForm.id(),
                    meetupId.getValue(),
                    name.getValue(),
                    email.getValue(),
                    this.memberForm.lastUpdated()
                );
                
                // Save the updated form
                memberApplicationService.saveMemberForm(updatedForm);
                
                clearForm();
                refreshGrid();
                Notification.show("Member details saved");
                UI.getCurrent().navigate(MasterDetailView.class);
            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show(
                        "Error updating the data. Somebody else has updated the record while you were making changes.");
                n.setPosition(Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (Exception validationException) {
                Notification.show("Failed to update the data. Check again that all values are valid");
            }
        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> memberId = event.getRouteParameters().get(MEMBER_ID).map(Long::parseLong);
        if (memberId.isPresent()) {
            Optional<MemberFormRecord> memberFormFromBackend = memberApplicationService.getMemberFormById(memberId.get());
            if (memberFormFromBackend.isPresent()) {
                populateForm(memberFormFromBackend.get());
            } else {
                Notification.show(
                        String.format("The requested member was not found, ID = %s", memberId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(MasterDetailView.class);
            }
        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        meetupId = new TextField("Meetup ID");
        name = new TextField("Name");
        email = new TextField("Email");
        formLayout.add(meetupId, name, email);

        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(MemberFormRecord value) {
        this.memberForm = value;
        binder.readBean(this.memberForm);
    }
}