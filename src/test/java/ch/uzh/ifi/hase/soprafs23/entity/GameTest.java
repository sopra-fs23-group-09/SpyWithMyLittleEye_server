package ch.uzh.ifi.hase.soprafs23.entity;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    @BeforeEach
    void setUp() {
        User testUser = new User();
        testUser.setPassword("testPassword");
        testUser.setUsername("testUsername");
        testUser.setHighScore(10);
        testUser.setCreationDate(new Date());
        testUser.setStatus(UserStatus.ONLINE);
        testUser.setToken("1");
        List<User> players = new ArrayList<>();
        Game game1 = new Game(1,players, 5,testUser);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void updatePointsIfGameEnded() {
    }

    @Test
    void runTimer() {
    }

    @Test
    void endRoundIfAllUsersGuessedCorrectly() {
    }

    @Test
    void getPlayerPoints() {
    }

    @Test
    void allocatePoints() {
    }

    @Test
    void checkGuess() {
    }

    @Test
    void nextRound() {
    }
}