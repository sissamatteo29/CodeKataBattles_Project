package org.example.tournament_microservice.service;

import org.example.tournament_microservice.model.TournamentModel;
import org.example.tournament_microservice.repository.TournamentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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

    public List<String> getTournaments() {
        return tournamentRepository.findAll()
                .stream()
                .map(TournamentModel::getName)
                .collect(Collectors.toList());
    }

    public List<String> getTournamentNamesByCreator(String creator) {
        System.out.println(tournamentRepository.findNamesByCreator(creator));
        return tournamentRepository.findNamesByCreator(creator);
    }



}
