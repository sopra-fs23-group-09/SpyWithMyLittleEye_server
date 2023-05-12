package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Lobby;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.repository.LobbyRepository;
import ch.uzh.ifi.hase.soprafs23.repository.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LobbyServiceTest {
    @Mock
    private PlayerRepository playerRepository;

    private Player testPlayer;

    @Mock
    private PlayerService playerService;

    @InjectMocks
    private LobbyService lobbyService;

    @BeforeEach
    public void setup() {
        LobbyRepository.reset();

        MockitoAnnotations.openMocks(this);

        testPlayer = new Player();
        testPlayer.setId(1L);
        testPlayer.setToken("1");
        testPlayer.setPassword("testPassword");
        testPlayer.setUsername("testUsername");
        testPlayer.setHighScore(10);
    }

    @Test
    public void createLobby_success() {
        Lobby created = lobbyService.createLobby(testPlayer, 2, 1.5f);

        assertEquals(created.getHostId(), testPlayer.getId());
        assertEquals(created.getAmountRounds(), 2);
        assert created.getAccessCode() >= 10000 && created.getAccessCode() < 100000;
    }

    @ParameterizedTest
    @ValueSource(ints = { -12, -3, 0, 21, 100, Integer.MAX_VALUE })
    public void createLobby_failureAmountRounds(int amountRounds) {
        assertThrows(ResponseStatusException.class, () -> lobbyService.createLobby(testPlayer, amountRounds, 1.5f));
    }

    @ParameterizedTest
    @MethodSource("createLobby_failureHostInOtherLobby_arguments")
    public void createLobby_failureHostInOtherLobby(int lobbyID, int amountRounds, float duration) {
        testPlayer.setLobbyID(lobbyID);
        assertThrows(ResponseStatusException.class, () -> lobbyService.createLobby(testPlayer, amountRounds, duration));
    }

    private static List<Arguments> createLobby_failureHostInOtherLobby_arguments() {
        List<Arguments> ret = new LinkedList<>();
        for (int rounds = 1; rounds <= 20; rounds++) {
            for (int i = 0; i < 10; i++) {
                int r = (int) (Math.random() * 99 + 1);
                ret.add(Arguments.of(r, rounds, 1.5f));
                ret.add(Arguments.of(-r, rounds, 1.5f));
            }
        }
        return ret;
    }

    @Test
    public void startGame_success() {
        Lobby lobby = new Lobby(testPlayer, 2, 12345, 2, 1.5f);
        LobbyRepository.addLobby(lobby);

        Game game = lobbyService.startGame(lobby.getId(), playerService);

        assertEquals(lobby.getId(), game.getId());
        assertEquals(lobby.getAmountRounds(), game.getAmountRounds());
        assertEquals(lobby.getHostId(), game.getHostId());
        assertEquals(1.5f, game.getDuration());
    }

    @ParameterizedTest
    @ValueSource(ints = { 1, 20, -4, 13, 29, Integer.MAX_VALUE, Integer.MIN_VALUE })
    public void startGame_failureLobbyDoesntExist(int lobbyId) {
        assertThrows(ResponseStatusException.class, () -> lobbyService.startGame(100, playerService));
    }

    @Test
    public void startGame_failureGameAlreadyStarted() {
        Lobby lobby = new Lobby(testPlayer, 3, 12335, 2, 1.5f);
        LobbyRepository.addLobby(lobby);

        lobbyService.startGame(lobby.getId(), playerService);
        assertThrows(ResponseStatusException.class, () -> lobbyService.startGame(lobby.getId(), playerService));
    }

    @Test
    public void addUser_success() {
        Lobby lobby = new Lobby(testPlayer, 4, 11345, 2, 1.5f);
        LobbyRepository.addLobby(lobby);

        Player u = new Player();
        u.setId(1L);
        u.setToken("3");
        u.setPassword("pw");
        u.setUsername("un");

        List<Player> compare = List.of(testPlayer, u);

        Lobby l = lobbyService.addUser(u, lobby.getAccessCode());
        assertEquals(l.getPlayers(), compare);

    }

    @ParameterizedTest
    @ValueSource(ints = { Integer.MIN_VALUE, -1000, -234, -3, 0, 1, 102, Integer.MAX_VALUE })
    public void addUser_failureWrongAccessCode(int accessCode) {
        assertThrows(ResponseStatusException.class, () -> lobbyService.addUser(testPlayer, accessCode));
    }

    @ParameterizedTest
    @ValueSource(ints = { Integer.MIN_VALUE, -1234, -126, -23, -5, 1, 34, 231, Integer.MAX_VALUE })
    public void addUser_failure(int lobbyID) {
        Lobby lobby = new Lobby(testPlayer, 5, 17345, 2, 1.5f);
        LobbyRepository.addLobby(lobby);

        Player u = new Player();
        u.setId(1L);
        u.setToken("3");
        u.setPassword("pw");
        u.setUsername("un");
        u.setLobbyID(lobbyID);

        assertThrows(ResponseStatusException.class, () -> lobbyService.addUser(u, 17345));
    }

    @Test
    public void deleteLobby_success() {
        Lobby l = new Lobby(testPlayer, 89, 12345, 4, 1.5f);
        LobbyRepository.addLobby(l);

        assertNotNull(lobbyService.getLobby(l.getId()));
        lobbyService.deleteLobby(l.getId(), playerService);
        assertThrows(ResponseStatusException.class, () -> lobbyService.getLobby(l.getId()));
    }

    @ParameterizedTest
    @ValueSource(ints = { Integer.MIN_VALUE, -1234, -126, -23, -5, 0, 1, 34, 231, Integer.MAX_VALUE })
    public void deleteLobby_failureNonexisting(int lobbyId) {
        assertThrows(ResponseStatusException.class, () -> lobbyService.deleteLobby(lobbyId, playerService));
    }

    @Test
    public void removeUser_success() {
        int lobbyId = 1;
        Lobby lobby = new Lobby(testPlayer, lobbyId, 12345, 2, 1.5f);

        LobbyRepository.addLobby(lobby);

        assertEquals(1, lobby.getPlayers().size());

        lobbyService.removeUser(testPlayer, lobbyId);

        assertEquals(0, lobby.getPlayers().size());
    }

    @Test
    public void removeUser_playerNotInLobby() {

        //assertThrows(ResponseStatusException.class, () -> lobbyService.deleteLobby(lobbyId));
    }

}
