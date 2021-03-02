package com.portfolio.lamb.config;

import com.portfolio.lamb.domain.user.Role;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class CustomOidcUserService implements OAuth2UserService<OidcUserRequest, OidcUser> {
    private OidcUserService delegate = new OidcUserService();

    // 구글외 나머지는 "CustomOAuth2UserService"로
    private static final List<String> clients = Arrays.asList("google");

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("----------------OidcUserService.customLoadUser()----------------");
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        if (!clients.contains(registrationId)) throw new IllegalArgumentException("해당 \"registrationId\"을 찾을 수 없습니다.");

        OidcUser oidcUser = delegate.loadUser(userRequest);

        Map<String, Object> attributes = oidcUser.getAttributes();
        String id = registrationId + "-" + attributes.getOrDefault("sub", "null");
//        log.info("Attributes : " + new JSONObject(attributes).toString(4));
        log.info("RegistrationId : " + registrationId);
        log.info("Id : " + id);
        log.info("email : " + attributes.getOrDefault("email", "null"));
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

        // Id : 구글만 "sub"

        // "name"값에 "RegistrationId"를 붙인 "id"를 저장하고 "nameAttributeKey"를 "name"으로 바꿔서 지정함
        return new DefaultOidcUser(mappedAuthorities, oidcUser.getIdToken(),
                OidcUserInfo.builder().name(id).build(), "name");
    }
}
