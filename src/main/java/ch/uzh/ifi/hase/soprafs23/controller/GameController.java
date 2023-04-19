package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.constant.Role;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import ch.uzh.ifi.hase.soprafs23.service.LobbyService;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import ch.uzh.ifi.hase.soprafs23.stomp.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class GameController {

    private final Logger logger = LoggerFactory.getLogger(GameController.class);

    private final UserService userService;
    private final LobbyService lobbyService;
    private final GameService gameService;

    GameController(UserService userService, GameService gameService, LobbyService lobbyService){
        this.userService = userService;
        this.gameService = gameService;
        this.lobbyService = lobbyService;
    }

    @GetMapping("/game/{lobbyId}/roleForUser/{playerId}") //probably should rename lobbyId to gameId, probably change Integer to int
    public ResponseEntity<Role> getRole(@PathVariable("lobbyId") Integer lobbyId, @PathVariable("playerId") Long playerId){
        //TODO: check that game has been started
        //TODO: check token
        return ResponseEntity.ok(lobbyService.getRole(lobbyId, playerId));
    }

    @GetMapping("/game/{lobbyId}/roundnr") //probably should rename lobbyId to gameId
    public ResponseEntity<RoundNr> getRound(@PathVariable("lobbyId") Integer lobbyId) {
        //TODO: check that game has been started
        //TODO: check token
        int currentRound = gameService.getCurrentRoundNr(lobbyId);
        int totalRounds = gameService.getTotalNrRounds(lobbyId);
        return ResponseEntity.ok(new RoundNr(currentRound, totalRounds));
    }
}
