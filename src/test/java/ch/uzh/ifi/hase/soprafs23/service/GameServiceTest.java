package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.controller.GameStompController;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {

    @Test
    public void testSaveSpiedObjectInfo() {
        // create players
        User player1 = new User();
        player1.setId(1L);
        player1.setUsername("petra");
        player1.setPassword("password");
        player1.setStatus(UserStatus.ONLINE);
        player1.setToken("token");
        player1.setCreationDate(new Date(0L));
        player1.setBirthday(new Date(0L));

        User player2 = new User();
        player2.setId(2L);
        player2.setUsername("eva");
        player2.setPassword("1234");
        player2.setStatus(UserStatus.ONLINE);
        player2.setToken("token");
        player2.setCreationDate(new Date(0L));
        player2.setBirthday(new Date(0L));

        List<User> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);

        //create game
        int gameid = 1;
        Game game = new Game(gameid,players,3,player1);
        game.nextRound();

        //add game to GameRepository
        GameRepository.addGame(game);

        GameService gameService = new GameService();

        String spiedObject = "car";
        gameService.saveSpiedObjectInfo(gameid, spiedObject);

        assertEquals(game.getKeyword(),spiedObject);
    }


    @Test
    void checkGuessAndAllocatePoints_wrongGuess() {
        // create players
        User player1 = new User();
        player1.setId(1L);
        player1.setUsername("petra");
        player1.setPassword("password");
        player1.setStatus(UserStatus.ONLINE);
        player1.setToken("token");
        player1.setCreationDate(new Date(0L));
        player1.setBirthday(new Date(0L));

        User player2 = new User();
        player2.setId(2L);
        player2.setUsername("eva");
        player2.setPassword("1234");
        player2.setStatus(UserStatus.ONLINE);
        player2.setToken("token");
        player2.setCreationDate(new Date(0L));
        player2.setBirthday(new Date(0L));

        List<User> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);

        //create game
        int gameid = 1;
        Game game = new Game(gameid,players,3,player1);
        game.nextRound();

        //add game to GameRepository
        GameRepository.addGame(game);

        GameService gameService = new GameService();

        String spiedObject = "car";
        gameService.saveSpiedObjectInfo(gameid, spiedObject);
        Date guessTime = new Date();
        gameService.checkGuessAndAllocatePoints(gameid,player2,"house", guessTime);

        assertEquals(0,game.getPlayerPoints().get(player2));
    }

    @Test
    void checkGuessAndAllocatePoints_correctGuess() {
        // create players
        User player1 = new User();
        player1.setId(1L);
        player1.setUsername("petra");
        player1.setPassword("password");
        player1.setStatus(UserStatus.ONLINE);
        player1.setToken("token");
        player1.setCreationDate(new Date(0L));
        player1.setBirthday(new Date(0L));

        User player2 = new User();
        player2.setId(2L);
        player2.setUsername("eva");
        player2.setPassword("1234");
        player2.setStatus(UserStatus.ONLINE);
        player2.setToken("token");
        player2.setCreationDate(new Date(0L));
        player2.setBirthday(new Date(0L));

        List<User> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);

        //create game
        int gameid = 1;
        Game game = new Game(gameid,players,3,player1);
        game.nextRound();
        Date startTime = new Date();
        game.initializeStartTime(startTime);

        //add game to GameRepository
        GameRepository.addGame(game);

        GameService gameService = new GameService();

        String spiedObject = "car";

        gameService.saveSpiedObjectInfo(gameid, spiedObject);
        int waitingPeriod = 5;
        try {
            Thread.sleep(waitingPeriod*1000); // sleep for 5 seconds
        } catch (InterruptedException e) {
        }

        Date guessTime = new Date();
        gameService.checkGuessAndAllocatePoints(gameid,player2,spiedObject, guessTime);

        assertEquals(Game.DURATION*60-waitingPeriod,game.getPlayerPoints().get(player2));
    }

    @Test
    void endRoundIfAllUsersGuessedCorrectly_notAllUsersGuessedCorrectly() {
    }

    @Test
    void handleGameOver() {
    }

    @Test
    void nextRound() {
    }

    @Test
    void initializeStartTime() {
    }
}