package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.PlayerStatus;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.repository.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityNotFoundException;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the UserResource REST resource.
 *
 * @see PlayerService
 */
@WebAppConfiguration
@SpringBootTest
public class PlayerServiceIntegrationTest {

    @Qualifier("playerRepository")
    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private PlayerService playerService;

    @BeforeEach
    public void setup() {
        playerRepository.deleteAll();
    }

    /*
    @Test
    public void updateToken_success() {
        createUser_validInputs_success();

        playerRepository.deleteAll();

        Player testPlayer = new Player();
        testPlayer.setPassword("testPassword");
        testPlayer.setUsername("testUsername");
        Player created = playerService.createUser(testPlayer);
        Player updated = playerService.updateToken(created.getId(), "2");

        assertEquals(created.getId(), updated.getId());
        assertEquals(created.getPassword(), updated.getPassword());
        assertEquals(created.getUsername(), updated.getUsername());
        assertEquals("2", updated.getToken());
        assertEquals(created.getCreationDate(), updated.getCreationDate());
        assertEquals(PlayerStatus.ONLINE, updated.getStatus());
    }

    @Test
    public void updateToken_failure() {
        assertThrows(EntityNotFoundException.class, () -> playerService.updateToken(1L, "2"));
    }

    @Test
    public void checkToken_success() {
        Player testPlayer = new Player();
        testPlayer.setPassword("testPassword");
        testPlayer.setUsername("testUsername");
        Player created = playerService.createUser(testPlayer);

        assertDoesNotThrow(() -> playerService.checkToken(created.getToken()));
    }

    @Test
    public void checkToken_failure() {
        assertThrows(ResponseStatusException.class, () -> playerService.checkToken("1"));
    }

    @Test
    public void setOffline_success() {
        Player testPlayer = new Player();
        testPlayer.setPassword("testPassword");
        testPlayer.setUsername("testUsername");
        Player created = playerService.createUser(testPlayer);

        playerService.setOffline(created.getToken(), false);
        Player updated = playerService.getUser(created.getId());
        assertEquals(PlayerStatus.ONLINE, updated.getStatus());

        playerService.setOffline(created.getToken(), true);
        updated = playerService.getUser(created.getId());
        assertEquals(PlayerStatus.OFFLINE, updated.getStatus());
    }

    @Test
    public void setOffline_failure() {
        assertThrows(ResponseStatusException.class, () -> playerService.setOffline("1", true));
    }

    @Test
    public void getUserID_success() {
        Player testPlayer = new Player();
        testPlayer.setPassword("testPassword");
        testPlayer.setUsername("testUsername");
        Player created = playerService.createUser(testPlayer);
        assertEquals(created.getId(), playerService.getUserID(created.getToken()));
    }

    @Test
    public void getUserID_failure() {
        assertThrows(ResponseStatusException.class, () -> playerService.getUserID("1"));
    }

    @Test
    public void updateUser_success() {
        Player testPlayer = new Player();
        testPlayer.setPassword("testPassword");
        testPlayer.setUsername("testUsername");
        Player toUpdate = new Player();
        toUpdate.setUsername("updatedName");
        toUpdate.setBirthday(new Date(0L));

        Player created = playerService.createUser(testPlayer);
        playerService.updateUser(toUpdate, created.getToken(), created.getId());
        Player updated = playerService.getUser(created.getId());

        assertEquals(toUpdate.getUsername(), updated.getUsername());
        assertEquals(toUpdate.getBirthday(), updated.getBirthday());
    }

    @Test
    public void updateUser_failureToken() {
        Player testPlayer = new Player();
        testPlayer.setPassword("testPassword");
        testPlayer.setUsername("testUsername");
        Player created = playerService.createUser(testPlayer);

        assertThrows(ResponseStatusException.class, ()-> playerService.updateUser(created, "1", created.getId()));
    }
    @Test
    public void updateUser_failureId() {
        Player testPlayer = new Player();
        testPlayer.setPassword("testPassword");
        testPlayer.setUsername("testUsername");
        Player created = playerService.createUser(testPlayer);

        assertThrows(ResponseStatusException.class, ()-> playerService.updateUser(created, created.getToken(), -1L));
    }

    @Test
    public void createUser_validInputs_success() {
        // given
        assertNull(playerRepository.findByUsername("testUsername"));

        Player testPlayer = new Player();
        testPlayer.setPassword("testPassword");
        testPlayer.setUsername("testUsername");

        // when
        Player createdPlayer = playerService.createUser(testPlayer);

        // then
        assertEquals(testPlayer.getId(), createdPlayer.getId());
        assertEquals(testPlayer.getPassword(), createdPlayer.getPassword());
        assertEquals(testPlayer.getUsername(), createdPlayer.getUsername());
        assertNotNull(createdPlayer.getToken());
        assertNotNull(createdPlayer.getCreationDate());
        assertEquals(PlayerStatus.ONLINE, createdPlayer.getStatus());
    }

    @Test
    public void createUser_duplicateUsername_throwsException() {
        assertNull(playerRepository.findByUsername("testUsername"));

        Player testPlayer = new Player();
        testPlayer.setPassword("testName");
        testPlayer.setUsername("testUsername");
        Player createdPlayer = playerService.createUser(testPlayer);

        // attempt to create second user with same username
        Player testPlayer2 = new Player();

        // change the name but forget about the username
        testPlayer2.setPassword("testName2");
        testPlayer2.setUsername("testUsername");

        // check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> playerService.createUser(testPlayer2));
    }

    @Test
    public void getTop15User_success() {
        assertNull(playerRepository.findByUsername("testUsername"));

        Player testPlayer = new Player();
        testPlayer.setPassword("testPassword");
        testPlayer.setUsername("testUsername");
        Player createdPlayer = playerService.createUser(testPlayer);

        Player top = playerService.getTop15User().get(0);

        assertEquals(createdPlayer.getId(), top.getId());
        assertEquals(createdPlayer.getPassword(), top.getPassword());
        assertEquals(createdPlayer.getUsername(), top.getUsername());
        assertEquals(createdPlayer.getToken(), top.getToken());
        assertEquals(createdPlayer.getCreationDate(), top.getCreationDate());
        assertEquals(createdPlayer.getStatus(), top.getStatus());
    }

    @Test
    public void getUser_success() {
        Player testPlayer = new Player();
        testPlayer.setPassword("testName");
        testPlayer.setUsername("testUsername");
        Player createdPlayer = playerService.createUser(testPlayer);

        Player returned = playerService.getUser(createdPlayer.getId());
        assertEquals(createdPlayer.getId(), returned.getId());
        assertEquals(createdPlayer.getPassword(), returned.getPassword());
        assertEquals(createdPlayer.getUsername(), returned.getUsername());
        assertEquals(createdPlayer.getToken(), returned.getToken());
        assertEquals(createdPlayer.getCreationDate(), returned.getCreationDate());
        assertEquals(createdPlayer.getStatus(), returned.getStatus());
    }

    @Test
    public void getUser_failure() {
        assertThrows(ResponseStatusException.class, ()-> playerService.getUser(1L));
    }
     */
}
