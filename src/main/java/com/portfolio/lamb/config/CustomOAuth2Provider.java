package com.portfolio.lamb.config;

import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

public enum CustomOAuth2Provider {
    REGISTRATION {
        public ClientRegistration.Builder getBuilder(String registrationId) {
            switch (registrationId) {
                case "google":
                    return CommonOAuth2Provider.GOOGLE.getBuilder(registrationId);
                case "github":
                    return CommonOAuth2Provider.GITHUB.getBuilder(registrationId);
                case "facebook":
                    return CommonOAuth2Provider.FACEBOOK.getBuilder(registrationId);
                case "naver":
                    return CustomOAuth2Provider.NAVER.getBuilder(registrationId);
                case "kakao":
                    return CustomOAuth2Provider.KAKAO.getBuilder(registrationId);
            }
            throw new IllegalArgumentException("registrationId is not on the list");
        }
    },
    NAVER {
        @Override
        public ClientRegistration.Builder getBuilder(String registrationId) {
            return getBuilder(registrationId, ClientAuthenticationMethod.POST, DEFAULT_REDIRECT_URL)
                    .scope("profile")
                    .authorizationUri("https://nid.naver.com/oauth2.0/authorize")
                    .tokenUri("https://nid.naver.com/oauth2.0/token")
                    .userInfoUri("https://openapi.naver.com/v1/nid/me")
                    .userNameAttributeName("response")
                    .clientName("Naver");
        }
    },
    KAKAO {
        @Override
        public ClientRegistration.Builder getBuilder(String registrationId) {
            return getBuilder(registrationId, ClientAuthenticationMethod.POST, DEFAULT_REDIRECT_URL)
                    .scope("profile")
                    .authorizationUri("https://kauth.kakao.com/oauth/authorize")
                    .tokenUri("https://kauth.kakao.com/oauth/token")
                    .userInfoUri("https://kapi.kakao.com/v2/user/me")
                    .userNameAttributeName("id")
                    .clientName("Kakao");
        }
    };

    private static final String DEFAULT_REDIRECT_URL = "{baseUrl}/login/oauth2/code/{registrationId}";

    protected final ClientRegistration.Builder getBuilder(
            String registrationId, ClientAuthenticationMethod method, String redirectUri) {

        return ClientRegistration.withRegistrationId(registrationId)
                .clientAuthenticationMethod(method)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri(redirectUri);
    }

    public abstract ClientRegistration.Builder getBuilder(String registrationId);
}
