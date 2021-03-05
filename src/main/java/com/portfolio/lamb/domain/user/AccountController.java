package com.portfolio.lamb.domain.user;


import com.portfolio.lamb.domain.user.Member;
import com.portfolio.lamb.domain.user.MemberPasswordDTO;
import com.portfolio.lamb.domain.user.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private HttpSession session;

    private static final List<String> clients = Arrays.asList("google", "facebook", "github", "naver", "kakao");
    private static final String requestBaseUri = "/oauth2/authorization/";

    @GetMapping("/login")
    public String getLoginPage(Model model, Principal principal) {
        if (principal != null) return "redirect:/";

        Map<String, String> urls = new LinkedHashMap<>(); // social login urls
        clients.forEach(registrationId -> urls.put(registrationId, requestBaseUri + registrationId));
        model.addAttribute("urls", urls);
        return "account/login";
    }

    @GetMapping("/register")
    public String register(Principal principal) {
        return principal != null ? "redirect:/" : "account/register";
    }

    @PostMapping("/register")
    public String register(Member member, Model model) {
        try {
            Assert.isTrue(member.getPassword() != null, "input password");

            String username = member.getUsername();
            Assert.isTrue(clients.stream().noneMatch(username::startsWith),
                    "username can not start with social client's name");
            Assert.isTrue(memberService.validName(username), username + " is using");

            member.setPassword(passwordEncoder.encode(member.getPassword()));
            member.setNickname(member.getUsername());
            memberService.save(member);
        } catch (Exception e) {
            model.addAttribute("error", e.toString());
            return "account/register";
        }
        return "redirect:/";
    }

    @GetMapping("/update")
    public String update(Principal principal, Model model) {
        if (principal.getName() == null) {
            model.addAttribute("error", "login first");
            return "redirect:/account/login";
        }
        return "account/update";
    }


    @Transactional
    @PostMapping("/update")
    public String update(Member request, Principal principal, Model model) {
        Member member = memberService.getMemberByUsername(principal.getName());
        try {
            if (!member.isSocial()) {
                Assert.isTrue(passwordEncoder.matches(request.getPassword(),
                        member.getPassword()), "wrong password");
            }
            String nickname = request.getNickname();
            Assert.isTrue(nickname.equals(member.getNickname()) ||
                            clients.stream().noneMatch(nickname::startsWith),
                    "nickname can not start with social client's name");
            Assert.isTrue(nickname.equals(member.getUsername()) ||
                    memberService.validName(nickname), nickname + " is using");
            member.updateNicknameOrEmail(request);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "account/update";
        }
        session.setAttribute("member_nickname", member.getNickname());
        session.setAttribute("member_email", member.getEmail());
        return "redirect:/";
    }

    @GetMapping("/changePassword")
    public String changePassword(Principal principal, Model model) {
        if (principal.getName() == null) {
            model.addAttribute("error", "login first");
            return "redirect:/account/login";
        }
        return "account/changePassword";
    }


    @Transactional
    @PostMapping("/changePassword")
    public String changePassword(Principal principal, MemberPasswordDTO memberPasswordDTO, Model model) {
        Member member = memberService.getMemberByUsername(principal.getName());
        if (member.isSocial()) return "redirect:/";

        log.info("member.getUsername() : " + member.getUsername());
        try {
            String password = memberPasswordDTO.getPassword();
            String newPassword = memberPasswordDTO.getNewPassword();
            String confirm = memberPasswordDTO.getConfirm();
            log.info("password : " + password);
            log.info("newPassword : " + newPassword);
            log.info("confirm : " + confirm);

            assert newPassword != null;
            Assert.isTrue(newPassword.equals(confirm), "new password and confirm are not same");
            Assert.isTrue(passwordEncoder.matches(password,
                    member.getPassword()), "wrong password");
            member.updatePassword(passwordEncoder.encode(newPassword));
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            log.info(e.toString());
            return "account/changePassword";
        }
        return "redirect:/";
    }

    @GetMapping("/signOut")
    public String signOut(Principal principal, Model model) {
        if (principal.getName() == null) {
            model.addAttribute("error", "login first");
            return "redirect:/account/login";
        }
        return "account/signOut";
    }


    @Transactional
    @PostMapping("/signOut")
    public String signOut(Authentication authentication, Principal principal, Member request, Model model) {
        Member member = memberService.getMemberByUsername(principal.getName());

        log.info("member.getUsername() : " + member.getUsername());
        if (!member.isSocial()) {
            try {
                Assert.isTrue(passwordEncoder.matches(request.getPassword(),
                        member.getPassword()), "wrong password");
            } catch (Exception e) {
                model.addAttribute("error", e.getMessage());
                log.info(e.toString());
                return "account/signOut";
            }
        }
        memberService.deleteMember(member);
        log.info("member deleted");
        session.invalidate();
        log.info("session invalidated");
        authentication.setAuthenticated(false);
        log.info("authentication revoked");
        return "redirect:/";
    }
}