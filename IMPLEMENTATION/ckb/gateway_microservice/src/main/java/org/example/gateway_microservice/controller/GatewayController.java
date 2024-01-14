package org.example.gateway_microservice.controller;

import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
@RestController
public class GatewayController extends WebSecurityConfigurerAdapter {

    private final RestTemplate restTemplate;

    public GatewayController() {
        this.restTemplate = new RestTemplate();
    }
    @GetMapping("/user")
    public Map<String, Object> user(@AuthenticationPrincipal OAuth2User principal) {
        System.out.println("Principal: " + principal);
        String username = (String) principal.getAttribute("name");

        ResponseEntity<Boolean> responseEntity = restTemplate.getForEntity("http://localhost:8086/userExists?username=" + username, Boolean.class);
        boolean userExists = Boolean.TRUE.equals(responseEntity.getBody());

        Map<String, Object> response = new HashMap<>();
        response.put("username", username);

        if (!userExists) {
            response.put("showRoleSelection", true);
        }

        return response;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests(a -> a
                        .antMatchers("/", "/error", "/webjars/**").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )
                .oauth2Login();
        http
                .logout(l -> l
                        .logoutSuccessUrl("/").permitAll()
                );
        http
                // ... existing code here
                .csrf(c -> c
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                );
    }

}
