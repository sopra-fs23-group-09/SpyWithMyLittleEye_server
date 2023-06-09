package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Lobby;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.repository.LobbyRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Random;

@Service
@Transactional
public class LobbyService {
    private final Logger log = LoggerFactory.getLogger(LobbyService.class);
    private int newLobbyId;
    private final Random random;
    private final GameService gameService;

    @Autowired
    public LobbyService(GameService gameService) {
        this.newLobbyId = 1;
        this.random = new Random();
        this.gameService = gameService;
    }

    public void kickPlayer(Player player, WebSocketService ws, PlayerService pl) {
        log.info("Kicking player {}", player);
        int deleteLobbyOrGame = getLobby(player.getLobbyID()).kickPlayer(player, ws);
        if (deleteLobbyOrGame == 1){
            gameService.handleGameOver(player.getLobbyID(), false);
            deleteLobby(player.getLobbyID(), pl);
        }else if (deleteLobbyOrGame == 0){
            deleteLobby(player.getLobbyID(), pl);
        }
    }

    public Lobby createLobby(Player host, int amountRounds, float time) {
        // make sure that host is not in another lobby else throw error
        if (host.getLobbyID() != 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You can only play one game at a time.");
        }
        int accessCode = generateAccessCode();
        if (amountRounds < 1 || amountRounds > 20) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The maximum amount of rounds is 20 and can't be less than 1");
        }
        Lobby newLobby = new Lobby(host, newLobbyId, accessCode, amountRounds, time);
        LobbyRepository.addLobby(newLobby);

        log.info("Created information for Lobby: {}", newLobby);
        newLobbyId++;

        return newLobby;
    }

    public Game startGame(int lobbyId, PlayerService playerService) {
        Lobby lobby = LobbyRepository.getLobbyById(lobbyId);
        if (lobby == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Lobby doesn't exist.");
        Game game = lobby.initiateGame(playerService);
        if (game == null)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "game already started.");
        GameRepository.addGame(game);
        return game;
    }

    // generate a random (and unique) lobby access number between 10000 and 99999
    private int generateAccessCode() {
        int accessCode = 0;
        // generate random access code until it is unique
        do {
            // generate random number between 10000 and 99999
            accessCode = random.nextInt(89999) + 10000;
        } while (checkAccessCode(accessCode)); // if lobby with generated access code exists, generate another random
                                               // number

        return accessCode;
    }

    // checks if there is a lobby with given accessCode
    private boolean checkAccessCode(int accessCode) {
        return LobbyRepository.getLobbyByAccessCode(accessCode) != null;
    }

    public Lobby addUser(Player player, int accessCode) {
        // check if accessCode exists
        if (!checkAccessCode(accessCode)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The provided access code does not exist.");
        }

        // check if user is already in a lobby or in a game, if so throw error
        if (player.getLobbyID() != 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You can only play one game at a time.");
        }

        Lobby lobby = LobbyRepository.getLobbyByAccessCode(accessCode);
        if(lobby.gameStarted()){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "The game already started.");
        }
        // check if lobby is already full
        if (!lobby.addPlayer(player)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "The lobby is full.");
        }
        return lobby;
    }

    public int removeUser(Player player, int lobbyId){
        Lobby lobby = LobbyRepository.getLobbyById(lobbyId);
        // check if player is in lobby (and remove player) else throw exception
        int removeResult = lobby.removePlayer(player);
        if (removeResult == 2){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The player is not in this lobby.");
        }else if (removeResult == 0){
            LobbyRepository.deleteLobby(lobbyId);
            return 0;
        }
        return 1;
    }

    public void deleteLobby(int lobbyId, PlayerService playerService) {
        Lobby l = getLobby(lobbyId);
        List<Player> players = l.getPlayers();
        for (int i = 0; i < players.size(); i++) {
            players.get(i).setLobbyID(0);
            playerService.saveFlushUser(players.get(i));
        }
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
