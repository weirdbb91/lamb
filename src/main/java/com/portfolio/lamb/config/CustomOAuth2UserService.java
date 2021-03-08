package com.portfolio.lamb.config;

import com.portfolio.lamb.domain.user.Member;
import com.portfolio.lamb.domain.user.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

    @Autowired
    private MemberService memberService;

    private static final List<String> clients = Arrays.asList("facebook", "github", "naver", "kakao");

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("loadUser :: OAuth2 for " + clients);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Assert.isTrue(clients.contains(registrationId),
                "registrationId \"" + registrationId + "\" is not valid");

        OAuth2User socialUser = delegate.loadUser(userRequest);
        Map<String, Object> attributes = socialUser.getAttributes();
        if (registrationId.equals("naver")) {
            attributes = (Map<String, Object>) attributes.getOrDefault("response", attributes);
        }
        String id = registrationId + "-" + attributes.getOrDefault("id", null);
        Assert.isTrue(StringUtils.hasLength(id), "not found OAuth2User id");
        log.info("loadUser :: OAuth2 " + id + " loaded");

        Member member = memberService.getOrCreateSocialMember(id);

        log.info("loadUser :: return member's UserDetails");
        return new DefaultOAuth2User(member.getAuthorities(),
                Collections.singletonMap("id", id), "id");
    }
}
