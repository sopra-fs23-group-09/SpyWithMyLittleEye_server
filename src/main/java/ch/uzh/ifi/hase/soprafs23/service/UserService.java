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

    private final LobbyService lobbyService;

    private final WebSocketService webSocketService;

    private final Map<Long, Timer> activeUserTimers;

    private final Map<Long, Boolean> activeUserBooleans;

    @Autowired
    public UserService(@Qualifier("userRepository") UserRepository userRepository, LobbyService lobbyService, WebSocketService ws) {
        this.userRepository = userRepository;
        this.lobbyService = lobbyService;
        this.webSocketService = ws;
        activeUserTimers = new HashMap<>();
        activeUserBooleans = new HashMap<>();
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
    public void saveFlushUser(User u){
        userRepository.saveAndFlush(u);
    }

    //could be renamed to deleteToken as written in class diagram
    private void clearToken(String token){
        User u = userRepository.findByToken(token);
        if (u == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No user with this token exists");
        }
        u.setToken(null);
        userRepository.save(u);
        userRepository.flush();
    }
    //probably rename to logoutUser because of class diagram, but setOffline has a meaning in combination
    //with status so i would prefer setOffline
    public void setOffline(String token, boolean status){
        User u = userRepository.findByToken(token);
        if (u == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No user");
        u.setStatus(status?UserStatus.OFFLINE:UserStatus.ONLINE);
        userRepository.save(u);
        userRepository.flush();

        if(!status) {
            activeUserTimers.put(u.getId(), new Timer());
            activeUserBooleans.put(u.getId(), false);
            log.info("Initializing keepalive timer for {}", u.getUsername());
            activeUserTimers.get(u.getId()).scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    log.info("Checking keepalive for {}", u.getUsername());
                    checkKeepalive(u.getId());
                }
            }, 10_000, 10_000);
        }else{
            log.info("logging out user {} ", u.getUsername());
            activeUserTimers.get(u.getId()).cancel();
            activeUserTimers.remove(u.getId());
            activeUserBooleans.remove(u.getId());
            clearToken(u.getToken());
        }
    }

    private void checkKeepalive(long userId) {
        if(activeUserBooleans.get(userId)){
            activeUserBooleans.put(userId, false);
        } else {
            User u = getUser(userId);
            activeUserTimers.get(userId).cancel();
            log.info("Removing {} due to inactivity", u.getUsername());
            setOffline(u.getToken(), true);
            if(u.getLobbyID() != 0) {
                lobbyService.kickPlayer(u, webSocketService);
            }
        }
    }

    public void keepAlive(String token) {
        log.debug("Keeping user with token {} alive", token);
        activeUserBooleans.put(getUserID(token), true);
    }
    public List<User> getUsers() {
        return this.userRepository.findAll();
    }
    public Long getUserID(String token){
        User u = userRepository.findByToken(token);
        if(u == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The user doesn't exist");
        }
        return u.getId();
    }
    //TODO: update ((the password +))the profile picture for M4 (probably not the password but don't know yet)
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
        if(u.getProfilePicture() != null){
            uToUpdate.setProfilePicture(u.getProfilePicture());
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

        setOffline(newUser.getToken(), false);

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
    public List<User> getTop15User(){
        List<User> topUsers = userRepository.findTop15ByOrderByHighScoreDesc();
        return Collections.unmodifiableList(topUsers);
    }

    public List<User> getTop15UsersGamesWon() {
        List<User> topUsers = userRepository.findTop15ByOrderByGamesWonDesc();
        return Collections.unmodifiableList(topUsers);
    }

    public User getUser(Long id){

        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This user is doing no existing!");
        }
        return user.get();
    }

}
