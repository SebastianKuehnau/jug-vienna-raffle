package com.vaadin.demo.application.views.meetupsample;

import com.vaadin.demo.application.services.MeetupService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

@PageTitle("Meetup Sample")
@Route("meetupsample")
@Menu(order = 10, icon = LineAwesomeIconUrl.USER_FRIENDS_SOLID)
@PermitAll
public class MeetupSampleView extends HorizontalLayout {


  @Autowired
  MeetupService service;

  private Div content;

  public MeetupSampleView() {




    var getMe = new Button("Get Me");
    var getMeInGroups = new Button("Get me in Group");
    var getMyGroups = new Button("Get my groups");
    content = new Div();

    getMe.addClickListener(e -> {
      content.add(service.getMe().toString());
    });

    getMeInGroups.addClickListener(e -> {
      content.add(service.getMeInGroup().toString());
    });

    getMyGroups.addClickListener(e -> {
      content.add(service.getMyGroups().toString());
    });

    var query = new TextField("Query");
    var queryButton = new Button("Query absenden");
    queryButton.addClickListener(e -> {
      content.add(service.query(query.getValue()));
    });

    var commands = new VerticalLayout();



    commands.add(getMe);
    commands.add(new Hr());
    commands.add(getMeInGroups);
    commands.add(new Hr());
    commands.add(getMyGroups);
    commands.add(new Hr());
    commands.add(query);
    commands.add(queryButton);
    commands.add(new Hr());
    var clear = new Button("Clear");
    clear.addClickListener(e -> {
      content.removeAll();
    });
    commands.add(clear);

    var display = new VerticalLayout(content);

    add(commands);
    add(display);

  }
}
