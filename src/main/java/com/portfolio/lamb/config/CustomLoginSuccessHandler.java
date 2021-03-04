package com.portfolio.lamb.config;

import com.portfolio.lamb.domain.user.Member;
import com.portfolio.lamb.domain.user.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Slf4j
@Component
public class CustomLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Autowired
    private MemberService memberService;

    @Autowired
    private HttpSession session;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {
        super.onAuthenticationSuccess(request, response, authentication);
        uploadUserInfoOnSession(authentication);
    }

    private void uploadUserInfoOnSession(Authentication authentication) {
        Member member = memberService.getMemberByUsername(authentication.getName());
        if (member != null) {
            log.info("Login success :: [" + member.getId() + "] " + member.getNickname() + " (" + member.getUsername() + ")");
            session.setAttribute("member_id", member.getId());
            session.setAttribute("member_username", member.getUsername());
            session.setAttribute("member_nickname", member.getNickname());
            session.setAttribute("member_social", member.isSocial());
        } else {
            log.info("Login success :: not found member info");
        }
    }
}
