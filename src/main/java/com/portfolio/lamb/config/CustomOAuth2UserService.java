package com.portfolio.lamb.config;

import com.portfolio.lamb.domain.user.Role;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User>{
    private DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

    // 구글은 "CustomOidcUserService"로
    private static final List<String> clients = Arrays.asList("facebook", "github", "naver", "kakao");

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("---------------OAuth2UserService.customLoadUser()---------------");
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        if (!clients.contains(registrationId)) throw new IllegalArgumentException("해당 \"registrationId\"을 찾을 수 없습니다.");

        OAuth2User socialUser = delegate.loadUser(userRequest);

        Map<String, Object> attributes = socialUser.getAttributes();
        // 네이버는 모든 정보들이 "response"에 담겨져 있음
        if (registrationId.equals("naver")) attributes = (Map<String, Object>) attributes.getOrDefault("response", attributes);
//        log.info("Attributes : " + new JSONObject(attributes).toString(4));
        Map<String, Object> att = new HashMap<>();
        att.put("registrationId", registrationId);
        log.info("RegistrationId : " + registrationId);

        att.put("id", registrationId + "-" + attributes.getOrDefault("id", "null"));
        log.info("Id : " + (att.getOrDefault("id", "null")));

        // Email : 카카오만 "id"외의 정보들이 "kakao_account"로 한겹 더 감싸져 있음
        if (registrationId.equals("kakao")) attributes = (Map<String, Object>) attributes.getOrDefault("kakao_account", attributes);
        att.put("email", attributes.getOrDefault("email", "null"));
        log.info("email : " + att.get("email"));;
        log.info("----------------------------------------------------------------");

        Set<GrantedAuthority> mappedAuthorities = new HashSet<>();
        mappedAuthorities.add(new SimpleGrantedAuthority(Role.USER.getValue()));  // TODO get user Authority

        // TODO
        // 1) Fetch the authority information from the protected resource using accessToken
        // 2) Map the authority information to one or more GrantedAuthority's and add it to mappedAuthorities

        // 3) Create a copy of oidcUser but use the mappedAuthorities instead

        // TODO 사용자 회원가입 또는 연동
        // 세션에 사용자 정보를 저장
//            httpSession.setAttribute("user", attributes);

        return new DefaultOAuth2User(mappedAuthorities, att, "id");
    }
}
