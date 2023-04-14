package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
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
    private final GameService gameService;

    GameController(UserService userService, GameService gameService){
        this.userService = userService;
        this.gameService = gameService;
    }

    @MessageMapping("game/{lobbyId}/spiedObject")
    @SendTo("/game/{lobbyId}/spiedObject")
    public SpiedObjectOut handleSpiedObject(SpiedObjectIn spiedObjectIn, @DestinationVariable("lobbyId") int lobbyId) throws Exception{
        String keyword = HtmlUtils.htmlEscape(spiedObjectIn.getKeyword());
        String color = HtmlUtils.htmlEscape(spiedObjectIn.getColor());

        int lobbyID = lobbyId;
        gameService.setKeywordAndColor(lobbyID, keyword, color);
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

    //to-do: adapt method to return correctly
    @MessageMapping("game/{lobbyId}/roles")
    @SendTo("/game/{lobbyId}/roles")
    //@SubscribeMapping("/game/{lobbyId}/roles")
    public Role determineRoles(@DestinationVariable("lobbyId") int lobbyId) throws Exception{
        Random random = new Random();
        int randomNumber = random.nextInt(5)+1; // random number between 1 and 5
        return new Role(randomNumber, "spier");
    }

    @MessageMapping("game/{lobbyId}/hints")
    @SendTo("/game/{lobbyId}/hints")
    public Hint distributeHints(Hint hint, @DestinationVariable("lobbyId") int lobbyId) throws Exception{
        return new Hint(hint.getHint());
    }
}
