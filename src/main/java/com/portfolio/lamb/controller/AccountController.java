package com.portfolio.lamb.controller;


import com.portfolio.lamb.config.CustomClientRegistrationRepository;
import com.portfolio.lamb.domain.Member;
import com.portfolio.lamb.domain.MemberDto;
import com.portfolio.lamb.service.MemberService;
import com.portfolio.lamb.validator.MemberDtoValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

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

    @Autowired
    private MemberDtoValidator memberDtoValidator;


    private static final String requestBaseUri = "/oauth2/authorization/";

    @GetMapping("/login")
    public String getLoginPage(@RequestParam(value = "error", required = false) String error, Model model) {
        Map<String, String> urls = new LinkedHashMap<>(); // social login urls
        CustomClientRegistrationRepository.clients.forEach(
                registrationId -> urls.put(registrationId, requestBaseUri + registrationId));
        model.addAttribute("urls", urls);
        model.addAttribute("error", error);
        return "account/login";
    }

    @GetMapping({"/nickname"})
    public String getNickname(@RequestParam long id) {
        Member member = memberService.getMemberById(id);
        return member != null ? member.getNickname() : null;
    }


    @GetMapping({"/register", "/update", "/password", "/signOut"})
    public void register(Model model) {
        model.addAttribute("memberDto", new MemberDto());
    }

    @PostMapping("/register")
    public String register(@ModelAttribute @Valid MemberDto memberDto, BindingResult bindingResult) {
        log.info("on register request : " + memberDto.toString());
        memberDtoValidator.validateUsername(memberDto, bindingResult);
        if (bindingResult.hasErrors()) return "account/register";

        memberDto.setNickname(memberDto.getUsername());
        memberDto.setPassword(passwordEncoder.encode(memberDto.getPassword()));
        memberService.save(new Member(memberDto));
        return "redirect:/";
    }

    @Transactional
    @PostMapping("/update")
    public String update(Authentication authentication,
                         @ModelAttribute @Valid MemberDto memberDto, BindingResult bindingResult) {
        Optional<Member> optionalMember = memberService.getMemberByUsername(authentication.getName());
        if (optionalMember.isEmpty()) {
            bindingResult.rejectValue("member", "MEMBER_NOT_FOUND", "present member not found");
            return "redirect:/board/list";
        }
        Member member = optionalMember.get();
        log.info("on update " + member.getUsername() + " request : " + memberDto.toString());
        if (StringUtils.hasLength(memberDto.getNickname())) {
            memberDto.setUsername(member.getUsername());
            memberDtoValidator.validateNickname(memberDto, bindingResult);
            if (!member.isSocial()) {
                memberDto.setComparePassword(member.getPassword());
                memberDtoValidator.passwordCheck(memberDto, bindingResult);
            }
            if (bindingResult.hasErrors()) return "account/update";
        }

        member.updateNicknameOrEmail(memberDto);
        session.setAttribute("member_nickname", member.getNickname());
        session.setAttribute("member_email", member.getEmail());
        return "redirect:/";
    }

    @Transactional
    @PostMapping("/password")
    public String password(Authentication authentication,
                           @ModelAttribute @Valid MemberDto memberDto, BindingResult bindingResult) {
        Optional<Member> optionalMember = memberService.getMemberByUsername(authentication.getName());
        if (optionalMember.isEmpty()) {
            bindingResult.rejectValue("member", "MEMBER_NOT_FOUND", "present member not found");
            return "redirect:/board/list";
        }
        Member member = optionalMember.get();
        log.info("on password " + member.getUsername() + " request : " + memberDto.toString());
        if (member.isSocial()) return "redirect:/";

        if (!memberDtoValidator.validConfirm(memberDto, bindingResult)) return "account/password";

        memberDto.setComparePassword(member.getPassword());
        memberDtoValidator.passwordCheck(memberDto, bindingResult);
        if (bindingResult.hasErrors()) return "account/password";

        member.updatePassword(passwordEncoder.encode(memberDto.getConfirm()));
        return "redirect:/";
    }

    @Transactional
    @PostMapping("/signOut")
    public String signOut(Authentication authentication,
                          @ModelAttribute @Valid MemberDto memberDto, BindingResult bindingResult) {
        Optional<Member> optionalMember = memberService.getMemberByUsername(authentication.getName());
        if (optionalMember.isEmpty()) {
            bindingResult.rejectValue("member", "MEMBER_NOT_FOUND", "present member not found");
            return "redirect:/board/list";
        }
        Member member = optionalMember.get();
        log.info("on signOut " + member.getUsername() + " request : " + memberDto.toString());
        memberDto.setComparePassword(member.getPassword());
        memberDtoValidator.passwordCheck(memberDto, bindingResult);
        if (bindingResult.hasErrors()) return "account/signOut";

        memberService.deleteMember(member);
        log.info("member deleted");
        session.invalidate();
        log.info("session invalidated");
        authentication.setAuthenticated(false);
        log.info("authentication revoked");
        return "redirect:/";
    }
}