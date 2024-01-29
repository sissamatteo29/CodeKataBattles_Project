package org.example.tournament_microservice.service;

import org.example.tournament_microservice.model.TournamentModel;
import org.example.tournament_microservice.repository.TournamentRepository;
import org.example.tournament_microservice.service.TournamentProducerService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TournamentService {


    private final TournamentRepository tournamentRepository;
    private final TournamentProducerService tournamentProducerService;

    public TournamentService(TournamentRepository userRepository, TournamentProducerService tournamentProducerService) {

        this.tournamentRepository = userRepository;
        this.tournamentProducerService = tournamentProducerService;
    }

    public void saveTournament(TournamentModel tournament) {
        System.out.println("Saving the tournament");
        //Generating Kafka message
        String name = tournament.getName();
        String message = "New tournament created! Check it: " + name;
        System.out.println("Creating the message (Kafka)");
        tournamentProducerService.produceTournamentEvent(message);
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
