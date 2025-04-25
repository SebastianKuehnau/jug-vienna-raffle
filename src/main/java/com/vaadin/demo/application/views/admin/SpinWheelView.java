package com.vaadin.demo.application.views.admin;

import com.vaadin.demo.application.data.Prize;
import com.vaadin.demo.application.services.PrizeService;
import com.vaadin.demo.application.services.meetup.MeetupService;
import com.vaadin.demo.application.views.admin.details.DetailsMainLayout;
import com.vaadin.demo.application.views.admin.details.PrizesCrudSubView;
import com.vaadin.demo.application.views.spinwheel.component.ReactSpinWheel;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@PageTitle( "JUG Vienna Raffle Spin Wheel")
@Route("raffle-admin/spin-wheel")
public class SpinWheelView extends Div implements HasUrlParameter<Long> {

    private final PrizeService prizeService;
    private final MeetupService meetupService;
    private final ReactSpinWheel reactSpinWheel;

    private Map<String, MeetupService.Member> members = new HashMap();
    private Optional<Prize> currentPrize;

    public SpinWheelView(PrizeService prizeService, MeetupService meetupService) {
        this.prizeService = prizeService;
        this.meetupService = meetupService;
        reactSpinWheel = new ReactSpinWheel();
        reactSpinWheel.addOnFinishSpin(s -> {
            var winner = members.get(s);
            var winnerDialog = new WinnerDialog(winner);
            winnerDialog.open();
        });
        reactSpinWheel.setSizeFull();
        add(reactSpinWheel);
    }

    public class WinnerDialog extends Dialog {
        public WinnerDialog(MeetupService.Member winner) {
            add(new H1("Winner: " + winner.name() + " (" + winner.id() + ")"));
            var acceptButton = new Button("Accept Price", e -> {
                currentPrize.ifPresent(prize -> {
                    prize.setWinner(winner.name() + " (" + winner.id() + ")");
                    prizeService.save(prize);
                });
                close();
                RouteParam routeParam = new RouteParam(DetailsMainLayout.RAFFLE_ID_PARAMETER, currentPrize.get().getRaffle().getId());
                UI.getCurrent().navigate(PrizesCrudSubView.class, routeParam);
            });
            acceptButton.addThemeVariants(
                    ButtonVariant.LUMO_PRIMARY,
                    ButtonVariant.LUMO_SUCCESS,
                    ButtonVariant.LUMO_LARGE);
            var declineButton = new Button("Decline Price", e -> close());
            declineButton.addThemeVariants(
                    ButtonVariant.LUMO_ERROR,
                    ButtonVariant.LUMO_PRIMARY,
                    ButtonVariant.LUMO_LARGE);
            add(new HorizontalLayout(declineButton, acceptButton));
            setModal(true);
            setCloseOnEsc(true);
            setCloseOnOutsideClick(true);
        }
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, Long prizeId) {
        Optional<Prize> optionalPrize = prizeService.get(prizeId);
        this.currentPrize = optionalPrize;

        optionalPrize.flatMap(prize -> meetupService.getEvent(prize.getRaffle().getMeetup_event_id()))
            .ifPresent(meetupEvent -> {
                for (MeetupService.Member member : meetupEvent.members()) {
                    this.members.put(member.id(), member);
                }

                List<String> eligibleMemberIds = meetupEvent.members().stream()
                    .filter(member -> !member.isOrganizer() && !member.hasEnteredRaffle())
                    .map(MeetupService.Member::id)
                    .toList();

                reactSpinWheel.setItems(eligibleMemberIds);
            });
    }
}
