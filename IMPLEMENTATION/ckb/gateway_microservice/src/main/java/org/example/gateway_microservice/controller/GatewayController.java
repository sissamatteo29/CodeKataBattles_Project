package org.example.gateway_microservice.controller;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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

    @GetMapping("/create-tournament")
    public String tournamentForm() {
        return "create-tournament";
    }

    @GetMapping("/create-battle")
    public String battleForm(@RequestParam(required = false) String tournamentName, Model model) {
        model.addAttribute("tournamentName", tournamentName);
        return "create-battle"; }

    @PostMapping("/createTournament")
    public String createTournament(@RequestParam String name, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date subscriptionDate, @RequestParam String creator) {
        System.out.println("Received form data - Name: " + name + ", Submission Date: " + subscriptionDate + ", Name: " + creator);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = dateFormat.format(subscriptionDate);
        System.out.println("Received form data - Name: " + name + ", Submission Date: " + formattedDate + ", Name: " + creator);
        String createNewTournamentUrl = "http://localhost:8085/createNewTournament?"
                + "name=" + name
                + "&subscriptionDate=" + formattedDate
                + "&creator=" + creator;
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(createNewTournamentUrl, null, String.class);
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            System.out.println("Tournament created successfully");
        } else {
            System.out.println("Error in creating tournament")  ;
        }
        return "profile";
    }

    @PostMapping("/createBattle")
    public String createBattle(@RequestParam String name, @RequestParam String tournament) {
        System.out.println("Received form data - Name: " + name + ", Tournament: " + tournament);
        String createNewBattleUrl = "http://localhost:8083/createNewBattle?"
            + "name=" + name
            + "&tournament=" + tournament;
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(createNewBattleUrl, null, String.class);
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            System.out.println("Battle created successfully");
        } else {
            System.out.println("Error in creating battle")  ;
        }
        return "profile";
    }


    @GetMapping("/all-tournaments")
    @ResponseBody
    public List<String> getAllTournaments(@RequestParam String name, Model model) {
        String createUrl = "http://localhost:8085/getAllTournaments?name=" + name;
        System.out.println("Logged-in username: " + name);
        ResponseEntity<List<String>> responseEntity = restTemplate.exchange(
                createUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<String>>() {}
        );
        System.out.println(responseEntity.getBody());

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            List<String> tournamentNames = responseEntity.getBody();
            model.addAttribute("tournamentNames", tournamentNames);
            System.out.println("getAllTournaments status OK");
            return tournamentNames;
        } else {
            System.out.println("getAllTournaments status KO");
            return null;
        }
    }

    @GetMapping("/tournament-detail")
    public String showTournamentDetail(@RequestParam("name") String tournamentName, Model model) {
        String createUrl = "http://localhost:8083/getAllBattles?tournamentName=" + tournamentName;
        System.out.println("Tournament name: " + tournamentName);
        ResponseEntity<List<String>> responseEntity = restTemplate.exchange(
                createUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<String>>() {}
        );
        System.out.println(responseEntity.getBody());


        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            List<String> battleNames = responseEntity.getBody();
            model.addAttribute("tournamentName", tournamentName);
            model.addAttribute("battleNames", battleNames);
            System.out.println("getAllBattles status OK");
            return "tournament-detail";
        } else {
            System.out.println("getAllTournaments status KO");
            return null;
        }
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
