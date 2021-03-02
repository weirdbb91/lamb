package com.portfolio.lamb.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import javax.sql.DataSource;

@Slf4j
@Configuration
@EnableWebSecurity
public class LambSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private CustomClientRegistrationRepository clientRegistrationRepository;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private CustomOAuth2UserService oAuth2UserService;

    @Autowired
    private CustomOidcUserService oidcUserService;


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
                    .clientRegistrationRepository(clientRegistrationRepository.customInMemoryClientRegistrationRepository())
                    .loginPage("/account/login")
                    .failureUrl("/error")
                    .userInfoEndpoint()
                        .userService(oAuth2UserService)
                        .oidcUserService(oidcUserService) // only google uses OidcUserService
        ;
        //@formatter:on
    }

    public PersistentTokenRepository tokenRepository() {
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        return jdbcTokenRepository;
    }



}
