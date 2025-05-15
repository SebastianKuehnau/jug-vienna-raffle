package com.vaadin.demo.application.application.service;

import com.vaadin.demo.application.adapter.out.persistence.data.Member;
import com.vaadin.demo.application.domain.model.MemberFormRecord;
import com.vaadin.demo.application.domain.model.MemberRecord;
import com.vaadin.demo.application.application.port.out.MemberPort;
import com.vaadin.demo.application.application.port.in.MemberApplicationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Application service for Member-related operations
 * Serves as a façade between the UI layer and the domain layer
 */
@Service
public class MemberApplicationServiceImpl implements MemberApplicationService {

    private final MemberPort memberPort;

    public MemberApplicationServiceImpl(MemberPort memberPort) {
        this.memberPort = memberPort;
    }

    /**
     * Get a member form by ID
     */
    @Override
    public Optional<MemberFormRecord> getMemberFormById(Long id) {
        return memberPort.getMemberById(id)
                .map(this::memberRecordToFormRecord);
    }

    /**
     * Get a member form by Meetup ID
     */
    @Override
    public Optional<MemberFormRecord> getMemberFormByMeetupId(String meetupId) {
        return memberPort.getMemberByMeetupId(meetupId)
                .map(this::memberRecordToFormRecord);
    }

    /**
     * List all members as form records with pagination
     */
    @Override
    public Page<MemberFormRecord> listMemberForms(Pageable pageable) {
        return memberPort.getAllMembers(pageable)
                .map(this::memberRecordToFormRecord);
    }

    /**
     * List filtered members as form records with pagination
     */
    @Override
    public Page<MemberFormRecord> listMemberForms(Pageable pageable, Specification<Member> filter) {
        return memberPort.getMembers(pageable, filter)
                .map(this::memberRecordToFormRecord);
    }

    /**
     * Save a member form
     */
    @Override
    public MemberFormRecord saveMemberForm(MemberFormRecord memberForm) {
        MemberRecord memberRecord = formRecordToMemberRecord(memberForm);
        MemberRecord savedMember = memberPort.saveMember(memberRecord);
        return memberRecordToFormRecord(savedMember);
    }

    /**
     * Delete a member by ID
     */
    @Override
    public void deleteMember(Long id) {
        memberPort.deleteMember(id);
    }

    /**
     * Count all members
     */
    @Override
    public int countMembers() {
        return memberPort.countMembers();
    }

}