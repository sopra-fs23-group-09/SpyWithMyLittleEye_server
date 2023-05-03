package ch.uzh.ifi.hase.soprafs23.entity;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    private User player1, player2;

    private Game game;

    @Mock
    private UserService userService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        // create players
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
        int gameId = 1;
        game = new Game(gameId,players,3,player1, userService);
        game.nextRound();
    }
    @Test
    void allocatePoints() {
        Date startTime = new Date();
        game.initializeStartTime(startTime);

        // set guess times for player1 and player2
        // player1 is faster = winning
        int guessingTimePlayer1 = 10;
        int guessingTimePlayer2 = 15;
        Calendar cal = Calendar.getInstance();
        cal.setTime(startTime);
        cal.add(Calendar.SECOND, guessingTimePlayer1);
        Date guessTime1 = cal.getTime();
        cal.setTime(startTime);
        cal.add(Calendar.SECOND, guessingTimePlayer2);
        Date guessTime2 = cal.getTime();

        // allocate points for the players
        game.allocatePoints(player1, guessTime1);
        game.allocatePoints(player2, guessTime2);

        //stats before updatePointsIfGameEnded is run
        int pointsWonPlayer1 = game.getPlayerPoints().get(player1);
        int pointsWonPlayer2 = game.getPlayerPoints().get(player2);

        assertEquals(Game.DURATION*60-guessingTimePlayer1,pointsWonPlayer1);
        assertEquals(Game.DURATION*60-guessingTimePlayer2,pointsWonPlayer2);

    }

    @Test
    void updatePointsIfGameEnded() {
        Date startTime = new Date();
        game.initializeStartTime(startTime);

        // set guess times for player1 and player2
        // player1 is faster = winning
        Calendar cal = Calendar.getInstance();
        cal.setTime(startTime);
        cal.add(Calendar.SECOND, 10);
        Date guessTime1 = cal.getTime();
        cal.add(Calendar.SECOND, 15);
        Date guessTime2 = cal.getTime();

        // allocate points for the players
        game.allocatePoints(player1, guessTime1);
        game.allocatePoints(player2, guessTime2);

        //stats before updatePointsIfGameEnded is run
        int gamesPlayedBeforePlayer1 = player1.getGamesPlayed();
        int gamesPlayedBeforePlayer2 = player2.getGamesPlayed();

        int highScoreBeforePlayer1 = player1.getHighScore();
        int highScoreBeforePlayer2 = player2.getHighScore();

        int gamesWonBeforePlayer1 = player1.getGamesWon();
        int gamesWonBeforePlayer2 = player2.getGamesWon();

        game.updatePointsIfGameEnded();

        //stats after updatePointsIfGameEnded is run
        int gamesPlayedAfterPlayer1 = player1.getGamesPlayed();
        int gamesPlayedAfterPlayer2 = player2.getGamesPlayed();

        int highScoreAfterPlayer1 = player1.getHighScore();
        int highScoreAfterPlayer2 = player2.getHighScore();

        int gamesWonAfterPlayer1 = player1.getGamesWon();
        int gamesWonAfterPlayer2 = player2.getGamesWon();

        //test: nr games played increased
        assertEquals(gamesPlayedBeforePlayer1+1,gamesPlayedAfterPlayer1);
        assertEquals(gamesPlayedBeforePlayer2+1,gamesPlayedAfterPlayer2);

        //test: highScore after game is same or higher than before game
        assertTrue(highScoreBeforePlayer1 <= highScoreAfterPlayer1);
        assertTrue(highScoreBeforePlayer2 <= highScoreAfterPlayer2);

        //test: nr games won is increased for the winner (=player1)
        assertEquals(gamesWonBeforePlayer1+1,gamesWonAfterPlayer1);
        assertEquals(gamesWonBeforePlayer2,gamesWonAfterPlayer2);
    }



}