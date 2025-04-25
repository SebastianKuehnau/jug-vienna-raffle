package com.vaadin.demo.application.views.spinwheel;

import com.vaadin.demo.application.views.spinwheel.component.ReactSpinWheel;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.util.ArrayList;

@AnonymousAllowed
@Menu(icon = LineAwesomeIconUrl.SPINNER_SOLID, title = "Spin Wheel Demo")
@Route("spin-wheel")
public class SpinWheelView extends VerticalLayout {

    public SpinWheelView() {
        Span title = new Span("Spin Wheel Demo");
        add(title);

        var reactSpinWheel = new ReactSpinWheel();
        reactSpinWheel.setSizeFull();
        var itemList = new ArrayList<String>();
        for (int i = 0; i < 100; i++) {
            itemList.add("Item " + i);
        }
        reactSpinWheel.setItems(itemList);
        reactSpinWheel.addOnFinishSpin(s -> {});
        add(reactSpinWheel);

        setSizeFull();
    }
}
