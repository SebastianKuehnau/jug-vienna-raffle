package com.vaadin.demo.application.adapter;

import com.vaadin.demo.application.adapter.persistence.data.Member;
import com.vaadin.demo.application.domain.model.MemberRecord;
import com.vaadin.demo.application.domain.port.MemberPort;
import com.vaadin.demo.application.adapter.persistence.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;

/**
 * Adapter implementation of the MemberPort interface
 * This service is responsible for all Member-related operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MemberServiceAdapter implements MemberPort {

    private final MemberRepository memberRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<MemberRecord> getMemberById(Long id) {
        return memberRepository.findById(id)
                .map(Mapper::toMemberRecord);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MemberRecord> getMemberByMeetupId(String meetupId) {
        return memberRepository.findByMeetupId(meetupId)
                .map(Mapper::toMemberRecord);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MemberRecord> getAllMembers(Pageable pageable) {
        return memberRepository.findAll(pageable)
                .map(Mapper::toMemberRecord);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MemberRecord> getMembers(Pageable pageable, Specification<?> specification) {
        @SuppressWarnings("unchecked")
        Specification<Member> memberSpec = (Specification<Member>) specification;
        return memberRepository.findAll(memberSpec, pageable)
                .map(Mapper::toMemberRecord);
    }

    @Override
    @Transactional
    public MemberRecord saveMember(MemberRecord memberRecord) {
        Member memberEntity;

        if (memberRecord.id() != null) {
            // Update existing member
            Optional<Member> existingMember = memberRepository.findById(memberRecord.id());
            if (existingMember.isPresent()) {
                memberEntity = existingMember.get();
                updateMemberFromRecord(memberEntity, memberRecord);
            } else {
                memberEntity = createMemberFromRecord(memberRecord);
            }
        } else {
            // Create new member
            memberEntity = createMemberFromRecord(memberRecord);
        }

        memberEntity.setLastUpdated(OffsetDateTime.now());
        Member savedMember = memberRepository.save(memberEntity);
        return Mapper.toMemberRecord(savedMember);
    }

    @Override
    @Transactional
    public void deleteMember(Long id) {
        memberRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public int countMembers() {
        return (int) memberRepository.count();
    }

    private Member createMemberFromRecord(MemberRecord record) {
        Member member = new Member();
        updateMemberFromRecord(member, record);
        return member;
    }

    private void updateMemberFromRecord(Member member, MemberRecord record) {
        if (record.id() != null) {
            member.setId(record.id());
        }
        member.setMeetupId(record.meetupId());
        member.setName(record.name());
        member.setEmail(record.email());
    }
}