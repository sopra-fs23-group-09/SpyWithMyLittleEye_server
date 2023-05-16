package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.PlayerStatus;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.repository.PlayerRepository;
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
 * Player Service
 * This class is the "worker" and responsible for all functionality related to
 * the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 */
@Service
@Transactional
public class PlayerService {

    private final Logger log = LoggerFactory.getLogger(PlayerService.class);

    private final PlayerRepository playerRepository;

    private final LobbyService lobbyService;

    private final WebSocketService webSocketService;

    private final Map<Long, Timer> activeUserTimers;

    private final Map<Long, Boolean> activeUserBooleans;

    @Autowired
    public PlayerService(@Qualifier("playerRepository") PlayerRepository playerRepository, LobbyService lobbyService, WebSocketService ws) {
        this.playerRepository = playerRepository;
        this.lobbyService = lobbyService;
        this.webSocketService = ws;
        activeUserTimers = new HashMap<>();
        activeUserBooleans = new HashMap<>();
    }

    //called setToken in the class diagram
    public Player updateToken(Long id, String token){
        Player player = playerRepository.getOne(id);
        player.setToken(token);
        player = playerRepository.save(player);
        playerRepository.flush();
        return player;
    }
    public void checkToken(String token){
        log.debug("checking token {}", token);
        Player p = playerRepository.findByToken(token);
        if("null".equals(token) || p == null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No permission to enter.");
        }
        log.debug("Found Player {}", p);
    }
    public void saveFlushUser(Player u){
        playerRepository.saveAndFlush(u);
    }

    //could be renamed to deleteToken as written in class diagram
    private Player clearToken(String token){
        Player u = playerRepository.findByToken(token);
        if (u == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No user with this token exists");
        }
        u.setToken(null);
        log.info("clearing token of {}", u);
        u = playerRepository.save(u);
        playerRepository.flush();
        log.debug("token of user "+ u.getUsername()+": " + u.getToken());
        log.debug("user still in repo with that token?:"+ playerRepository.findByToken(token));
        return u;
    }
    //probably rename to logoutUser because of class diagram, but setOffline has a meaning in combination
    //with status so i would prefer setOffline
    public Player setOffline(String token, boolean status){
        Player u = playerRepository.findByToken(token);
        if (u == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No user");
        u.setStatus(status? PlayerStatus.OFFLINE: PlayerStatus.ONLINE);
        u = playerRepository.save(u);
        playerRepository.flush();

        if(!status) {
            activeUserTimers.put(u.getId(), new Timer());
            activeUserBooleans.put(u.getId(), false);
            log.info("Initializing keepalive timer for {}", u);
            activeUserTimers.get(u.getId()).scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    Player keepalive = playerRepository.findByToken(token);
                    log.debug("Checking keepalive for {}", keepalive);
                    checkKeepalive(keepalive.getId());
                }
            }, 10_000, 10_000);
        }else{
            log.info("logging out user {}", u);
            activeUserTimers.get(u.getId()).cancel();
            activeUserTimers.remove(u.getId());
            activeUserBooleans.remove(u.getId());
            u = clearToken(u.getToken());
        }

        return u;
    }

    private void checkKeepalive(long userId) {
        if(activeUserBooleans.get(userId)){
            activeUserBooleans.put(userId, false);
        } else {
            Player u = getUser(userId);
            activeUserTimers.get(userId).cancel();
            log.info("Removing {} due to inactivity", u);
            u = setOffline(u.getToken(), true);
            if(u.getLobbyID() != 0) {
                lobbyService.kickPlayer(u, webSocketService, this);
            }
            u.setLobbyID(0);
            saveFlushUser(u);
        }
    }

    public void keepAlive(String token) {
        log.debug("Keeping user with token {} alive", token);
        activeUserBooleans.put(getUserID(token), true);
    }
    public List<Player> getUsers() {
        return this.playerRepository.findAll();
    }
    public Long getUserID(String token){
        Player u = playerRepository.findByToken(token);
        if(u == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The user doesn't exist");
        }
        return u.getId();
    }
    //TODO: update ((the password +))the profile picture for M4 (probably not the password but don't know yet)
    //this method combines all the update [attribute] methods in the class diagram
    public void updateUser(Player u, String token, Long userId){
        Optional<Player> uToUpdateO = playerRepository.findById(userId);
        if(uToUpdateO.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The user doesn't exist!");
        }
        Player uToUpdate = uToUpdateO.get();
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
        playerRepository.save(uToUpdate);
        playerRepository.flush();
    }

    public void exitLobby(Player player){
        if (player.getLobbyID() == 0){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "you are not in a lobby yet");
        }
        player.setLobbyID(0);
        playerRepository.save(player);
        playerRepository.flush();
    }

    public Player createUser(Player newPlayer) {
        newPlayer.setToken(UUID.randomUUID().toString());
        newPlayer.setStatus(PlayerStatus.ONLINE);
        newPlayer.setCreationDate(new Date());
        checkIfUserExists(newPlayer);
        // saves the given entity but data is only persisted in the database once
        // flush() is called
        newPlayer = playerRepository.save(newPlayer);
        playerRepository.flush();

        setOffline(newPlayer.getToken(), false);

        log.debug("Created Information for Player: {}", newPlayer);
        return newPlayer;
    }


    /**
     * This is a helper method that will check the uniqueness criteria of the
     * username defined in the Player entity. The method will do nothing if the input is unique
     * and throw an error otherwise.
     * @param playerToBeCreated
     * @throws org.springframework.web.server.ResponseStatusException
     * @see Player
     */
    private void checkIfUserExists(Player playerToBeCreated) {
        Player playerByUsername = playerRepository.findByUsername(playerToBeCreated.getUsername());
        if (playerByUsername != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "The username provided is not unique. Choose another one!");
        }
    }
    public List<Player> getTop15User(){
        List<Player> topPlayers = playerRepository.findTop15ByOrderByHighScoreDesc();
        return Collections.unmodifiableList(topPlayers);
    }

    public List<Player> getTop15UsersGamesWon() {
        List<Player> topPlayers = playerRepository.findTop15ByOrderByGamesWonDesc();
        return Collections.unmodifiableList(topPlayers);
    }

    public Player getUser(Long id){

        Optional<Player> user = playerRepository.findById(id);
        if (user.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This user is doing no existing!");
        }
        return user.get();
    }

}
