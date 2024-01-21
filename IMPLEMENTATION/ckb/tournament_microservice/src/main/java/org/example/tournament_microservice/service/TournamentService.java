package org.example.tournament_microservice.service;

import org.checkerframework.common.value.qual.EnsuresMinLenIf;
import org.example.tournament_microservice.model.TournamentModel;
import org.example.tournament_microservice.repository.TournamentRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TournamentService {
    private final TournamentRepository tournamentRepository;

    public TournamentService(TournamentRepository userRepository) {
        this.tournamentRepository = userRepository;
    }

    public void saveTournament(TournamentModel tournament) {
        System.out.println("Saving the tournament");
        tournamentRepository.save(tournament);
    }

    public List<String> getTournamentNamesByCreator(String creator) {
        System.out.println(tournamentRepository.findNamesByCreator(creator));
        return tournamentRepository.findNamesByCreator(creator);
    }

}
