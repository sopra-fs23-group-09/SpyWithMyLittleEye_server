package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Lobby;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.LobbyRepository;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
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
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    private User testUser;

    @InjectMocks
    private LobbyService lobbyService;

    @BeforeEach
    public void setup() {
        LobbyRepository.reset();

        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setId(1L);
        testUser.setToken("1");
        testUser.setPassword("testPassword");
        testUser.setUsername("testUsername");
        testUser.setHighScore(10);
    }

    @Test
    public void createLobby_success() {
        Lobby created = lobbyService.createLobby(testUser, 2);

        assertEquals(created.getHostId(), testUser.getId());
        assertEquals(created.getAmountRounds(), 2);
        assert created.getAccessCode() >= 10000 && created.getAccessCode() < 100000;
    }

    @ParameterizedTest
    @ValueSource(ints = { -12, -3, 0, 21, 100, Integer.MAX_VALUE })
    public void createLobby_failureAmountRounds(int amountRounds) {
        assertThrows(ResponseStatusException.class, () -> lobbyService.createLobby(testUser, 22));
    }

    @ParameterizedTest
    @MethodSource("createLobby_failureHostInOtherLobby_arguments")
    public void createLobby_failureHostInOtherLobby(int lobbyID, int amountRounds) {
        testUser.setLobbyID(lobbyID);
        assertThrows(ResponseStatusException.class, () -> lobbyService.createLobby(testUser, amountRounds));
    }

    private static List<Arguments> createLobby_failureHostInOtherLobby_arguments() {
        List<Arguments> ret = new LinkedList<>();
        for (int rounds = 1; rounds <= 20; rounds++) {
            for (int i = 0; i < 10; i++) {
                int r = (int) (Math.random() * 99 + 1);
                ret.add(Arguments.of(r, rounds));
                ret.add(Arguments.of(-r, rounds));
            }
        }
        return ret;
    }

    @Test
    public void startGame_success() {
        Lobby lobby = new Lobby(testUser, 2, 12345, 2);
        LobbyRepository.addLobby(lobby);

        Game game = lobbyService.startGame(lobby.getId());

        assertEquals(lobby.getId(), game.getId());
        assertEquals(lobby.getAmountRounds(), game.getAmountRounds());
        assertEquals(lobby.getHostId(), game.getHostId());
    }

    @ParameterizedTest
    @ValueSource(ints = { 1, 20, -4, 13, 29, Integer.MAX_VALUE, Integer.MIN_VALUE })
    public void startGame_failureLobbyDoesntExist(int lobbyId) {
        assertThrows(ResponseStatusException.class, () -> lobbyService.startGame(100));
    }

    @Test
    public void startGame_failureGameAlreadyStarted() {
        Lobby lobby = new Lobby(testUser, 3, 12335, 2);
        LobbyRepository.addLobby(lobby);

        lobbyService.startGame(lobby.getId());
        assertThrows(ResponseStatusException.class, () -> lobbyService.startGame(lobby.getId()));
    }

    @Test
    public void addUser_success() {
        Lobby lobby = new Lobby(testUser, 4, 11345, 2);
        LobbyRepository.addLobby(lobby);

        User u = new User();
        u.setId(1L);
        u.setToken("3");
        u.setPassword("pw");
        u.setUsername("un");

        List<User> compare = List.of(testUser, u);

        Lobby l = lobbyService.addUser(u, lobby.getAccessCode());
        assertEquals(l.getPlayers(), compare);

    }

    @ParameterizedTest
    @ValueSource(ints = { Integer.MIN_VALUE, -1000, -234, -3, 0, 1, 102, Integer.MAX_VALUE })
    public void addUser_failureWrongAccessCode(int accessCode) {
        assertThrows(ResponseStatusException.class, () -> lobbyService.addUser(testUser, accessCode));
    }

    @ParameterizedTest
    @ValueSource(ints = { Integer.MIN_VALUE, -1234, -126, -23, -5, 1, 34, 231, Integer.MAX_VALUE })
    public void addUser_failure(int lobbyID) {
        Lobby lobby = new Lobby(testUser, 5, 17345, 2);
        LobbyRepository.addLobby(lobby);

        User u = new User();
        u.setId(1L);
        u.setToken("3");
        u.setPassword("pw");
        u.setUsername("un");
        u.setLobbyID(lobbyID);

        assertThrows(ResponseStatusException.class, () -> lobbyService.addUser(u, 17345));
    }

    @Test
    public void deleteLobby_success() {
        Lobby l = new Lobby(testUser, 89, 12345, 4);
        LobbyRepository.addLobby(l);

        assertNotNull(lobbyService.getLobby(l.getId()));
        lobbyService.deleteLobby(l.getId());
        assertThrows(ResponseStatusException.class, () -> lobbyService.getLobby(l.getId()));
    }

    @ParameterizedTest
    @ValueSource(ints = { Integer.MIN_VALUE, -1234, -126, -23, -5, 0, 1, 34, 231, Integer.MAX_VALUE })
    public void deleteLobby_failureNonexisting(int lobbyId) {
        assertThrows(ResponseStatusException.class, () -> lobbyService.deleteLobby(lobbyId));
    }
}
