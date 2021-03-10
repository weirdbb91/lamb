package com.portfolio.lamb.service;

import com.portfolio.lamb.domain.Member;
import com.portfolio.lamb.repository.MemberRepository;
import com.portfolio.lamb.domain.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    public Member save(Member member) {
        member.setRole(Role.USER);
        return memberRepository.save(member);
    }

    public boolean isUniqueUsername(String name) {
        return memberRepository.findByUsername(name).isEmpty();
    }

    public boolean isUniqueNickname(String name) {
        return memberRepository.findByNickname(name).isEmpty();
    }


    public Optional<Member> getMemberByUsername(String username) {
        return memberRepository.findByUsername(username);
    }

    public Member getOrCreateSocialMember(String username) {
        Optional<Member> optionalMember = memberRepository.findByUsername(username);
        Member member;
        if (optionalMember.isEmpty()) {
            log.info("getOrCreateSocialMember :: not found username :: create new Member");
            member = new Member().socialMember(username);
            memberRepository.save(member);
            log.info("getOrCreateSocialMember :: new Member saved well");
        } else {
            member = optionalMember.get();
        }
        log.info("getOrCreateSocialMember :: Member loaded");
        return member;
    }

    public void deleteMember(Member member) {
        memberRepository.deleteById(member.getId());
    }

    public Member getMemberById(long id) {
        return memberRepository.findById(id).orElse(null);
    }
}
