package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private GameService gameService;

    private User player1;
    private User player2;
    private int gameId;
    private Game game;

    @BeforeEach
    public void setup(){
        GameRepository.reset();

        MockitoAnnotations.openMocks(this);

        player1 = new User();
        player1.setId(1L);
        player1.setUsername("petra");
        player1.setPassword("password");
        player1.setStatus(UserStatus.ONLINE);
        player1.setToken("token");
        player1.setCreationDate(new Date(0L));
        player1.setBirthday(new Date(0L));

        player2 = new User();
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
        gameId = 1;
        game = new Game(gameId,players,3,player1,userService, 1.5f);
        game.nextRound();

        //set startTime
        Date startTime = new Date();
        game.initializeStartTime(startTime);

        //add game to GameRepository
        GameRepository.addGame(game);
    }

    @Test
    public void testSaveSpiedObjectInfo() {
        String spiedObject = "car";
        gameService.saveSpiedObjectInfo(gameId, spiedObject);

        assertEquals(game.getKeyword(),spiedObject);
    }


    @Test
    void checkGuessAndAllocatePoints_wrongGuess() {
        //set keyword to "car"
        String spiedObject = "car";
        gameService.saveSpiedObjectInfo(gameId, spiedObject);

        //check wrong guess: "house"
        String guess = "house";
        gameService.checkGuessAndAllocatePoints(gameId,player2,guess, new Date());

        assertEquals(0,game.getPlayerPoints().get(player2));
    }

    @Test
    void checkGuessAndAllocatePoints_correctGuess() {

        //set keyword to "car"
        String spiedObject = "car";
        gameService.saveSpiedObjectInfo(gameId, spiedObject);

        //wait for 5 seconds before guessed correctly
        int waitingPeriod = 2;
        try {
            Thread.sleep(waitingPeriod*1000); // sleep for 2 seconds
        } catch (InterruptedException e) {
        }

        Date guessTime = new Date();
        gameService.checkGuessAndAllocatePoints(gameId,player2,spiedObject, guessTime);

        assertEquals(game.getDuration()*60-waitingPeriod,(float) game.getPlayerPoints().get(player2));
    }
}