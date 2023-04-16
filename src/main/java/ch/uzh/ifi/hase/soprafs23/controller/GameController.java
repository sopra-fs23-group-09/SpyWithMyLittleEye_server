package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.constant.Role;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import ch.uzh.ifi.hase.soprafs23.service.LobbyService;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import ch.uzh.ifi.hase.soprafs23.stomp.dto.*;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

import java.util.Random;

@Controller
public class GameController {

    private final UserService userService;
    private final LobbyService lobbyService;
    private final GameService gameService;

    GameController(UserService userService, GameService gameService, LobbyService lobbyService){
        this.userService = userService;
        this.gameService = gameService;
        this.lobbyService = lobbyService;
    }

    @MessageMapping("game/{lobbyId}/spiedObject")
    @SendTo("/game/{lobbyId}/spiedObject")
    public SpiedObjectOut handleSpiedObject(SpiedObjectIn spiedObjectIn, @DestinationVariable("lobbyId") int lobbyId) throws Exception{
        String keyword = HtmlUtils.htmlEscape(spiedObjectIn.getKeyword());
        String color = HtmlUtils.htmlEscape(spiedObjectIn.getColor());

        gameService.setKeywordAndColor(lobbyId, keyword, color);
        return new SpiedObjectOut(color);
    }

    @MessageMapping("game/{lobbyId}/guesses")
    @SendTo("/game/{lobbyId}/guesses")
    public GuessOut handleGuess(GuessIn guessIn, @DestinationVariable("lobbyId") int lobbyId) throws Exception{
        User user = userService.getUser(guessIn.getId());
        String username = user.getUsername();
        String guess = HtmlUtils.htmlEscape(guessIn.getGuess());
        int lobbyID = lobbyId;

        if (gameService.checkGuess(lobbyID, guess)){
            guess = "CORRECT";
            gameService.allocatePoints(lobbyID, user);
        }

        return new GuessOut(username, guess);
    }

    @MessageMapping("game/{lobbyId}/round/{playerId}")
    @SendTo("/game/{lobbyId}/round/{playerId}")
    public Role determineRoles(@DestinationVariable("lobbyId") int lobbyId, @DestinationVariable("playerId") Long playerId) throws Exception{
        return lobbyService.getRole(lobbyId, playerId); //note c: or get it from round?
    }

    @MessageMapping("game/{lobbyId}/roundnr")
    @SendTo("/game/{lobbyId}/roundnr")
    public RoundNr determineRound(@DestinationVariable("lobbyId") int lobbyId) throws Exception{
        //int currentRound = lobbyService.getCurrentRoundNr(lobbyId);
        //int totalRounds = lobbyService.getTotalNrRounds(lobbyId);
        return new RoundNr(2,5);
    }

    @MessageMapping("game/{lobbyId}/hints")
    @SendTo("/game/{lobbyId}/hints")
    public Hint distributeHints(Hint hint, @DestinationVariable("lobbyId") int lobbyId) throws Exception{
        return new Hint(hint.getHint());
    }
}
