package com.vaadin.demo.application.views.admin.details;

import com.vaadin.demo.application.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@PageTitle("Raffle Details")
@RoutePrefix("raffle-admin/:" + DetailsMainLayout.RAFFLE_ID_PARAMETER)
@ParentLayout(MainLayout.class)
public class DetailsMainLayout extends VerticalLayout implements RouterLayout, BeforeEnterObserver {
    public static final String RAFFLE_ID_PARAMETER = "raffleID";

    private final Map<Class<? extends Component>, Tab> navigationTargetToTab = new HashMap<>();
    private final Tabs tabs;
    
    public DetailsMainLayout() {
        tabs = new Tabs();
        add(tabs);
        setSizeFull();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        //Load raffle from parameter
        Optional<String> raffleIdParam = event.getRouteParameters().get(RAFFLE_ID_PARAMETER);

        if (raffleIdParam.isEmpty()) {
            return;
        }

        raffleIdParam.ifPresent(this::updateTabs);

        // update selected tab
        if (navigationTargetToTab.containsKey(event.getNavigationTarget())) {
            tabs.setSelectedTab(navigationTargetToTab.get(event.getNavigationTarget()));
        }
    }
    
    private void updateTabs(String raffleId) {
        tabs.removeAll();

        RouteParameters params = new RouteParameters(
                new RouteParam(RAFFLE_ID_PARAMETER, raffleId));
        
        Tab detailsTab = createTabWithRouterLink("Details", DetailsSubView.class, params);
        Tab prizeTab = createTabWithRouterLink("Prizes", PrizesCrudSubView.class, params);
        Tab participantTab = createTabWithRouterLink("Participants", ParticipantsSubView.class, params);
        
        tabs.add(detailsTab, prizeTab, participantTab);
    }
    
    private <T extends Component> Tab createTabWithRouterLink(String title, Class<T> targetClass, RouteParameters params) {
        RouterLink link = new RouterLink(title, targetClass, params);
        Tab tab = new Tab(link);
        navigationTargetToTab.put(targetClass, tab);
        return tab;
    }
}