package com.portfolio.lamb.config;

import com.portfolio.lamb.domain.OAuthAttributes;
import com.portfolio.lamb.domain.user.Role;
import com.portfolio.lamb.domain.user.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;

    private final HttpSession httpSession;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        OAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);
        log.info("-------------------CustomOAuth2UserService--------------------");
//        log.info("------------------------" + registrationId + " Request-------------------------");
//        log.info(new JSONObject(userRequest).toString(4));
//        log.info("--------------------" + registrationId + " User Attribute---------------------");
//        log.info(new JSONObject(oAuth2User).toString(4));
//        log.info("-----------------------------end------------------------------");


        // OAuth2UserService를 통해 가져온 OAuth2User의 attribute를 담을 클래스
        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        log.info("name : " + attributes.getUsername());
        log.info("email : " + attributes.getEmail());
        log.info("picture : " + attributes.getPicture());
        log.info("----------------------------------------------------------------");

        // TODO 사용자 회원가입 또는 연동

        // 세션에 사용자 정보를 저장
        httpSession.setAttribute("user", attributes);


        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(Role.USER.getValue())),
                attributes.getAttributes(),
                attributes.getNameAttributeKey()
        );
    }
}