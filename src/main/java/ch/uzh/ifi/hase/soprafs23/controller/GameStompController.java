package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.Location;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import ch.uzh.ifi.hase.soprafs23.service.LobbyService;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import ch.uzh.ifi.hase.soprafs23.service.WebSocketService;
import ch.uzh.ifi.hase.soprafs23.stomp.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

import java.util.Date;

@Controller
public class GameStompController {

    private final Logger logger = LoggerFactory.getLogger(GameStompController.class);

    private final UserService userService;
    private final LobbyService lobbyService;
    private final GameService gameService;

    private final WebSocketService webSocketService;

    GameStompController(UserService userService, GameService gameService, LobbyService lobbyService, WebSocketService ws){
        this.userService = userService;
        this.gameService = gameService;
        this.lobbyService = lobbyService;
        this.webSocketService = ws;
    }

    @MessageMapping("/games/{gameId}/spiedObject")
    //@SendTo("/topic/games/{gameId}/spiedObject")
    //@SubscribeMapping("/topic/games/{gameId}/spiedObject")
    public void handleSpiedObject(SpiedObjectIn spiedObjectIn, @DestinationVariable("gameId") int gameId) throws Exception{
        //extract information from JSON
        String keyword = spiedObjectIn.getObject();
        String color = spiedObjectIn.getColor();
        Location location = spiedObjectIn.getLocation();

        //save information of spied object
        gameService.saveSpiedObjectInfo(gameId, keyword); //n: removed color and Location because not needed to store in game

        //return SpiedObjectOut to subscribers
        webSocketService.sendMessageToSubscribers("/topic/games/"+gameId+"/spiedObject", new SpiedObjectOut(location, color));
    }

    @MessageMapping("/games/{gameId}/guesses")
    //@SendTo("/topic/games/{gameId}/guesses")
    //@SubscribeMapping("/topic/games/{gameId}/guesses")
    public void handleGuess(GuessIn guessIn, @DestinationVariable("gameId") int gameId) throws Exception{
        Date guessTime = new Date(); // to make sure that guess time is registered at request
        //extract information from JSON
        String guess = guessIn.getGuess();
        User user = userService.getUser(guessIn.getId());
        //evaluate guess and save String to be returned to subscribers
        String guessBack = gameService.checkGuessAndAllocatePoints(gameId, user, guess, guessTime);

        String username = user.getUsername();

        //TODO: guess is automatically stored in list of guesses, there also is a method in gameservice to get them (getGuesses(gameId))
        //TODO: need to return the list/an DTO or something such that the Guess objects correctly reach the client
        //return GuessOut to subscribers
        webSocketService.sendMessageToSubscribers("/topic/games/"+gameId+"/guesses", new GuessOut(username, guessBack));
    }

    @MessageMapping("/games/{gameId}/hints")
    //@SendTo("/topic/games/{gameId}/hints")
    //@SubscribeMapping("/topic/games/{gameId}/hints")
    public void distributeHints(Hint hint, @DestinationVariable("gameId") int gameId) throws Exception{
        //send hint directly to all subscribers
        webSocketService.sendMessageToSubscribers("/topic/games/"+gameId+"/hints", hint);
    }
}
