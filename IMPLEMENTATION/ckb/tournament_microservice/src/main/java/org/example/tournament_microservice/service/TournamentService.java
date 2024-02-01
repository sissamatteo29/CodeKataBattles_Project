package org.example.tournament_microservice.service;

import org.example.tournament_microservice.model.TournamentModel;
import org.example.tournament_microservice.repository.TournamentRankingRepository;
import org.example.tournament_microservice.repository.TournamentRepository;
import org.example.tournament_microservice.service.TournamentProducerService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TournamentService {


    private final TournamentRepository tournamentRepository;
    private final TournamentProducerService tournamentProducerService;
    private final TournamentRankingRepository tournamentRankingRepository;

    public TournamentService(TournamentRepository userRepository, TournamentProducerService tournamentProducerService,
                             TournamentRankingRepository tournamentRankingRepository) {

        this.tournamentRepository = userRepository;
        this.tournamentProducerService = tournamentProducerService;
        this.tournamentRankingRepository = tournamentRankingRepository;
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

    public Optional<TournamentModel> getTournamentByName(String tournamentName) {
        return tournamentRepository.findByName(tournamentName);
    }


    public void endTournament(String tournamentName) {
        System.out.println("Ending the tournament");

        // Retrieve the tournament by name from the repository
        Optional<TournamentModel> optionalTournament = tournamentRepository.findByName(tournamentName);
        LocalDate localDate = LocalDate.now();
        Date currentDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        if (optionalTournament.isPresent()) {
            TournamentModel tournament = optionalTournament.get();
            tournament.setEnded(true);
            tournament.setEndDate(currentDate);

            // Retrieve the list of subscribed users for the given tournament
            List<String> subscribedUsersIds = tournamentRankingRepository.findStudIdsByTourId(tournament.getName());

            String message = "Tournament " + tournamentName + " is ended! ";

            tournamentProducerService.produceTournamentEventEnding(message, subscribedUsersIds);
            tournamentRepository.save(tournament);
        } else {
            System.out.println("Tournament not found: " + tournamentName);
        }
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
