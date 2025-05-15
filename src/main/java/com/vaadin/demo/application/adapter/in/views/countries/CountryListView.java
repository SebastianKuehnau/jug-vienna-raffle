package com.vaadin.demo.application.adapter.in.views.countries;

import com.vaadin.demo.application.domain.model.CountryRecord;
import com.vaadin.demo.application.services.CountryService;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.vaadin.lineawesome.LineAwesomeIconUrl;


@AnonymousAllowed
@Menu(order = 1, icon = LineAwesomeIconUrl.GLOBE_EUROPE_SOLID)
@Route("country-list")
public class CountryListView extends VerticalLayout {

    private final Grid<CountryRecord> grid = new Grid<>(CountryRecord.class);

    public CountryListView(CountryService countryService) {

        var filterField = new TextField();
        filterField.setPlaceholder("Filter by name");
        filterField.setValueChangeMode(ValueChangeMode.EAGER);
        filterField.addValueChangeListener(event -> {
            grid.setItems(countryService.searchCountriesByPrefix(event.getValue()));
        });

        grid.setItems(countryService.fetchCountryList());
        add(filterField, grid);
        setSizeFull();
    }
}
