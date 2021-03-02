package com.portfolio.lamb.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
public class CustomClientRegistrationRepository implements ClientRegistrationRepository {

    @Autowired
    private Environment env;

    private static final List<String> clients = Arrays.asList("google", "facebook", "github", "naver", "kakao");
    private static final String CLIENT_PROPERTY_KEY = "spring.security.oauth2.client.registration.";

    public ClientRegistrationRepository customInMemoryClientRegistrationRepository() {
        List<ClientRegistration> registrations = clients.stream()
                .map(this::findByRegistrationId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return new InMemoryClientRegistrationRepository(registrations);
    }

    @Override
    public ClientRegistration findByRegistrationId(String registrationId) {
        String clientId = env.getProperty(CLIENT_PROPERTY_KEY + registrationId + ".client-id");
        String clientSecret = env.getProperty(CLIENT_PROPERTY_KEY + registrationId + ".client-secret");
        if (clientId == null || clientSecret == null) return null;

        return CustomOAuth2Provider.REGISTRATION.getBuilder(registrationId)
                .clientId(clientId).clientSecret(clientSecret).build();
    }
}
