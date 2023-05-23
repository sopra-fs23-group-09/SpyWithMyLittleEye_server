package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.constant.Role;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.rest.dto.RoundGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import ch.uzh.ifi.hase.soprafs23.service.PlayerService;
import ch.uzh.ifi.hase.soprafs23.stomp.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class GameController {

    private final PlayerService playerService;
    private final GameService gameService;

    GameController(PlayerService playerService, GameService gameService){
        this.playerService = playerService;
        this.gameService = gameService;
    }

    @GetMapping("/games/{gameId}/roleForUser/{playerId}")
    public ResponseEntity<Role> getRole(@RequestHeader(value = "Token", defaultValue = "null") String token, @PathVariable("gameId") int gameId, @PathVariable("playerId") Long playerId){
        //user authentication over token in header
        playerService.checkToken(token);

        return ResponseEntity.ok(gameService.getRole(gameId, playerId));
    }

    @GetMapping("/games/{gameId}/roundnr")
    public ResponseEntity<RoundNr> getRound(@RequestHeader(value = "Token", defaultValue = "null") String token, @PathVariable("gameId") int gameId) {
        //user authentication over token in header
        playerService.checkToken(token);

        int currentRound = gameService.getCurrentRoundNr(gameId);
        int totalRounds = gameService.getTotalNrRounds(gameId);
        return ResponseEntity.ok(new RoundNr(currentRound, totalRounds));
    }

    @GetMapping("/games/{gameId}/round/results")
    public ResponseEntity<RoundGetDTO> getRoundInformation(@RequestHeader(value = "Token", defaultValue = "null") String token, @PathVariable("gameId") int gameId){
        //user authentication over token in header
        playerService.checkToken(token);

        Game game = gameService.getGame(gameId);

        return ResponseEntity.ok().body(DTOMapper.INSTANCE.convertGameToRoundGetDTO(game));
    }
}
