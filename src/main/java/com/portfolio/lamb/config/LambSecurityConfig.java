package com.portfolio.lamb.config;

import com.portfolio.lamb.domain.OAuthAttributes;
import com.portfolio.lamb.domain.user.Role;
import com.portfolio.lamb.domain.user.UserDetailsServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import javax.sql.DataSource;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Configuration
@EnableWebSecurity
@PropertySource("classpath:application.properties")
public class LambSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private Environment env;

    @Autowired
    private DataSource dataSource;


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //@formatter:off
        http
                .csrf()
                    .disable()
                .authorizeRequests()
                    .antMatchers("/",
                            "/account/login",
                            "/account/register",
                            "/oauth2/**",
                            "/css/**",
                            "/images/**")
                        .permitAll()
                    .anyRequest()
                        .authenticated()
                .and()
                .formLogin()
                    .loginPage("/account/login")
                    .defaultSuccessUrl("/", true)
                        .permitAll()
                .and()
                .logout()
                    .invalidateHttpSession(true)
                    .clearAuthentication(true)
                    .logoutSuccessUrl("/account/login?logout")
                    .deleteCookies("JSESSIONID")
                        .permitAll()
                .and()
                    .csrf()
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .and()
                .rememberMe()
                    .userDetailsService(userDetailsService)
                    .tokenRepository(this.tokenRepository())
                .and()
                .oauth2Login()
                    .clientRegistrationRepository(this.clientRegistrationRepository())
                    .loginPage("/account/login")
                    .failureUrl("/error")
                    .userInfoEndpoint()
                        .userService(this.oAuth2UserService())
                        .oidcUserService(this.oidcUserService());
        //@formatter:on
    }

    private OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService() {
        final OAuth2UserService delegate = new DefaultOAuth2UserService();

        return (userRequest) -> {
            String registrationId = userRequest.getClientRegistration().getRegistrationId();
            String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

            // Delegate to the default implementation for loading a user
            OAuth2User oAuth2User = delegate.loadUser(userRequest);
            log.info("--------------CustomOAuth2UserService.loadUser()---------------");
            log.info("------------------------" + registrationId + " userRequest-----------------------");
            log.info(new JSONObject(userRequest).toString(4));
            log.info("--------------------" + registrationId + " User---------------------");
            log.info(new JSONObject(oAuth2User).toString(4));
            log.info("-----------------------------end------------------------------");

            OAuth2AccessToken accessToken = userRequest.getAccessToken();
            OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

            log.info("name : " + attributes.getUsername());
            log.info("email : " + attributes.getEmail());
            log.info("picture : " + attributes.getPicture());
            log.info("----------------------------------------------------------------");

            Set<GrantedAuthority> mappedAuthorities = new HashSet<>();

            // TODO
            // 1) Fetch the authority information from the protected resource using accessToken
            // 2) Map the authority information to one or more GrantedAuthority's and add it to mappedAuthorities

            // 3) Create a copy of oidcUser but use the mappedAuthorities instead

            // TODO 사용자 회원가입 또는 연동
            // 세션에 사용자 정보를 저장
//            httpSession.setAttribute("user", attributes);

            oAuth2User = new DefaultOAuth2User(
                    Collections.singleton(new SimpleGrantedAuthority(Role.USER.getValue())),
                    attributes.getAttributes(),
                    attributes.getNameAttributeKey());

            return oAuth2User;
        };
    }
    private OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {
        final OidcUserService delegate = new OidcUserService();

        return (userRequest) -> {
            String registrationId = userRequest.getClientRegistration().getRegistrationId();

            // Delegate to the default implementation for loading a user
            OidcUser oidcUser = delegate.loadUser(userRequest);
            log.info("------------------OidcUserService.loadUser()------------------");
            log.info("------------------------" + registrationId + " userRequest-----------------------");
            log.info(new JSONObject(userRequest).toString(4));
            log.info("--------------------" + registrationId + " User---------------------");
            log.info(new JSONObject(oidcUser).toString(4));
            log.info("-----------------------------end------------------------------");

            OAuth2AccessToken accessToken = userRequest.getAccessToken();
            Set<GrantedAuthority> mappedAuthorities = new HashSet<>();

            // TODO
            // 1) Fetch the authority information from the protected resource using accessToken
            // 2) Map the authority information to one or more GrantedAuthority's and add it to mappedAuthorities

            // 3) Create a copy of oidcUser but use the mappedAuthorities instead
            oidcUser = new DefaultOidcUser(mappedAuthorities, oidcUser.getIdToken(), oidcUser.getUserInfo());

            return oidcUser;
        };
    }

    public PersistentTokenRepository tokenRepository() {
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        return jdbcTokenRepository;
    }


    private static final List<String> clients = Arrays.asList("google", "facebook", "github", "naver", "kakao");
    private static final String CLIENT_PROPERTY_KEY = "spring.security.oauth2.client.registration.";

    public ClientRegistrationRepository clientRegistrationRepository() {
        List<ClientRegistration> registrations = clients.stream()
                .map(this::getRegistration)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return new InMemoryClientRegistrationRepository(registrations);
    }

    private ClientRegistration getRegistration(String client) {
        String clientId = env.getProperty(CLIENT_PROPERTY_KEY + client + ".client-id");
        String clientSecret = env.getProperty(CLIENT_PROPERTY_KEY + client + ".client-secret");

        if (clientId == null || clientSecret == null) return null;

        if (client.equals("google")) {
            return CommonOAuth2Provider.GOOGLE.getBuilder(client)
                    .clientId(clientId).clientSecret(clientSecret).build();
        }
        if (client.equals("facebook")) {
            return CommonOAuth2Provider.FACEBOOK.getBuilder(client)
                    .clientId(clientId).clientSecret(clientSecret).build();
        }
        if (client.equals("github")) {
            return CommonOAuth2Provider.GITHUB.getBuilder(client)
                    .clientId(clientId).clientSecret(clientSecret).build();
        }

//        CustomOAuth2Provider 구현
        if (client.equals("naver")) {
            return CustomOAuth2Provider.NAVER.getBuilder(client)
                    .clientId(clientId).clientSecret(clientSecret).build();
        }
        if (client.equals("kakao")) {
            return CustomOAuth2Provider.KAKAO.getBuilder(client)
                    .clientId(clientId).clientSecret(clientSecret).build();
        }
        return null;
    }
}
