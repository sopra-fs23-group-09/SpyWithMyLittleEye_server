package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.constant.PlayerStatus;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class PlayerRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PlayerRepository playerRepository;

    @Test
    public void findByUsername_success() {
        // given
        Player player = new Player();
        player.setPassword("password");
        player.setUsername("firstname@lastname");
        player.setStatus(PlayerStatus.ONLINE);
        player.setToken("1");
        player.setCreationDate(new Date());

        entityManager.persist(player);
        entityManager.flush();

        // when
        Player found = playerRepository.findByUsername(player.getUsername());

        // then
        assertNotNull(found.getId());
        assertEquals(found.getPassword(), player.getPassword());
        assertEquals(found.getUsername(), player.getUsername());
        assertEquals(found.getToken(), player.getToken());
        assertEquals(found.getStatus(), player.getStatus());
        assertEquals(found.getCreationDate(), player.getCreationDate());
    }

    @Test
    public void findByToken_success() {
        // given
        Player player = new Player();
        player.setPassword("password");
        player.setUsername("firstname@lastname");
        player.setStatus(PlayerStatus.ONLINE);
        player.setToken("1");
        player.setCreationDate(new Date());

        entityManager.persist(player);
        entityManager.flush();

        // when
        Player found = playerRepository.findByToken(player.getToken());

        // then
        assertNotNull(found.getId());
        assertEquals(found.getPassword(), player.getPassword());
        assertEquals(found.getUsername(), player.getUsername());
        assertEquals(found.getToken(), player.getToken());
        assertEquals(found.getStatus(), player.getStatus());
        assertEquals(found.getCreationDate(), player.getCreationDate());
    }

    @Test
    public void getTop15Users(){
        Player testPlayer = new Player();
        testPlayer.setPassword("testPassword");
        testPlayer.setUsername("testUsername");
        testPlayer.setHighScore(10);
        testPlayer.setCreationDate(new Date());
        testPlayer.setStatus(PlayerStatus.ONLINE);
        testPlayer.setToken("1");

        Player testPlayer2 = new Player();
        testPlayer2.setPassword("testPassword2");
        testPlayer2.setUsername("testUsername2");
        testPlayer2.setHighScore(20);
        testPlayer2.setCreationDate(new Date());
        testPlayer2.setStatus(PlayerStatus.ONLINE);
        testPlayer2.setToken("2");

        Player testPlayer3 = new Player();
        testPlayer3.setPassword("testPassword3");
        testPlayer3.setUsername("testUsername3");
        testPlayer3.setHighScore(30);
        testPlayer3.setCreationDate(new Date());
        testPlayer3.setStatus(PlayerStatus.ONLINE);
        testPlayer3.setToken("3");

        entityManager.persist(testPlayer);
        entityManager.flush();
        entityManager.persist(testPlayer2);
        entityManager.flush();
        entityManager.persist(testPlayer3);
        entityManager.flush();

        List<Player> players = playerRepository.findTop15ByOrderByHighScoreDesc();
        assertEquals(players.get(0), testPlayer3);
        assertEquals(players.get(1), testPlayer2);
    }
}
