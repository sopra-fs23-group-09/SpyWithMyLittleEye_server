package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.LobbyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LobbyService {
    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final LobbyRepository lobbyRepository;

    @Autowired
    public LobbyService(@Qualifier("lobbyRepository") LobbyRepository lobbyRepository) {
        this.lobbyRepository = lobbyRepository;
    }

    public void createLobby(User host){
        //TODO
    }
    private int generateAccessCode(){
        //TODO
        return 0;
    }

    //class diagram says that this method takes an integer, thought user fits better
    public void addUser(User user){
        //TODO
    }
    //Accesscode in lobby is int, therefore i take int instead of String (which is written in class diagram
    //for this method
    private boolean checkAccessCode(int accessCode){
        //TODO
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
