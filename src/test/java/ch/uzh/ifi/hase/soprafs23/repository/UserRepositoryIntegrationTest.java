package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class UserRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void findByUsername_success() {
        // given
        User user = new User();
        user.setPassword("password");
        user.setUsername("firstname@lastname");
        user.setStatus(UserStatus.ONLINE);
        user.setToken("1");
        user.setCreationDate(new Date());

        entityManager.persist(user);
        entityManager.flush();

        // when
        User found = userRepository.findByUsername(user.getUsername());

        // then
        assertNotNull(found.getId());
        assertEquals(found.getPassword(), user.getPassword());
        assertEquals(found.getUsername(), user.getUsername());
        assertEquals(found.getToken(), user.getToken());
        assertEquals(found.getStatus(), user.getStatus());
        assertEquals(found.getCreationDate(), user.getCreationDate());
    }

    @Test
    public void findByToken_success() {
        // given
        User user = new User();
        user.setPassword("password");
        user.setUsername("firstname@lastname");
        user.setStatus(UserStatus.ONLINE);
        user.setToken("1");
        user.setCreationDate(new Date());

        entityManager.persist(user);
        entityManager.flush();

        // when
        User found = userRepository.findByToken(user.getToken());

        // then
        assertNotNull(found.getId());
        assertEquals(found.getPassword(), user.getPassword());
        assertEquals(found.getUsername(), user.getUsername());
        assertEquals(found.getToken(), user.getToken());
        assertEquals(found.getStatus(), user.getStatus());
        assertEquals(found.getCreationDate(), user.getCreationDate());
    }

    @Test
    public void getTop15Users(){
        User testUser = new User();
        testUser.setPassword("testPassword");
        testUser.setUsername("testUsername");
        testUser.setHighScore(10);
        testUser.setCreationDate(new Date());
        testUser.setStatus(UserStatus.ONLINE);
        testUser.setToken("1");

        User testUser2 = new User();
        testUser2.setPassword("testPassword2");
        testUser2.setUsername("testUsername2");
        testUser2.setHighScore(20);
        testUser2.setCreationDate(new Date());
        testUser2.setStatus(UserStatus.ONLINE);
        testUser2.setToken("2");

        User testUser3 = new User();
        testUser3.setPassword("testPassword3");
        testUser3.setUsername("testUsername3");
        testUser3.setHighScore(30);
        testUser3.setCreationDate(new Date());
        testUser3.setStatus(UserStatus.ONLINE);
        testUser3.setToken("3");

        entityManager.persist(testUser);
        entityManager.flush();
        entityManager.persist(testUser2);
        entityManager.flush();
        entityManager.persist(testUser3);
        entityManager.flush();

        List<User> users = userRepository.findTop15ByOrderByHighScoreDesc();
        assertEquals(users.get(0),testUser3);
        assertEquals(users.get(1),testUser2);
    }
}
