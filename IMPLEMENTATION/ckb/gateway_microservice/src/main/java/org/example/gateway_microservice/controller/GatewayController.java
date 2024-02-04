package org.example.gateway_microservice.controller;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class GatewayController extends WebSecurityConfigurerAdapter {

    private final RestTemplate restTemplate;

    public GatewayController() {
        this.restTemplate = new RestTemplate();
    }

    // checks if user exists, returns http response
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
        // this is the name of the view that is returned
        return "index";
    }

    @GetMapping("/getNotifications")
    @ResponseBody
    public ResponseEntity<List<String>> getNotifications(@RequestParam String username) {
        String microserviceEndpoint = "http://localhost:8086/getNotifications?username=" + username;
        RestTemplate restTemplate = new RestTemplate();
        List<String> notifications = restTemplate.getForObject(microserviceEndpoint, List.class);
        System.out.println("Obtained a list: "+ notifications.toString());
        if (notifications != null) {
            return ResponseEntity.ok(notifications);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/profile")
    public String showProfile() {
        return "profile";
    }

    @GetMapping("/notification")
    public String showNotification() {
        return "notification";
    }

    @GetMapping("/create-tournament")
    public String tournamentForm() {
        return "create-tournament";
    }

    // the model here is a way to pass information from the controller to the view
    @GetMapping("/create-battle")
    public String battleForm(@RequestParam(required = false) String tournamentName, Model model) {
        model.addAttribute("tournamentName", tournamentName);
        return "create-battle";
    }
    // -Sara, per indirizzare alla view del subscribed tournament, ancora è una versione semplice
    @GetMapping("/subscribed-tournament-view")
    public String showSubscribedTournamentView(@RequestParam("name") String tournamentName, Model model) {
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
            return "subscribed-tournament-view";
        } else {
            System.out.println("getAllTournaments status KO");
            return null;
        }
    }

    @GetMapping("/isTournamentEnded")
    public ResponseEntity<Boolean> isTournamentEnded(@RequestParam String tournamentName) {
        // Make a REST request to the external service to check if the tournament has ended
        String externalServiceUrl = "http://localhost:8085/isTournamentEnded?tournamentName=" + tournamentName;

        RestTemplate restTemplate = new RestTemplate();
        Boolean tournamentEnded = restTemplate.getForObject(externalServiceUrl, Boolean.class);

        return ResponseEntity.ok(tournamentEnded);
    }

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

    @PostMapping("/end-tournament")
    public String endTournament(@RequestParam String tournamentName) {
        System.out.println("Tournament name for end-tournament: " + tournamentName);
        String url = "http://localhost:8085/endTournament?tournament=" + tournamentName;
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, null, String.class);
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            System.out.println("Ended tournament successful");
        } else {
            System.out.println("Tournament cannot be ended");
        }
        return "profile";
    }


    @PostMapping("/subscribe")
    public String subscribe(@RequestParam String tournamentName, @RequestParam String username) {
        System.out.println("Received form data - Tournament: " + tournamentName);

        String url = "http://localhost:8085/addStudent?"
                + "tourId=" + tournamentName + "&studId=" + username;

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, null, String.class);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            System.out.println("Subscription successful");
        } else {
            System.out.println("Subscription error");
        }

        return "profile";
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
    public List<String> getAllTournamentSubscribed(@RequestParam String name) {
        String createUrl = "http://localhost:8085/getAllSubscription?name=" + name;
        System.out.println("Logged-in name: " + name);
        ResponseEntity<List<String>> responseEntity = restTemplate.exchange(
                createUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<String>>() {}
        );
        System.out.println(responseEntity.getBody());

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            List<String> tournamentNames = responseEntity.getBody();
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
    @GetMapping("/battle-detail")
    public String showBattleDetail(
            @RequestParam("tournament_name") String tournamentName,
            @RequestParam("battle_name") String battleName, Model model) {
        model.addAttribute("tournamentName", tournamentName);
        model.addAttribute("battleName", battleName);
        System.out.println("Get Mapping battle-detail");
        return "battle-detail";

    }

    @GetMapping("/battle-home")
    public String showBattleHome(
            @RequestParam("tournament_name") String tournamentName,
            @RequestParam("battle_name") String battleName, Model model) {
        model.addAttribute("tournamentName", tournamentName);
        model.addAttribute("battleName", battleName);
        System.out.println("Get Mapping battle-home");
        return "battle-home";

    }

    @GetMapping("/getBattlesByTour")
    @ResponseBody
    public List<String> getBattlesByTournament(@RequestParam String tournamentName, Model model) {
        String battleMicroserviceUrl = "http://localhost:8083";
        String endpoint = "/getBattlesByTour";

        String createUrl = battleMicroserviceUrl + endpoint + "?tournamentName=" + tournamentName;

        System.out.println("Tournament name: " + tournamentName);

        ResponseEntity<List<String>> responseEntity = restTemplate.exchange(
                createUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<String>>() {}
        );

        System.out.println(responseEntity.getBody());

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            List<String> battleNames = responseEntity.getBody();
            model.addAttribute("battleNames", battleNames);
            System.out.println("getBattlesByTournament status OK");
            return battleNames;
        } else {
            System.out.println("getBattlesByTournament status KO");
            return null;
        }
    }


    @GetMapping("/getBattlesByTourAndStud")
    @ResponseBody
    public List<String> getBattlesByTourAndStud(
            @RequestParam String tour,
            @RequestParam String stud) {
        String url = "http://localhost:8083" + "/getBattlesByTourAndStud?tour=" + tour + "&stud=" + stud;

        ResponseEntity<List<String>> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<String>>() {}
        );

        if (responseEntity != null && responseEntity.getStatusCode() == HttpStatus.OK) {
            List<String> battles = responseEntity.getBody();
            System.out.println("getBattlesByTourAndStud status OK");
            return battles;
        } else {
            System.out.println("getBattlesByTourAndStud status KO");
            return null;
        }
    }

    @PostMapping("/addStudent")
    public ResponseEntity<String> addStudent(
            @RequestParam String tour,
            @RequestParam String battle,
            @RequestParam String stud,
            @RequestParam String team) {
        String url = "http://localhost:8083" + "/addStudent?tour=" + tour + "&battle=" + battle +
                "&stud=" + stud + "&team=" + team;
        return restTemplate.postForEntity(url, null, String.class);
    }
    // fine parte nuova da testare

    @GetMapping("/all-scores-by-battle")
    @ResponseBody
    public List<Object[]> getAllScores(@RequestParam String tour, @RequestParam String battle) {
        String createUrl = "http://localhost:8083/distinctScoresAndTeamNames?tour=" + tour + "&battle=" + battle;
        ResponseEntity<List<Object[]>> responseEntity = (ResponseEntity<List<Object[]>>) restTemplate.exchange(
                createUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Object[]>>() {}
        );

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            List<Object[]> scores = responseEntity.getBody();
            System.out.println("request scores status OK");
            return scores;
        } else {
            System.out.println("request scores status KO");
            return null;
        }
    }

    @GetMapping("/all-scores-by-tour")
    @ResponseBody
    public List<Object[]> getAllTourScores(@RequestParam String tour) {
        String createUrl = "http://localhost:8085/studScores?tour=" + tour;
        ResponseEntity<List<Object[]>> responseEntity = (ResponseEntity<List<Object[]>>) restTemplate.exchange(
                createUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Object[]>>() {}
        );

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            List<Object[]> scores = responseEntity.getBody();
            System.out.println("request scores status OK");
            return scores;
        } else {
            System.out.println("request scores status KO");
            return null;
        }
    }

    @GetMapping("/score")
    public ResponseEntity<Integer> getScoreByTourBattleStud(
            @RequestParam String tour,
            @RequestParam String battle,
            @RequestParam String stud) {

        String serviceUrl = "http://localhost:8083/score";

        ResponseEntity<Integer> responseEntity = restTemplate.getForEntity(
                serviceUrl + "?tour={tour}&battle={battle}&stud={stud}",
                Integer.class, tour, battle, stud);

        return ResponseEntity.status(responseEntity.getStatusCode())
                .headers(responseEntity.getHeaders())
                .body(responseEntity.getBody());
    }
    @GetMapping("/student-team")
    public ResponseEntity<String> getTeamName(
            @RequestParam("tour") String tour,
            @RequestParam("battle") String battle,
            @RequestParam("stud") String stud) {

        String serviceUrl = "http://localhost:8083/teamName";

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(
                serviceUrl + "?tour={tour}&battle={battle}&stud={stud}",
                String.class, tour, battle, stud);

        return ResponseEntity.status(responseEntity.getStatusCode())
                .headers(responseEntity.getHeaders())
                .body(responseEntity.getBody());
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

    @GetMapping("/newSubmission/{username}/{repository}")
    public void redirectRequestToGitHubMicroservice(@PathVariable String username, @PathVariable String repository,
                                                    @RequestParam String tour, @RequestParam String battle, @RequestParam String teamName) throws URISyntaxException {

        /* Send the request as it is to the GitHub integration microservice */
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest sendGitHub = HttpRequest.newBuilder()
                .GET()
                .uri(new URI(String.format("http://localhost:8090/newSubmission/%s/%s?tour=%s&battle=%s&teamName=%s", username, repository, tour, battle, teamName)))
                .build();
        client.sendAsync(sendGitHub, HttpResponse.BodyHandlers.discarding());

    }

}
