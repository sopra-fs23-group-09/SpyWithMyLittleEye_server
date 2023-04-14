package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.Lobby;
import ch.uzh.ifi.hase.soprafs23.rest.dto.LobbyGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.LobbyService;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import ch.uzh.ifi.hase.soprafs23.service.WebSocketService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
//import com.solidfire.gson.Gson;


@Controller
public class LobbyStompController {
    private final LobbyService lobbyService;
    private final UserService userService;
    private final WebSocketService webSocketService;


    LobbyStompController(LobbyService lobbyService, UserService userService, WebSocketService webSocketService) {
        this.lobbyService = lobbyService;
        this.userService = userService;
        this.webSocketService = webSocketService;
    }

    @MessageMapping("/lobbies/{lobbyId}/joined") // when someone sends to here
    @SendTo("/game/lobbies/{lobbyId}") // we send here to our subscribers
    @SubscribeMapping("/game/lobbies/{lobbyId}")
    public void getLobbyInformation(@DestinationVariable("lobbyId") String lobbyId){ //TODO: n: why is lobbyId a string?
        Lobby lobby = lobbyService.getLobby(Integer.parseInt(lobbyId));
        LobbyGetDTO lobbyGetDTO = DTOMapper.INSTANCE.convertLobbyToLobbyGetDTO(lobby);
        // TODO if lobby null throw error
        // TODO : Need to return list of users, Amount rounds, access code -> will be sent to everyone who subscribes to /lobbies/lobbyID
        //return new Gson().toJson(lobbyGetDTO);
        webSocketService.sendMessageToSubscribers("/game/lobbies/" + lobbyId, lobbyGetDTO);
    }

    @MessageMapping("games/{lobbyId}")
    @SendTo("game/lobbies/{lobbyId}")
    public void startGame(@DestinationVariable("lobbyId") String lobbyId){  //TODO: n: why is lobbyId a string?
        lobbyService.startGame(Integer.parseInt(lobbyId));
        webSocketService.sendMessageToSubscribers("/game/lobbies/" + lobbyId, true);
        //note n: true einfach so returnen ist natürlich nicht korrekt, schaue es mir noch an
    }


}
