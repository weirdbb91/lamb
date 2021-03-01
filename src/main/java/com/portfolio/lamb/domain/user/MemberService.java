package com.portfolio.lamb.domain.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    public Member save(Member member) {
        member.setRole(Role.USER);
        return memberRepository.save(member);
    }

    public boolean existName(String name) {
        return memberRepository.findByUsername(name).isPresent();
    }
}
