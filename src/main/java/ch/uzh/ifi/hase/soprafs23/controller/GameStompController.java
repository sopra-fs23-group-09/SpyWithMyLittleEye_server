package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.stomp.dto.Location;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.entity.wrappers.Guess;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import ch.uzh.ifi.hase.soprafs23.service.LobbyService;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import ch.uzh.ifi.hase.soprafs23.service.WebSocketService;
import ch.uzh.ifi.hase.soprafs23.stomp.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ResponseStatusException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@Controller
public class GameStompController {

    private final Logger logger = LoggerFactory.getLogger(GameStompController.class);

    private Timer roundTimer;
    private final UserService userService;
    private final LobbyService lobbyService;
    private final GameService gameService;

    private final WebSocketService webSocketService;

    GameStompController(UserService userService, GameService gameService, LobbyService lobbyService, WebSocketService ws){
        this.userService = userService;
        this.gameService = gameService;
        this.lobbyService = lobbyService;
        this.webSocketService = ws;
        this.roundTimer = new Timer();
    }

    @MessageMapping("/games/{gameId}/spiedObject")
    public void handleSpiedObject(SpiedObjectIn spiedObjectIn, @DestinationVariable("gameId") int gameId) throws Exception{
        //extract information from JSON
        String keyword = spiedObjectIn.getObject();
        String color = spiedObjectIn.getColor();
        Location location = spiedObjectIn.getLocation();
        logger.info("Received spiedObject with keyword {} for game {}", keyword, gameId);

        //save information of spied object
        gameService.saveSpiedObjectInfo(gameId, keyword);


        // Save time as start time of the round
        Date startTime = gameService.initializeStartTime(gameId);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String startTimeString = dateFormat.format(startTime);

        //return SpiedObjectOut to subscribers
        webSocketService.sendMessageToSubscribers("/topic/games/"+gameId+"/spiedObject", new SpiedObjectOut(location, color, startTimeString, Game.DURATION));
        gameService.runTimer(this, gameId);
    }

    public void handleEndRound(int gameId, String message) {
        // send end round message to subscribers
        webSocketService.sendMessageToSubscribers("/topic/games/"+gameId+"/endRound", new EndRoundMessage(message));
    }

    @MessageMapping("/games/{gameId}/nextRound")
    public void nextRound(@DestinationVariable("gameId") int gameId) {
        gameService.nextRound(gameId);
        webSocketService.sendMessageToSubscribers("/topic/games/"+gameId+"/nextRound", new EndRoundMessage("Round over")); // TODO don't rly need to return anything...
    }

    private void endRoundIfAllUsersGuessedCorrectly(int gameId) {
        if (gameService.allPlayersGuessedCorrectly(gameId)){
            roundTimer.cancel();
            handleEndRound(gameId,"everyone guessed correctly");
        }
    }

    @MessageMapping("/games/{gameId}/guesses")
    public void handleGuess(GuessIn guessIn, @DestinationVariable("gameId") int gameId) {
        logger.info("Handling guess '{}'", guessIn);
        Date guessTime = new Date(); // guess time is registered at request to evaluate how many points the player gets

        //extract information from JSON
        String guess = guessIn.getGuess();
        User user = userService.getUser(guessIn.getId());

        List<Guess> playerGuesses = gameService.checkGuessAndAllocatePoints(gameId, user, guess, guessTime);

        webSocketService.sendMessageToSubscribers("/topic/games/"+gameId+"/guesses", playerGuesses);

        endRoundIfAllUsersGuessedCorrectly(gameId);
    }

    @MessageMapping("/games/{gameId}/hints")
    public void distributeHints(Hint hint, @DestinationVariable("gameId") int gameId){
        //send hint directly to all subscribers
        logger.info("Distributing hint '{}' for game {}", hint, gameId);
        webSocketService.sendMessageToSubscribers("/topic/games/"+gameId+"/hints", hint);
    }

    @MessageMapping("games/{gameId}/gameOver")
    public void endGame(@DestinationVariable("gameId") int gameId){
        gameService.handleGameOver(gameId);
        webSocketService.sendMessageToSubscribers("/topic/games/"+gameId+"/gameOver", "gameOver");
    }
}
