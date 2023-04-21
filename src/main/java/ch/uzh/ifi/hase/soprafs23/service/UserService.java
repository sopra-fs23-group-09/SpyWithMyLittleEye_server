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

import java.util.*;

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

    //called setToken in the class diagram
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

    public List<User> getTop100Users(){
        //userRepository.findAll(Sort.by("highScore"));
        return userRepository.findTop100ByOrderByHighScoreDesc();
    }

    //could be renamed to deleteToken as written in class diagram
    public void clearToken(String token){
        User u = userRepository.findByToken(token);
        u.setToken(null);
        userRepository.save(u);
        userRepository.flush();
    }
    //probably rename to logoutUser because of class diagram, but setOffline has a meaning in combination
    //with status so i would prefer setOffline
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
    //TODO: update the password and the profile picture
    //this method combines all the update [attribute] methods in the class diagram
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
    public List<User> getTop100User(){
        List<User> topUsers = userRepository.findTop100ByOrderByHighScoreDesc();
        return Collections.unmodifiableList(topUsers);
    }

    //class diagram says we should overload this method with parameter string, don't get the reason
    //annotated so we don't forget to check
    public User getUser(Long id){

        Optional<User> user = userRepository.findById(id); //note c: merge-conflict resolved in github, CHECK!
        if (user.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This user is doing no existing!");
        }
        return user.get();
    }

    //login of user is at the moment in usercontroller, probably implement this in userservice, would be
    //more beautiful. Also, there is a loginUser method in the class diagram for the userservice
    //maybe lower priority as it should work the way it is implemented at the moment

}
