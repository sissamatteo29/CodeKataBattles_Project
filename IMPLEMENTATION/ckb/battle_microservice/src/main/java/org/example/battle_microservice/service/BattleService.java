package org.example.battle_microservice.service;

import org.example.battle_microservice.repository.BattleRepository;
import org.springframework.stereotype.Service;

@Service
public class BattleService {
    private final BattleRepository battleRepository;
    public BattleService(BattleRepository userRepository) {
        this.battleRepository = userRepository;
    }

}
