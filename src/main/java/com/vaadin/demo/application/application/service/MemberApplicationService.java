package com.vaadin.demo.application.application.service;

import com.vaadin.demo.application.data.Member;
import com.vaadin.demo.application.domain.model.MemberFormRecord;
import com.vaadin.demo.application.services.MemberService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Application service for Member-related operations
 * Serves as a façade between the UI layer and the domain layer
 */
@Service
public class MemberApplicationService {
    
    private final MemberService memberService;
    
    public MemberApplicationService(MemberService memberService) {
        this.memberService = memberService;
    }
    
    /**
     * Get a member form by ID
     */
    public Optional<MemberFormRecord> getMemberFormById(Long id) {
        return memberService.get(id)
                .map(MemberFormRecord::fromMember);
    }
    
    /**
     * Get a member form by Meetup ID
     */
    public Optional<MemberFormRecord> getMemberFormByMeetupId(String meetupId) {
        return memberService.getByMeetupId(meetupId)
                .map(MemberFormRecord::fromMember);
    }
    
    /**
     * List all members as form records with pagination
     */
    public Page<MemberFormRecord> listMemberForms(Pageable pageable) {
        return memberService.list(pageable)
                .map(MemberFormRecord::fromMember);
    }
    
    /**
     * Save a member form
     */
    public MemberFormRecord saveMemberForm(MemberFormRecord memberForm) {
        Member member;
        
        if (memberForm.id() != null) {
            // Update existing member
            Optional<Member> existingMember = memberService.get(memberForm.id());
            if (existingMember.isPresent()) {
                member = memberForm.updateMember(existingMember.get());
            } else {
                member = memberForm.toMember();
            }
        } else {
            // Create new member
            member = memberForm.toMember();
        }
        
        Member savedMember = memberService.save(member);
        return MemberFormRecord.fromMember(savedMember);
    }
    
    /**
     * Delete a member by ID
     */
    public void deleteMember(Long id) {
        memberService.delete(id);
    }
    
    /**
     * Count all members
     */
    public int countMembers() {
        return memberService.count();
    }
}