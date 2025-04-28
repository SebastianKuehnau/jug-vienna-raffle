package com.vaadin.demo.application.application.service;

import com.vaadin.demo.application.domain.model.EventRecord;
import com.vaadin.demo.application.domain.model.ParticipantRecord;
import com.vaadin.demo.application.domain.model.PrizeDialogFormRecord;
import com.vaadin.demo.application.domain.model.PrizeFormRecord;
import com.vaadin.demo.application.domain.model.PrizeRecord;
import com.vaadin.demo.application.domain.model.PrizeTemplateRecord;
import com.vaadin.demo.application.domain.model.RaffleFormRecord;
import com.vaadin.demo.application.domain.model.RaffleRecord;
import java.util.List;
import java.util.Optional;

public interface RaffleApplicationService {

  Optional<RaffleRecord> getRaffleById(Long id);

  List<RaffleRecord> getAllRaffles();

  Optional<PrizeRecord> getPrizeById(Long id);

  Optional<RaffleRecord> getRaffleByMeetupEventId(String meetupEventId);

  List<PrizeRecord> getPrizesForRaffle(RaffleRecord raffle);

  List<PrizeTemplateRecord> getAllPrizeTemplateRecords();


  List<PrizeTemplateRecord> getPrizeTemplateRecordsByName(String namePattern);


  Optional<PrizeTemplateRecord> getPrizeTemplateRecordById(Long id);


  PrizeRecord createPrizeFromTemplateRecord(Long templateId, RaffleRecord raffle,
      String voucherCode);

  List<ParticipantRecord> getEligibleParticipants(RaffleRecord raffle);

  RaffleRecord createRaffle(EventRecord event);

  RaffleRecord saveRaffle(RaffleRecord raffle);

  PrizeRecord savePrize(PrizeRecord prize);

  PrizeFormRecord savePrizeForm(PrizeFormRecord prizeForm, RaffleRecord raffle);

  PrizeDialogFormRecord savePrizeDialogForm(PrizeDialogFormRecord dialogForm, RaffleRecord raffle);

  List<RaffleFormRecord> getAllRaffleForms();

  RaffleFormRecord createRaffleFromForm(String meetupEventId);

  Optional<PrizeFormRecord> getPrizeFormById(Long id);

  Optional<PrizeDialogFormRecord> getPrizeDialogFormById(Long id);

  Optional<PrizeDialogFormRecord> getPrizeTemplateDialogFormById(Long id);

  PrizeDialogFormRecord createEmptyPrizeDialogForm(boolean isTemplate);

  void deletePrizeDialogForm(PrizeDialogFormRecord form);

  PrizeTemplateRecord savePrizeTemplateRecord(PrizeTemplateRecord prizeTemplate);

  PrizeRecord awardPrize(PrizeRecord prize, ParticipantRecord participant);

  void deletePrize(Long prizeId);

  void deletePrizeTemplate(Long templateId);
}
