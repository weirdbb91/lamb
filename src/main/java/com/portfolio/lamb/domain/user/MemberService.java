package com.portfolio.lamb.domain.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

    public boolean validName(String name) {
        return memberRepository.findByUsername(name).isEmpty()
                && memberRepository.findByNickname(name).isEmpty();
    }

    public Member getMemberByUsername(String username) {
        return memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("not found username : " + username));
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
}
