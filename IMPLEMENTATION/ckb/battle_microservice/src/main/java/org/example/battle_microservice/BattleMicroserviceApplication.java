package org.example.battle_microservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BattleMicroserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BattleMicroserviceApplication.class, args);
    }

}
