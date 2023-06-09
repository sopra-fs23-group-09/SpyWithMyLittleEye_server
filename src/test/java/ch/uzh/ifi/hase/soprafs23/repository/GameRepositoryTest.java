package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.constant.PlayerStatus;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.service.PlayerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameRepositoryTest {

    private Game testGame;

    @BeforeEach
    void setup() {
		GameRepository.reset();

        //create players
        Player player1 = new Player();
        player1.setId(1L);
        player1.setUsername("petra");
        player1.setPassword("password");
        player1.setStatus(PlayerStatus.ONLINE);
        player1.setToken("token");
        player1.setCreationDate(new Date(0L));
        player1.setBirthday(new Date(0L));

        Player player2 = new Player();
        player2.setId(2L);
        player2.setUsername("eva");
        player2.setPassword("1234");
        player2.setStatus(PlayerStatus.ONLINE);
        player2.setToken("token");
        player2.setCreationDate(new Date(0L));
        player2.setBirthday(new Date(0L));

        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);

        //create game
        testGame = new Game(1,players,3, player1, Mockito.mock(PlayerService.class), 1.5f);
    }
    @Test
    void getGameById() {
        //add game to the repository
        GameRepository.addGame(testGame);

        //find game in repsitory by id
        Game gameFromRepository = GameRepository.getGameById(testGame.getId());

        //check if game added beforehand and game retrieved via id is the same
        assertEquals(gameFromRepository.getId(), testGame.getId());
        assertEquals(gameFromRepository.getHostId(), testGame.getHostId());
        assertEquals(gameFromRepository.getAmountRounds(), testGame.getAmountRounds());
    }

    @Test
    void deleteGame() {
        //add and delete  game
        GameRepository.addGame(testGame);
        GameRepository.deleteGame(testGame.getId());

        //game should not be found in GameRepository anymore
        assertNull(GameRepository.getGameById(testGame.getId()));
    }
}