package com.portfolio.lamb.config;

import com.portfolio.lamb.domain.user.Member;
import com.portfolio.lamb.domain.user.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class CustomOidcUserService implements OAuth2UserService<OidcUserRequest, OidcUser> {
    private OidcUserService delegate = new OidcUserService();

    @Autowired
    private MemberService memberService;

    private static final List<String> clients = Arrays.asList("google");

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("loadUser :: Oidc for " + clients);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Assert.isTrue(clients.contains(registrationId),
                "registrationId \"" + registrationId + "\" is not valid");

        OidcUser socialUser = delegate.loadUser(userRequest);
        Map<String, Object> attributes = socialUser.getAttributes();
        String id = registrationId + "-" + attributes.getOrDefault("sub", null); // Id : 구글은 "sub"
        Assert.isTrue(id != null, "loadUser :: not found OidcUser id");
        log.info("loadUser :: OidcUser " + id + " loaded");

        Member member = memberService.getOrCreateSocialMember(id);

        log.info("loadUser :: return member's UserDetails");
        return new DefaultOidcUser(member.getAuthorities(), socialUser.getIdToken(),
                OidcUserInfo.builder().name(id).build(), "name");
    }
}
