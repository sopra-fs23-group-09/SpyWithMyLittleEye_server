package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.constant.Role;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.rest.dto.RoundGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import ch.uzh.ifi.hase.soprafs23.service.LobbyService;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import ch.uzh.ifi.hase.soprafs23.stomp.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class GameController {

    private final UserService userService;
    private final LobbyService lobbyService;
    private final GameService gameService;

    GameController(UserService userService, GameService gameService, LobbyService lobbyService){
        this.userService = userService;
        this.gameService = gameService;
        this.lobbyService = lobbyService;
    }

    @GetMapping("/games/{gameId}/roleForUser/{playerId}")
    public ResponseEntity<Role> getRole(@RequestHeader(value = "Token", defaultValue = "null") String token, @PathVariable("gameId") int gameId, @PathVariable("playerId") Long playerId){
        //user authentication over token in header
        userService.checkToken(token);


        return ResponseEntity.ok(gameService.getRole(gameId, playerId));
    }

    @GetMapping("/games/{gameId}/roundnr")
    public ResponseEntity<RoundNr> getRound(@RequestHeader(value = "Token", defaultValue = "null") String token, @PathVariable("gameId") int gameId) {

        //user authentication over token in header
        userService.checkToken(token);

        int currentRound = gameService.getCurrentRoundNr(gameId);
        int totalRounds = gameService.getTotalNrRounds(gameId);
        return ResponseEntity.ok(new RoundNr(currentRound, totalRounds));
    }

    @GetMapping("/games/{gameId}/round/results")
    public ResponseEntity<RoundGetDTO> getRoundInformation(@RequestHeader(value = "Token", defaultValue = "null") String token, @PathVariable("gameId") int gameId){
        //user authentication over token in header
        userService.checkToken(token);

        return ResponseEntity.ok().body(DTOMapper.INSTANCE.convertGameToRoundGetDTO(GameRepository.getGameById(gameId)));
    }
}
