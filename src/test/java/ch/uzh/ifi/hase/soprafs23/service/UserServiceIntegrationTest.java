package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
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
 * @see UserService
 */
@WebAppConfiguration
@SpringBootTest
public class UserServiceIntegrationTest {

    @Qualifier("userRepository")
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();
    }

    @Test
    public void updateToken_success() {
        createUser_validInputs_success();

        userRepository.deleteAll();

        User testUser = new User();
        testUser.setPassword("testPassword");
        testUser.setUsername("testUsername");
        User created = userService.createUser(testUser);
        User updated = userService.updateToken(created.getId(), "2");

        assertEquals(created.getId(), updated.getId());
        assertEquals(created.getPassword(), updated.getPassword());
        assertEquals(created.getUsername(), updated.getUsername());
        assertEquals("2", updated.getToken());
        assertEquals(created.getCreationDate(), updated.getCreationDate());
        assertEquals(UserStatus.ONLINE, updated.getStatus());
    }

    @Test
    public void updateToken_failure() {
        assertThrows(EntityNotFoundException.class, () -> userService.updateToken(1L, "2"));
    }

    @Test
    public void checkToken_success() {
        User testUser = new User();
        testUser.setPassword("testPassword");
        testUser.setUsername("testUsername");
        User created = userService.createUser(testUser);

        assertDoesNotThrow(() -> userService.checkToken(created.getToken()));
    }

    @Test
    public void checkToken_failure() {
        assertThrows(ResponseStatusException.class, () -> userService.checkToken("1"));
    }

    @Test
    public void clearToken_success() {
        User testUser = new User();
        testUser.setPassword("testPassword");
        testUser.setUsername("testUsername");
        User created = userService.createUser(testUser);
        userService.clearToken(created.getToken());
        User cleared = userService.getUser(created.getId());

        assertEquals(null, cleared.getToken());
    }

    @Test
    public void clearToken_failure() {
        assertThrows(ResponseStatusException.class, () -> userService.clearToken("1"));
    }

    @Test
    public void setOffline_success() {
        User testUser = new User();
        testUser.setPassword("testPassword");
        testUser.setUsername("testUsername");
        User created = userService.createUser(testUser);

        userService.setOffline(created.getToken(), true);
        User updated = userService.getUser(created.getId());
        assertEquals(UserStatus.OFFLINE, updated.getStatus());

        userService.setOffline(created.getToken(), false);
        updated = userService.getUser(created.getId());
        assertEquals(UserStatus.ONLINE, updated.getStatus());
    }

    @Test
    public void setOffline_failure() {
        assertThrows(ResponseStatusException.class, () -> userService.setOffline("1", true));
    }

    @Test
    public void getUserID_success() {
        User testUser = new User();
        testUser.setPassword("testPassword");
        testUser.setUsername("testUsername");
        User created = userService.createUser(testUser);
        assertEquals(created.getId(), userService.getUserID(created.getToken()));
    }

    @Test
    public void getUserID_failure() {
        assertThrows(ResponseStatusException.class, () -> userService.getUserID("1"));
    }

    @Test
    public void updateUser_success() {
        User testUser = new User();
        testUser.setPassword("testPassword");
        testUser.setUsername("testUsername");
        User toUpdate = new User();
        toUpdate.setUsername("updatedName");
        toUpdate.setBirthday(new Date(0L));

        User created = userService.createUser(testUser);
        userService.updateUser(toUpdate, created.getToken(), created.getId());
        User updated = userService.getUser(created.getId());

        assertEquals(toUpdate.getUsername(), updated.getUsername());
        assertEquals(toUpdate.getBirthday(), updated.getBirthday());
    }

    @Test
    public void updateUser_failureToken() {
        User testUser = new User();
        testUser.setPassword("testPassword");
        testUser.setUsername("testUsername");
        User created = userService.createUser(testUser);

        assertThrows(ResponseStatusException.class, ()->userService.updateUser(created, "1", created.getId()));
    }
    @Test
    public void updateUser_failureId() {
        User testUser = new User();
        testUser.setPassword("testPassword");
        testUser.setUsername("testUsername");
        User created = userService.createUser(testUser);

        assertThrows(ResponseStatusException.class, ()->userService.updateUser(created, created.getToken(), -1L));
    }

    @Test
    public void createUser_validInputs_success() {
        // given
        assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setPassword("testPassword");
        testUser.setUsername("testUsername");

        // when
        User createdUser = userService.createUser(testUser);

        // then
        assertEquals(testUser.getId(), createdUser.getId());
        assertEquals(testUser.getPassword(), createdUser.getPassword());
        assertEquals(testUser.getUsername(), createdUser.getUsername());
        assertNotNull(createdUser.getToken());
        assertNotNull(createdUser.getCreationDate());
        assertEquals(UserStatus.ONLINE, createdUser.getStatus());
    }

    @Test
    public void createUser_duplicateUsername_throwsException() {
        assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setPassword("testName");
        testUser.setUsername("testUsername");
        User createdUser = userService.createUser(testUser);

        // attempt to create second user with same username
        User testUser2 = new User();

        // change the name but forget about the username
        testUser2.setPassword("testName2");
        testUser2.setUsername("testUsername");

        // check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser2));
    }

    @Test
    public void getTop15User_success() {
        assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setPassword("testPassword");
        testUser.setUsername("testUsername");
        User createdUser = userService.createUser(testUser);

        User top = userService.getTop15User().get(0);

        assertEquals(createdUser.getId(), top.getId());
        assertEquals(createdUser.getPassword(), top.getPassword());
        assertEquals(createdUser.getUsername(), top.getUsername());
        assertEquals(createdUser.getToken(), top.getToken());
        assertEquals(createdUser.getCreationDate(), top.getCreationDate());
        assertEquals(createdUser.getStatus(), top.getStatus());
    }

    @Test
    public void getUser_success() {
        User testUser = new User();
        testUser.setPassword("testName");
        testUser.setUsername("testUsername");
        User createdUser = userService.createUser(testUser);

        User returned = userService.getUser(createdUser.getId());
        assertEquals(createdUser.getId(), returned.getId());
        assertEquals(createdUser.getPassword(), returned.getPassword());
        assertEquals(createdUser.getUsername(), returned.getUsername());
        assertEquals(createdUser.getToken(), returned.getToken());
        assertEquals(createdUser.getCreationDate(), returned.getCreationDate());
        assertEquals(createdUser.getStatus(), returned.getStatus());
    }

    @Test
    public void getUser_failure() {
        assertThrows(ResponseStatusException.class, ()->userService.getUser(1L));
    }
}
