package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.Role;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Lobby;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.repository.LobbyRepository;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Random;

@Service
@Transactional
public class LobbyService {
    private final Logger log = LoggerFactory.getLogger(UserService.class);
    // note c: added UserRepository, to check whether user is already in lobby and then add the lobbyID to the user
    private final UserRepository userRepository;

    private int newLobbyId;

    @Autowired
    public LobbyService(@Qualifier("userRepository") UserRepository userRepository) {
        this.userRepository = userRepository;
        this.newLobbyId = 1;
    }

    public Lobby createLobby(User host, int amountRounds){
        // to-do: make sure that host is not in another lobby, else throw error
        int accessCode = generateAccessCode();
        if(amountRounds < 1 || amountRounds > 20){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The maximum amount of rounds is 20 and can't be less than 1");
        }
        Lobby newLobby = new Lobby(host, newLobbyId, accessCode, amountRounds);
        LobbyRepository.addLobby(newLobby);

        log.info("Created information for Lobby: {}", newLobby);
        newLobbyId++;

        return newLobby;
    }

    public Game startGame(int lobbyId){
        Lobby lobby = LobbyRepository.getLobbyById(lobbyId);
        Game game = lobby.play();
        GameRepository.addGame(game);
        return game;
    }

    //generate a random (and unique) lobby access number between 10000 and 99999
    private int generateAccessCode(){
        Random random = new Random();
        int accessCode = 0;
        //generate random access code until it is unique
        do {
            //generate random number between 10000 and 99999
            accessCode = random.nextInt(89999) + 10000;
        } while (checkAccessCode(accessCode)); // if lobby with generated access code exists, generate another random number

        return accessCode;
    }

    //checks if there is a lobby with given accessCode
    private boolean checkAccessCode(int accessCode){
        return LobbyRepository.getLobbyByAccessCode(accessCode) != null;
    }

    //note c: added accessCode as parameter: checkAccessCode is private method and find correct lobby to add user
    public Lobby addUser(User player, int accessCode){
        // check if accessCode exists
        if (!checkAccessCode(accessCode)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The provided access code does not exist.");
        }
        //probably add a check here for rejoin as additional feature for M4

        // check if user is already in a lobby or in a game, if so throw error
        if (player.getLobbyID() != 0){ // note c: after game ends, lobbyID and gameID have to be set to null again
            //to-do: ResponseStatusException for websocket (?!)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You can only play one game at a time.");
        }

        Lobby lobby = LobbyRepository.getLobbyByAccessCode(accessCode);

        // check if lobby is  already full
        if (!lobby.addPlayer(player)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The lobby is full.");
        }
        return lobby;
    }

    public void deleteLobby(int lobbyId){ //TODO: after the host ends the game
        //also need to delete the lobbyId of all players in this method, so they can join a new lobby
        LobbyRepository.deleteLobby(lobbyId);
    }

    public Lobby getLobby(int lobbyId) {
        Lobby lobby = LobbyRepository.getLobbyById(lobbyId);
        if (lobby == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This lobby doesn't exist.");
        }
        return lobby;
    }

}
