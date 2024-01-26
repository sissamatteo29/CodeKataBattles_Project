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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class GatewayController extends WebSecurityConfigurerAdapter {

    private final RestTemplate restTemplate;

    public GatewayController() {
        this.restTemplate = new RestTemplate();
    }

    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> user(@AuthenticationPrincipal OAuth2User principal) {
        String username = (String) principal.getAttribute("login");
        ResponseEntity<Integer> roleResponseEntity = restTemplate.getForEntity("http://localhost:8086/getUserRole?username=" + username, Integer.class);
        Integer userRole = roleResponseEntity.getBody();

        ResponseEntity<Boolean> responseEntity = restTemplate.getForEntity("http://localhost:8086/checkUser?username=" + username, Boolean.class);
        Boolean userCheckResult = responseEntity.getBody();

        Map<String, Object> response = new HashMap<>();
        response.put("name", username);
        response.put("userCheckResult", userCheckResult);
        response.put("role", userRole);
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

    private String encodeToBase64(byte[] file) throws IOException {
        return new String(Base64.getUrlEncoder().encode(file));
    }

    @PostMapping("/createBattle")
    public String createBattle(@RequestParam String name,
                               @RequestParam String tournament,
                               @RequestParam("automation_build_script") MultipartFile automationBuildScript,
                               @RequestParam("code_test") MultipartFile code_test,
                               @RequestParam("code") MultipartFile code,
                               @RequestParam int max_team_size,
                               @RequestParam int min_team_size,
                               @RequestParam String repository_link,
                               @RequestParam Boolean manual_evaluation,
                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date reg_deadline,
                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date sub_deadline,
                               @RequestParam String creator,
                               Model model) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDateReg = dateFormat.format(reg_deadline);
        String formattedDateSub = dateFormat.format(sub_deadline);

        try {
            // Convert MultipartFile to byte array
            byte[] automationBuildScriptBytes = automationBuildScript.getBytes();
            byte[] codeTestBytes = code_test.getBytes();
            byte[] codeBytes = code.getBytes();

            String encodedABS = Base64.getUrlEncoder().encodeToString(automationBuildScriptBytes);
            String encodedCT = Base64.getUrlEncoder().encodeToString(codeTestBytes);
            String encodedC = Base64.getUrlEncoder().encodeToString(codeBytes);

            System.out.println("Received form data: name=" + name
                    + "&tournament=" + tournament + "&automation_build_script=" + encodedABS.length() +
                    "&code_test=" + encodedCT.length() + "&code=" + encodedC.length() + "&max_team_size=" + max_team_size + "&min_team_size=" +
                    min_team_size + "&repository_link=" + repository_link + "&manual_evaluation=" + manual_evaluation +
                    "&reg_deadline=" + formattedDateReg + "&sub_deadline=" + formattedDateSub + "creator: " + creator);
            String createNewBattleUrl = "http://localhost:8083/createNewBattle?"
                    + "name=" + name
                    + "&tournament=" + tournament + "&automation_build_script=" +
                    encodedABS +
                    "&code_test=" + encodedCT +
                    "&code=" + encodedC +
                    "&max_team_size=" + max_team_size + "&min_team_size=" +
                    min_team_size + "&repository_link=" + repository_link + "&manual_evaluation=" + manual_evaluation +
                    "&reg_deadline=" + formattedDateReg + "&sub_deadline=" + formattedDateSub + "&creator=" + creator;
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(createNewBattleUrl, null, String.class);
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                System.out.println("Battle created successfully");
            } else {
                System.out.println("Error in creating battle");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "profile";
    }

    @GetMapping("/all-tournaments-abs")
    @ResponseBody
    public List<String> getAllTournamentsAbs(Model model) {
        String createUrl = "http://localhost:8085/getAllTournamentsAbs?";
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
            System.out.println("getAllTournamentsAbs status OK");
            return tournamentNames;
        } else {
            System.out.println("getAllTournamentsAbs status KO");
            return null;
        }
    }

    @GetMapping("/all-tournaments-subscribed")
    @ResponseBody
    public List<String> getAllTournamentSubscribed(@RequestParam String name, Model model) {
        String createUrl = "http://localhost:8086/getAllSubscription?name=" + name;
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
            System.out.println("getAllTournamentsSubscriptions status OK");
            return tournamentNames;
        } else {
            System.out.println("getAllTournamentsSubscriptions status KO");
            return null;
        }
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
