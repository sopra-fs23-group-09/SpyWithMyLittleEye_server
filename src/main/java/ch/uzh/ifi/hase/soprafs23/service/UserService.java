package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to
 * the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 */
@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    @Autowired
    public UserService(@Qualifier("userRepository") UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public User updateToken(Long id, String token){
        User user = userRepository.getOne(id);
        user.setToken(token);
        user = userRepository.save(user);
        userRepository.flush();
        return user;
    }
    public void checkToken(String token){
        if("null".equals(token) || userRepository.findByToken(token) == null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No permission to enter.");
        }
    }
    public void clearToken(String token){
        User u = userRepository.findByToken(token);
        u.setToken(null);
        userRepository.save(u);
        userRepository.flush();
    }
    public void setOffline(String token, boolean status){
        User u = userRepository.findByToken(token);
        u.setStatus(status?UserStatus.OFFLINE:UserStatus.ONLINE);
        userRepository.save(u);
        userRepository.flush();
    }
    public List<User> getUsers() {
        return this.userRepository.findAll();
    }
    public Long getUserID(String token){
        User u = userRepository.findByToken(token);
        Long id = u.getId();
        return id;
    }
    public void updateUser(User u, String token, Long userId){
        Optional<User> uToUpdateO = userRepository.findById(userId);
        if(uToUpdateO.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The user doesn't exist!");
        }
        User uToUpdate = uToUpdateO.get();
        if(!token.equals(uToUpdate.getToken())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You can only change your own profile!");
        }
        if(u.getUsername() != null){
            checkIfUserExists(u);
            uToUpdate.setUsername(u.getUsername());
        }
        if(u.getBirthday() != null){
            uToUpdate.setBirthday(u.getBirthday());
        }
        userRepository.save(uToUpdate);
        userRepository.flush();
    }

    public User createUser(User newUser) {
        newUser.setToken(UUID.randomUUID().toString());
        newUser.setStatus(UserStatus.ONLINE);
        newUser.setCreationDate(new Date());
        checkIfUserExists(newUser);
        // saves the given entity but data is only persisted in the database once
        // flush() is called
        newUser = userRepository.save(newUser);
        userRepository.flush();

        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }


    /**
     * This is a helper method that will check the uniqueness criteria of the
     * username defined in the User entity. The method will do nothing if the input is unique
     * and throw an error otherwise.
     * @param userToBeCreated
     * @throws org.springframework.web.server.ResponseStatusException
     * @see User
     */
    private void checkIfUserExists(User userToBeCreated) {
        User userByUsername = userRepository.findByUsername(userToBeCreated.getUsername());
        if (userByUsername != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "The username provided is not unique. Choose another one!");
        }
    }
}
