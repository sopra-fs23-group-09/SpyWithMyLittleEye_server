package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Lobby;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.LobbyRepository;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.ReadOnlyFileSystemException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LobbyServiceTest {
    @Mock
    private UserRepository userRepository;

    private User testUser;

    @InjectMocks
    private LobbyService lobbyService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // given
        testUser = new User();
        testUser.setId(1L);
        testUser.setToken("1");
        testUser.setPassword("testPassword");
        testUser.setUsername("testUsername");
        testUser.setHighScore(10);


        // when -> any object is being save in the userRepository -> return the dummy
        // testUser
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(testUser);
    }

    @Test
    public void createLobby_success(){
        Lobby created = lobbyService.createLobby(testUser, 2);

        assertEquals(created.getHostId(), testUser.getId());
        assertEquals(created.getAmountRounds(), 2);
    }
    @Test
    public void createLobby_failureAmountRounds(){
        assertThrows(ResponseStatusException.class, ()-> lobbyService.createLobby(testUser, 22));
    }
    @Test
    public void createLobby_failureHostInOtherLobby(){
        testUser.setLobbyID(1);
        assertThrows(ResponseStatusException.class, ()->lobbyService.createLobby(testUser, 2));
    }
    @Test
    public void startGame_success(){
        Lobby lobby = new Lobby(testUser, 2, 12345, 2);
        LobbyRepository.addLobby(lobby);

        User u = new User();
        u.setId(1L);
        u.setToken("3");
        u.setPassword("pw");
        u.setUsername("un");

        lobby.addPlayer(u);

        Game game = lobbyService.startGame(lobby.getId());

        assertEquals(lobby.getId(), game.getId());
        assertEquals(lobby.getAmountRounds(), game.getAmountRounds());
        assertEquals(lobby.getHostId(), game.getHostId());
    }
    @Test
    public void startGame_failureLobbyDoesntExist(){
        assertThrows(ResponseStatusException.class, ()-> lobbyService.startGame(100));
    }
    @Test
    public void startGame_failureGameAlreadyStarted(){
        Lobby lobby = new Lobby(testUser, 3, 12335, 2);
        LobbyRepository.addLobby(lobby);

        User u = new User();
        u.setId(1L);
        u.setToken("3");
        u.setPassword("pw");
        u.setUsername("un");

        lobby.addPlayer(u);

        lobbyService.startGame(lobby.getId());
        assertThrows(ResponseStatusException.class, ()-> lobbyService.startGame(lobby.getId()));
    }
    @Test
    public void addUser_success(){
        Lobby lobby = new Lobby(testUser, 4, 11345, 2);
        LobbyRepository.addLobby(lobby);

        User u = new User();
        u.setId(1L);
        u.setToken("3");
        u.setPassword("pw");
        u.setUsername("un");

        List<User> compare = new ArrayList<>();
        compare.add(testUser);
        compare.add(u);

        Lobby l = lobbyService.addUser(u, lobby.getAccessCode());
        assertEquals(l.getPlayers(), compare);

    }
    @Test
    public void addUser_failureWrongAccessCode(){
        User u = new User();
        u.setId(1L);
        u.setToken("3");
        u.setPassword("pw");
        u.setUsername("un");

        assertThrows(ResponseStatusException.class, ()->lobbyService.addUser(u, 79797));
    }
    @Test
    public void addUser_failure(){
        Lobby lobby = new Lobby(testUser, 5, 17345, 2);
        LobbyRepository.addLobby(lobby);

        User u = new User();
        u.setId(1L);
        u.setToken("3");
        u.setPassword("pw");
        u.setUsername("un");
        u.setLobbyID(8);

        assertThrows(ResponseStatusException.class, ()->lobbyService.addUser(u, 17345));
    }
    @Test
    public void
}
