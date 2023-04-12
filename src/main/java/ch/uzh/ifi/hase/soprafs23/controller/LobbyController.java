package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.Lobby;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.rest.dto.LobbyGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.LobbyPostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.LobbyService;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class LobbyController {

    private final LobbyService lobbyService;
    private final UserService userService;

    LobbyController(LobbyService lobbyService, UserService userService) {
        this.lobbyService = lobbyService;
        this.userService = userService;
    }

    @PostMapping("/lobbies")
    public ResponseEntity<LobbyGetDTO> createLobby(@RequestHeader(value = "token", defaultValue = "null") String token, @RequestBody LobbyPostDTO lobbyPostDTO) {
        userService.checkToken(token);
        User host = userService.getUser(userService.getUserID(token));

        int amountRounds = lobbyPostDTO.getAmountRounds();

        Lobby createdLobby = lobbyService.createLobby(host, amountRounds);

        return ResponseEntity.created(null).body(DTOMapper.INSTANCE.convertLobbyToLobbyGetDTO(createdLobby));
    }

    @PutMapping("/lobbies/join/{userId}")
    public ResponseEntity<LobbyGetDTO> joinLobby(@PathVariable(value = "userId") Long userId, @RequestBody String accessCode, @RequestHeader(value = "token", defaultValue = "null") String token) {
        userService.checkToken(token);
        User user = userService.getUser(userId);
        Lobby lobby = lobbyService.addUser(user, Integer.parseInt(accessCode));
        return ResponseEntity.created(null).body(DTOMapper.INSTANCE.convertLobbyToLobbyGetDTO(lobby));
    }
}
