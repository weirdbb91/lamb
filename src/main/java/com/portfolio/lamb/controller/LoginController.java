package com.portfolio.lamb.controller;


import com.portfolio.lamb.config.CustomClientRegistrationRepository;
import com.portfolio.lamb.domain.user.Member;
import com.portfolio.lamb.domain.user.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.*;

@SessionAttributes
@Controller
@RequestMapping("/account")
public class LoginController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private CustomClientRegistrationRepository clientRegistrationRepository;

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private HttpSession session;


    private static final List<String> clients = Arrays.asList("google", "facebook", "github", "naver", "kakao");
    private static final String authorizationRequestBaseUri = "/oauth2/authorization/";

    @GetMapping("/login")
    public String getLoginPage(Model model, Principal principal) {
        if (principal != null) return "redirect:/";

        // social login urls
        Map<String, String> urls = new LinkedHashMap<>();
        clients.forEach(registrationId -> urls.put(registrationId,
                authorizationRequestBaseUri + registrationId));
        model.addAttribute("urls", urls);

        return "account/login";
    }

    @GetMapping("/loginSuccess")
    public String loginSuccess() {
        return "redirect:/";
    }

//      TODO 얘는 복붙하다가 딸려온것 같다 확인 요망
    /*
    @GetMapping("/loginSuccess")
    public String getLoginInfo(Model model, OAuth2AuthenticationToken authentication) {
        OAuth2AuthorizedClient client = authorizedClientService
                .loadAuthorizedClient(
                        authentication.getAuthorizedClientRegistrationId(),
                        authentication.getName());

        String userInfoEndpointUri = client.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUri();

        // 유저 정보 요청
        if (!StringUtils.isEmpty(userInfoEndpointUri)) {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + client.getAccessToken()
                    .getTokenValue());
            HttpEntity entity = new HttpEntity("", headers);
            ResponseEntity<Map> response = restTemplate
                    .exchange(userInfoEndpointUri, HttpMethod.GET, entity, Map.class);
            Map userAttributes = response.getBody();

            System.out.println("User info request result : \n" + response.toString());

            System.out.println("Registration id : " + client.getClientRegistration().getRegistrationId());
            System.out.println("userAttributes.get(\"name\")" + userAttributes.get("name"));

            model.addAttribute("name", client.getClientRegistration().getRegistrationId() + userAttributes.get("name"));
        }

        return "/";
    }
    */

    @GetMapping("/loginFailure")
    public String loginFailure() {
        return "error";
    }


    @GetMapping("/register")
    public String register(Principal principal) {
        return principal != null ? "redirect:/" : "account/register";
    }

    @PostMapping("/register")
    public String register(Model model, Member member) {
        if (member.getUsername().length() > 30) {
            model.addAttribute("error", "name의 최대길이는 30입니다.");
            return "account/register";
        }
        if (memberService.existName(member.getUsername())) {
            model.addAttribute("error", "이미 사용중인 name입니다.");
            return "account/register";
        }
        try {
            member.setPassword(passwordEncoder.encode(member.getPassword()));
            memberService.save(member);
        } catch (Exception e) {
            model.addAttribute("error", e.toString());
            return "account/register";
        }
        return "redirect:/account/login";
    }
}