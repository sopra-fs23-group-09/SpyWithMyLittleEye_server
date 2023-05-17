package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Lobby;
import ch.uzh.ifi.hase.soprafs23.stomp.dto.GameStarted;
import ch.uzh.ifi.hase.soprafs23.rest.dto.LobbyGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.LobbyService;
import ch.uzh.ifi.hase.soprafs23.service.PlayerService;
import ch.uzh.ifi.hase.soprafs23.service.WebSocketService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;


@Controller
public class LobbyStompController {
    private final LobbyService lobbyService;
    private final PlayerService playerService;
    private final WebSocketService webSocketService;

    LobbyStompController(LobbyService lobbyService, PlayerService playerService, WebSocketService webSocketService) {
        this.lobbyService = lobbyService;
        this.playerService = playerService;
        this.webSocketService = webSocketService;
    }

    @MessageMapping("/lobbies/{lobbyId}/joined")
    public void getLobbyInformation(@DestinationVariable("lobbyId") int lobbyId){
        Lobby lobby = lobbyService.getLobby(lobbyId);
        LobbyGetDTO lobbyGetDTO = DTOMapper.INSTANCE.convertLobbyToLobbyGetDTO(lobby);
        webSocketService.sendMessageToSubscribers("/topic/lobbies/" + lobbyId, lobbyGetDTO);
    }

    @MessageMapping("games/{lobbyId}")
    public void startGame(@DestinationVariable("lobbyId") int lobbyId){
        Game game = lobbyService.startGame(lobbyId, playerService);
        webSocketService.sendMessageToSubscribers("/topic/lobbies/" + lobbyId, new GameStarted(game.getId()));
    }
}
