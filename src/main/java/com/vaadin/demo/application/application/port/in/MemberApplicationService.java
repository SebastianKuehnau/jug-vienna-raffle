package com.vaadin.demo.application.application.port.in;

import com.vaadin.demo.application.adapter.out.persistence.data.Member;
import com.vaadin.demo.application.domain.model.MemberFormRecord;
import com.vaadin.demo.application.domain.model.MemberRecord;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface MemberApplicationService {

  Optional<MemberFormRecord> getMemberFormById(Long id);

  Optional<MemberFormRecord> getMemberFormByMeetupId(String meetupId);

  Page<MemberFormRecord> listMemberForms(Pageable pageable);

  Page<MemberFormRecord> listMemberForms(Pageable pageable, Specification<Member> filter);

  MemberFormRecord saveMemberForm(MemberFormRecord memberForm);

  void deleteMember(Long id);

  int countMembers();

  /**
   * Convert domain MemberRecord to UI MemberFormRecord
   */
  default MemberFormRecord memberRecordToFormRecord(MemberRecord record) {
    return new MemberFormRecord(
        record.id(),
        record.meetupId(),
        record.name(),
        record.email(),
        record.lastUpdated()
    );
  }

  /**
   * Convert UI MemberFormRecord to domain MemberRecord
   */
  default MemberRecord formRecordToMemberRecord(MemberFormRecord form) {
    return new MemberRecord(
        form.id(),
        form.meetupId(),
        form.name(),
        form.email(),
        form.lastUpdated()
    );
  }
}
