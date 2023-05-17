package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.repository.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private PlayerService playerService;

    private Player testPlayer;


    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // given
        testPlayer = new Player();
        testPlayer.setId(1L);
        testPlayer.setToken("1");
        testPlayer.setPassword("testPassword");
        testPlayer.setUsername("testUsername");
        testPlayer.setHighScore(10);


        // when -> any object is being save in the playerRepository -> return the dummy
        // testPlayer
        Mockito.when(playerRepository.save(Mockito.any())).thenReturn(testPlayer);
    }

    @Test
    public void updateToken_success() {
        Mockito.when(playerRepository.getOne(Mockito.anyLong())).thenReturn(testPlayer);

        Player updated = playerService.updateToken(testPlayer.getId(), "2");
        Mockito.verify(playerRepository, Mockito.times(1)).save(Mockito.any());

        assertEquals(testPlayer.getId(), updated.getId());
        assertEquals(testPlayer.getPassword(), updated.getPassword());
        assertEquals(testPlayer.getUsername(), updated.getUsername());
    }

    @Test
    public void checkToken_failure() {
        Mockito.when(playerRepository.findByToken(Mockito.anyString())).thenReturn(null);

        assertThrows(ResponseStatusException.class, () -> playerService.checkToken(testPlayer.getToken()));
    }

    @Test
    public void checkToken_success() {
        Mockito.when(playerRepository.findByToken(Mockito.anyString())).thenReturn(testPlayer);

        assertDoesNotThrow(() -> playerService.checkToken(testPlayer.getToken()));
    }

    /*
    @Test
    public void setOffline() {
        Mockito.when(playerRepository.findByToken(Mockito.anyString())).thenReturn(testPlayer);

        playerService.setOffline(testPlayer.getToken(), true);
        Mockito.verify(playerRepository, Mockito.atLeast(1)).save(Mockito.any());
    }
     */

    @Test
    public void getUsers_success() {
        Mockito.when(playerRepository.findAll()).thenReturn(List.of(testPlayer));

        Player createdPlayer = playerService.getPlayers().get(0);
        assertEquals(testPlayer.getId(), createdPlayer.getId());
        assertEquals(testPlayer.getPassword(), createdPlayer.getPassword());
        assertEquals(testPlayer.getUsername(), createdPlayer.getUsername());
    }

    @Test
    public void getUserId_failure() {
        Mockito.when(playerRepository.findByToken(Mockito.anyString())).thenReturn(null);

        assertThrows(ResponseStatusException.class, () -> playerService.getPlayerID(testPlayer.getToken()));
    }

    @Test
    public void getUserID_success() {
        Mockito.when(playerRepository.findByToken(Mockito.anyString())).thenReturn(testPlayer);

        assertEquals(testPlayer.getId(), playerService.getPlayerID(testPlayer.getToken()));
    }

    @Test
    public void updateUser_invalidToken() {
        Mockito.when(playerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(testPlayer));

        assertThrows(ResponseStatusException.class, () -> playerService.updatePlayer(testPlayer, "-1", testPlayer.getId()));
    }
    @Test
    public void updateUser_missingUser() {
        Mockito.when(playerRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> playerService.updatePlayer(testPlayer, testPlayer.getToken(), testPlayer.getId()));
    }

    @Test
    public void updateUser_success() {
        Mockito.when(playerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(testPlayer));

        playerService.updatePlayer(testPlayer, testPlayer.getToken(), testPlayer.getId());
        Mockito.verify(playerRepository, Mockito.times(1)).save(Mockito.any());
    }
    /*
    @Test
    public void createUser_validInputs_success() {
        // when -> any object is being save in the playerRepository -> return the dummy
        // testPlayer
        Player createdPlayer = playerService.createPlayer(testPlayer);

        // then
        Mockito.verify(playerRepository, Mockito.times(1)).save(Mockito.any());

        assertEquals(testPlayer.getId(), createdPlayer.getId());
        assertEquals(testPlayer.getPassword(), createdPlayer.getPassword());
        assertEquals(testPlayer.getUsername(), createdPlayer.getUsername());
        assertNotNull(createdPlayer.getCreationDate());
        assertNotNull(createdPlayer.getToken());
        //assertEquals(PlayerStatus.ONLINE, createdPlayer.getStatus());
    }
     */

    /*
    @Test
    public void createUser_duplicateUsername_throwsException() {
        // given -> a first user has already been created
        playerService.createPlayer(testPlayer);

        // when -> setup additional mocks for PlayerRepository
        Mockito.when(playerRepository.findByUsername(Mockito.any())).thenReturn(testPlayer);

        // then -> attempt to create second user with same user -> check that an error
        // is thrown
        assertThrows(ResponseStatusException.class, () -> playerService.createPlayer(testPlayer));
    }
     */

    @Test
    public void getTop15User() {
        Mockito.when(playerRepository.findTop15ByOrderByHighScoreDesc()).thenReturn(Collections.unmodifiableList(List.of(testPlayer)));

        Player returned = playerService.getTop15PlayersHighScore().get(0);
        assertEquals(testPlayer.getId(), returned.getId());
        assertEquals(testPlayer.getPassword(), returned.getPassword());
        assertEquals(testPlayer.getUsername(), returned.getUsername());
    }

    @Test
    public void getUser_exists() {
        Mockito.when(playerRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(testPlayer));

        Player returned = playerService.getPlayer(testPlayer.getId());
        assertEquals(testPlayer.getId(), returned.getId());
        assertEquals(testPlayer.getPassword(), returned.getPassword());
        assertEquals(testPlayer.getUsername(), returned.getUsername());
    }

    @Test
    public void getUser_notFound() {
        Mockito.when(playerRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> playerService.getPlayer(testPlayer.getId()));
    }
}
