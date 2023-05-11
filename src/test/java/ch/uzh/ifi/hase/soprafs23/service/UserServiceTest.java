package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
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

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;


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
    public void updateToken_success() {
        Mockito.when(userRepository.getOne(Mockito.anyLong())).thenReturn(testUser);

        User updated = userService.updateToken(testUser.getId(), "2");
        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any());

        assertEquals(testUser.getId(), updated.getId());
        assertEquals(testUser.getPassword(), updated.getPassword());
        assertEquals(testUser.getUsername(), updated.getUsername());
    }

    @Test
    public void checkToken_failure() {
        Mockito.when(userRepository.findByToken(Mockito.anyString())).thenReturn(null);

        assertThrows(ResponseStatusException.class, () -> userService.checkToken(testUser.getToken()));
    }

    @Test
    public void checkToken_success() {
        Mockito.when(userRepository.findByToken(Mockito.anyString())).thenReturn(testUser);

        assertDoesNotThrow(() -> userService.checkToken(testUser.getToken()));
    }


    @Test
    public void setOffline() {
        Mockito.when(userRepository.findByToken(Mockito.anyString())).thenReturn(testUser);
        userService.setOffline("abc", false);
        userService.setOffline(testUser.getToken(), true);
        Mockito.verify(userRepository, Mockito.atLeast(1)).save(Mockito.any());
    }

    @Test
    public void getUsers_success() {
        Mockito.when(userRepository.findAll()).thenReturn(List.of(testUser));

        User createdUser = userService.getUsers().get(0);
        assertEquals(testUser.getId(), createdUser.getId());
        assertEquals(testUser.getPassword(), createdUser.getPassword());
        assertEquals(testUser.getUsername(), createdUser.getUsername());
    }

    @Test
    public void getUserId_failure() {
        Mockito.when(userRepository.findByToken(Mockito.anyString())).thenReturn(null);

        assertThrows(ResponseStatusException.class, () -> userService.getUserID(testUser.getToken()));
    }

    @Test
    public void getUserID_success() {
        Mockito.when(userRepository.findByToken(Mockito.anyString())).thenReturn(testUser);

        assertEquals(testUser.getId(), userService.getUserID(testUser.getToken()));
    }

    @Test
    public void updateUser_invalidToken() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(testUser));

        assertThrows(ResponseStatusException.class, () -> userService.updateUser(testUser, "-1", testUser.getId()));
    }
    @Test
    public void updateUser_missingUser() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> userService.updateUser(testUser, testUser.getToken(), testUser.getId()));
    }

    @Test
    public void updateUser_success() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(testUser));

        userService.updateUser(testUser, testUser.getToken(), testUser.getId());
        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    public void createUser_validInputs_success() {
        Mockito.when(userRepository.findByToken(Mockito.anyString())).thenReturn(testUser);

        // when -> any object is being save in the userRepository -> return the dummy
        // testUser
        User createdUser = userService.createUser(testUser);

        // then
        Mockito.verify(userRepository, Mockito.times(2)).save(Mockito.any());

        assertEquals(testUser.getId(), createdUser.getId());
        assertEquals(testUser.getPassword(), createdUser.getPassword());
        assertEquals(testUser.getUsername(), createdUser.getUsername());
        assertNotNull(createdUser.getCreationDate());
        assertNotNull(createdUser.getToken());
        assertEquals(UserStatus.ONLINE, createdUser.getStatus());
    }

    @Test
    public void createUser_duplicateUsername_throwsException() {
        Mockito.when(userRepository.findByToken(Mockito.anyString())).thenReturn(testUser);

        // given -> a first user has already been created
        userService.createUser(testUser);

        // when -> setup additional mocks for UserRepository
        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

        // then -> attempt to create second user with same user -> check that an error
        // is thrown
        assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
    }

    @Test
    public void getTop15User() {
        Mockito.when(userRepository.findTop15ByOrderByHighScoreDesc()).thenReturn(Collections.unmodifiableList(List.of(testUser)));

        User returned = userService.getTop15User().get(0);
        assertEquals(testUser.getId(), returned.getId());
        assertEquals(testUser.getPassword(), returned.getPassword());
        assertEquals(testUser.getUsername(), returned.getUsername());
    }

    @Test
    public void getUser_exists() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(testUser));

        User returned = userService.getUser(testUser.getId());
        assertEquals(testUser.getId(), returned.getId());
        assertEquals(testUser.getPassword(), returned.getPassword());
        assertEquals(testUser.getUsername(), returned.getUsername());
    }

    @Test
    public void getUser_notFound() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> userService.getUser(testUser.getId()));
    }
}
