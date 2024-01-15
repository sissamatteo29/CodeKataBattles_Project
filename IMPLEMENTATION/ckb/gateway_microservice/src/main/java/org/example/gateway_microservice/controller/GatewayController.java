package org.example.gateway_microservice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

import java.util.HashMap;
import java.util.Map;

@Controller
public class GatewayController extends WebSecurityConfigurerAdapter {

    private final RestTemplate restTemplate;

    public GatewayController() {
        this.restTemplate = new RestTemplate();
    }

    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> user(@AuthenticationPrincipal OAuth2User principal) {
        String username = (String) principal.getAttribute("login");
        ResponseEntity<Boolean> responseEntity = restTemplate.getForEntity("http://localhost:8086/checkUser?username=" + username, Boolean.class);
        Boolean userCheckResult = responseEntity.getBody();

        Map<String, Object> response = new HashMap<>();
        response.put("name", username);
        response.put("userCheckResult", userCheckResult);

        return ResponseEntity.ok(response);
    }
    @PostMapping("/selectRole")
    public String saveRole(@AuthenticationPrincipal OAuth2User principal, @RequestParam int role) {
        String username = (String) principal.getAttribute("login");
        System.out.println(username);
        System.out.println(role);
        ResponseEntity<Boolean> responseEntity2 = restTemplate.getForEntity("http://localhost:8086/checkUser?username=" + username, Boolean.class);
        if (Boolean.FALSE.equals(responseEntity2.getBody())) {
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(
                    "http://localhost:8086/saveUser?username=" + username + "&role=" + role,
                    null,
                    String.class
            );
            System.out.println("User saved");
        }
        return "index";
    }

    @GetMapping("/profile")
    public String profile() {
        return "profile";
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
