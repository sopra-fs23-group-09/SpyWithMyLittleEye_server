package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.Lobby;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.rest.dto.LobbyGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import ch.uzh.ifi.hase.soprafs23.service.LobbyService;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import ch.uzh.ifi.hase.soprafs23.stomp.dto.GuessIn;
import ch.uzh.ifi.hase.soprafs23.stomp.dto.GuessOut;
import ch.uzh.ifi.hase.soprafs23.stomp.dto.SpiedObjectIn;
import ch.uzh.ifi.hase.soprafs23.stomp.dto.SpiedObjectOut;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.util.HtmlUtils;

@Controller
public class LobbyStompController {
    private final LobbyService lobbyService;
    private final UserService userService;

    LobbyStompController(LobbyService lobbyService, UserService userService) {
        this.lobbyService = lobbyService;
        this.userService = userService;
    }

    @MessageMapping("lobbies/{lobbyId_from}/joined") // when someone sends to here
    @SendTo("/lobbies/{lobbyId_to}") // we send here to our subscribers
    public LobbyGetDTO getLobbyInformation(@DestinationVariable("lobbyId_to") int lobbyId_to, @PathVariable (value = "lobbyId_from") int lobbyId_from) throws Exception{
        //userService.checkToken(token);
        //User user = userService.getUser(userService.getUserID(token));
        lobbyId_to = lobbyId_from;
        Lobby lobby = lobbyService.getLobby(lobbyId_from);
        // TODO : Need to return list of users, Amount rounds, access code -> will be sent to everyone who subscribes to /lobbies/lobbyID
        return DTOMapper.INSTANCE.convertLobbyToLobbyGetDTO(lobby);
    }


}
