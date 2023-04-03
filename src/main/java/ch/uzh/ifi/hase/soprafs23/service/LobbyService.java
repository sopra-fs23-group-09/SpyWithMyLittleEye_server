package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.entity.Lobby;
import ch.uzh.ifi.hase.soprafs23.entity.User;
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

    private final LobbyRepository lobbyRepository;
    // note c: added UserRepository, to check whether user is already in lobby and then add the lobbyID to the user
    private final UserRepository userRepository;

    @Autowired
    public LobbyService(@Qualifier("lobbyRepository") LobbyRepository lobbyRepository, UserRepository userRepository) {
        this.lobbyRepository = lobbyRepository;
        this.userRepository = userRepository;
    }

    public void createLobby(User host){
        Lobby newLobby = new Lobby();
        int accessCode = generateAccessCode();
        newLobby.setAccessCode(accessCode);
        addUser(host, accessCode);

        //save new lobby to database
        newLobby = lobbyRepository.save(newLobby);
        lobbyRepository.flush();

        log.debug("Created information for Lobby: {}", newLobby);
        // return newLobby; //to-do: should a Lobby object be returned?
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

    //class diagram says that this method takes an integer, thought user fits better
    //note c: added accessCode as parameter: checkAccessCode is private method and find correct lobby to add user
    public void addUser(User user, int accessCode){

        // check if accessCode exists
        if (!checkAccessCode(accessCode)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "provided access code does not exist");
        }

        // check if user is already in a lobby or in a game, if so throw error
        if (user.getLobbyID() != null || user.getGameID() != null){ // note c: after game ends, lobbyID and gameID have to be set to null again
            //to-do: ResponseStatusException for websocket (?!)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "you can only play one game at a time");
        }

        Lobby lobby = lobbyRepository.findByAccessCode(accessCode);

        // check if lobby is  already full
        if (lobby.getFull()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "lobby is full");
        }

        // change User.LobbyID and add user to the lobby
        user.setLobbyID(lobby.getId());
        lobby.addUser(user);
    }

    //Access code in lobby is int, therefore i take int instead of String (which is written in class diagram
    //for this method
    private boolean checkAccessCode(int accessCode){
        Lobby lobbyByAccessCode = lobbyRepository.findByAccessCode(accessCode);
        if (lobbyByAccessCode != null){
            return true;
        }
        return false;
    }

    public List<User> getUsersInLobby(){
        //TODO
        return null;
    }

    public void deleteLobby(int lobbyId){
        //TODO
    }
}
