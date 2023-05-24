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
 * the user (e.g., it creates, modifies, deletes, finds).
 * The result will be passed back to the caller.
 */
@Service
@Transactional
public class PlayerService {

    private final Logger log = LoggerFactory.getLogger(PlayerService.class);

    private final PlayerRepository playerRepository;

    private final LobbyService lobbyService;

    private final WebSocketService webSocketService;

    private final Map<Long, Timer> activePlayerTimers;

    private final Map<Long, Boolean> activePlayerBooleans;

    @Autowired
    public PlayerService(@Qualifier("playerRepository") PlayerRepository playerRepository, LobbyService lobbyService, WebSocketService ws) {
        this.playerRepository = playerRepository;
        this.lobbyService = lobbyService;
        this.webSocketService = ws;
        activePlayerTimers = new HashMap<>();
        activePlayerBooleans = new HashMap<>();
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

    private Player clearToken(String token){
        Player player = playerRepository.findByToken(token);
        if (player == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No user with this token exists");
        }
        player.setToken(null);
        log.info("clearing token of {}", player);
        player = playerRepository.save(player);
        playerRepository.flush();
        log.debug("token of user "+ player.getUsername()+": " + player.getToken());
        log.debug("user still in repo with that token?:"+ playerRepository.findByToken(token));
        return player;
    }

    public Player setOffline(String token, boolean status){
        Player player = playerRepository.findByToken(token);
        if (player == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No user");
        player.setStatus(status? PlayerStatus.OFFLINE: PlayerStatus.ONLINE);
        player = playerRepository.save(player);
        playerRepository.flush();

        if(!status) {
            activePlayerTimers.put(player.getId(), new Timer());
            activePlayerBooleans.put(player.getId(), false);
            log.info("Initializing keepalive timer for {}", player);
            activePlayerTimers.get(player.getId()).scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    Player keepalive = playerRepository.findByToken(token);
                    log.debug("Checking keepalive for {}", keepalive);
                    checkKeepalive(keepalive.getId());
                }
            }, 10_000, 10_000);
        }else{
            log.info("logging out user {}", player);
            activePlayerTimers.get(player.getId()).cancel();
            activePlayerTimers.remove(player.getId());
            activePlayerBooleans.remove(player.getId());
            player = clearToken(player.getToken());
        }
        return player;
    }

    private void checkKeepalive(long playerId) {
        if(activePlayerBooleans.get(playerId)){
            activePlayerBooleans.put(playerId, false);
        } else {
            Player player = getPlayer(playerId);
            activePlayerTimers.get(playerId).cancel();
            log.info("Removing {} due to inactivity", player);
            player = setOffline(player.getToken(), true);
            if(player.getLobbyID() != 0) {
                lobbyService.kickPlayer(player, webSocketService, this);
            }
            player.setLobbyID(0);
            saveFlushUser(player);
        }
    }

    public void keepAlive(String token) {
        log.debug("Keeping player with token {} alive", token);
        activePlayerBooleans.put(getPlayerID(token), true);
    }
    public List<Player> getPlayers() {
        return this.playerRepository.findAll();
    }
    public Long getPlayerID(String token){
        Player player = playerRepository.findByToken(token);
        if(player == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The user doesn't exist");
        }
        return player.getId();
    }

    //this method combines all the update [attribute] methods in the class diagram
    public void updatePlayer(Player player, String token, Long userId){
        Optional<Player> playerToUpdateO = playerRepository.findById(userId);
        if(playerToUpdateO.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The user doesn't exist!");
        }
        Player playerToUpdate = playerToUpdateO.get();
        if(!token.equals(playerToUpdate.getToken())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You can only change your own profile!");
        }
        if(player.getUsername() != null){
            checkIfUserExists(player);
            checkLengthOfName(player);
            playerToUpdate.setUsername(player.getUsername());
        }
        if(player.getPassword() != null){
            playerToUpdate.setPassword(player.getPassword());
        }
        if(player.getBirthday() != null){
            playerToUpdate.setBirthday(player.getBirthday());
        }
        if(player.getProfilePicture() != null){
            playerToUpdate.setProfilePicture(player.getProfilePicture());
        }
        playerRepository.save(playerToUpdate);
        playerRepository.flush();
    }

    public void exitLobby(Player player){
        if (player.getLobbyID() == 0){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You are not in a lobby yet.");
        }
        player.setLobbyID(0);
        playerRepository.save(player);
        playerRepository.flush();
    }

    public String generateUniqueToken(){
        // generate random token
        String token = UUID.randomUUID().toString();

        // generate random tokens until the token generated is unique for the player
        while (playerRepository.findByToken(token) != null){
            token = UUID.randomUUID().toString();
        }
        return token;
    }

    public Player createPlayer(Player newPlayer) {
        String token = generateUniqueToken();

        // set the properties for the new player and check if another player already has the same username
        checkIfUserExists(newPlayer);
        checkLengthOfName(newPlayer);
        log.info("createUser with name:" + newPlayer.getUsername());

        newPlayer.setToken(token);
        newPlayer.setStatus(PlayerStatus.ONLINE);
        newPlayer.setCreationDate(new Date());

        // saves the given entity but data is only persisted in the database once
        // flush() is called
        newPlayer = playerRepository.save(newPlayer);
        playerRepository.flush();

        newPlayer = setOffline(newPlayer.getToken(), false);

        log.debug("Created Information for Player: {}", newPlayer.getUsername());
        return newPlayer;
    }

    private void checkLengthOfName(Player newPlayer){
        if(newPlayer.getUsername().length() > 7){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "The username can have up to 7 characters. Choose another one!");
        }
    }

    private void checkIfUserExists(Player playerToBeCreated) {
        Player playerByUsername = playerRepository.findByUsername(playerToBeCreated.getUsername());
        // check if the username is already taken
        if (playerByUsername != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "The username provided is not unique. Please choose another one.");
        }
    }

    public List<Player> getTop15PlayersHighScore(){
        List<Player> topPlayers = playerRepository.findTop15ByOrderByHighScoreDesc();
        return Collections.unmodifiableList(topPlayers);
    }

    public List<Player> getTop15PlayersGamesWon() {
        List<Player> topPlayers = playerRepository.findTop15ByOrderByGamesWonDesc();
        return Collections.unmodifiableList(topPlayers);
    }

    public Player getPlayer(Long id){
        Optional<Player> player = playerRepository.findById(id);
        if (player.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This user does not exist.");
        }
        return player.get();
    }
}
