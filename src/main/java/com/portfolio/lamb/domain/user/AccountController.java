package com.portfolio.lamb.domain.user;


import com.portfolio.lamb.config.CustomClientRegistrationRepository;
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
import java.security.Principal;
import java.util.LinkedHashMap;
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


    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("memberDto", new MemberDto());
        return "account/register";
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


    @GetMapping("/update")
    public String update(Model model) {
        model.addAttribute("memberDto", new MemberDto());
        return "account/update";
    }

    @Transactional
    @PostMapping("/update")
    public String update(Principal principal,
                         @ModelAttribute @Valid MemberDto memberDto, BindingResult bindingResult) {
        Member member = memberService.getMemberByUsername(principal.getName());
        log.info("on update " + member.getUsername() + " request : " + memberDto.toString());
        if (StringUtils.hasLength(memberDto.getNickname())) {
            memberDto.setUsername(member.getUsername());
            memberDtoValidator.validateNickname(memberDto, bindingResult);
        }
        memberDto.setComparePassword(member.getPassword());
        memberDtoValidator.passwordCheck(memberDto, bindingResult);
        if (bindingResult.hasErrors()) return "account/update";

        member.updateNicknameOrEmail(memberDto);
        session.setAttribute("member_nickname", member.getNickname());
        session.setAttribute("member_email", member.getEmail());
        return "redirect:/";
    }


    @GetMapping("/password")
    public String changePassword(Model model) {
        model.addAttribute("memberDto", new MemberDto());
        return "account/password";
    }

    @Transactional
    @PostMapping("/password")
    public String password(Principal principal,
                           @ModelAttribute @Valid MemberDto memberDto, BindingResult bindingResult) {
        Member member = memberService.getMemberByUsername(principal.getName());
        log.info("on password " + member.getUsername() + " request : " + memberDto.toString());
        if (member.isSocial()) return "redirect:/";

        if (!memberDtoValidator.validConfirm(memberDto, bindingResult)) return "account/password";

        memberDto.setComparePassword(member.getPassword());
        memberDtoValidator.passwordCheck(memberDto, bindingResult);
        if (bindingResult.hasErrors()) return "account/password";

        member.updatePassword(passwordEncoder.encode(memberDto.getConfirm()));
        return "redirect:/";
    }


    @GetMapping("/signOut")
    public String signOut(Model model) {
        model.addAttribute("memberDto", new MemberDto());
        return "account/signOut";
    }

    @Transactional
    @PostMapping("/signOut")
    public String signOut(Authentication authentication, Principal principal,
                          @ModelAttribute @Valid MemberDto memberDto, BindingResult bindingResult) {
        Member member = memberService.getMemberByUsername(principal.getName());
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