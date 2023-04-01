package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.entity.Lobby;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.LobbyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

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
        Lobby newLobby = new Lobby();
        newLobby.setAccessCode(generateAccessCode());
        addUser(host); //to-do: change method parameter => host must be added to lobby

        //save new lobby to database
        newLobby = lobbyRepository.save(newLobby);
        lobbyRepository.flush();

        log.debug("Created information for Lobby: {}", newLobby);
        // return newLobby;
    }

    //generate a random number between 10000 and 99999
    private int generateAccessCode(){
        Random random = new Random();
        int accessCode = 0;

        //generate random access code until it is unique
        boolean accessCodeIsUnique = false;
        while (!accessCodeIsUnique){
            //generate random number between 10000 and 99999
            accessCode = random.nextInt(89999) + 10000;

            Lobby lobbyByAccessCode = lobbyRepository.findByAccessCode(accessCode);
            if (lobbyByAccessCode != null){
                accessCodeIsUnique = true;
            }
        }

        return accessCode;
    }

    //class diagram says that this method takes an integer, thought user fits better
    public void addUser(User user){
        //TODO
        //verify that the user is not in another lobby already => throw error
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
