package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.Lobby;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.rest.dto.LobbyGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.LobbyPostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.LobbyService;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
        float time = lobbyPostDTO.getTime();

        Lobby createdLobby = lobbyService.createLobby(host, amountRounds,time);

        return ResponseEntity.created(null).body(DTOMapper.INSTANCE.convertLobbyToLobbyGetDTO(createdLobby));
    }

    @PutMapping("/lobbies/join/{userId}") //userId probably not best API design
    public ResponseEntity<LobbyGetDTO> joinLobby(@PathVariable(value = "userId") Long userId, @RequestBody String accessCode, @RequestHeader(value = "token", defaultValue = "null") String token) {
        userService.checkToken(token);
        User user = userService.getUser(userId);
        Gson gson = new Gson();
        JsonObject jsonObject;
        try{jsonObject = gson.fromJson(accessCode, JsonObject.class);}
        catch(JsonSyntaxException e){throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Illegal body format");}
        JsonElement elem = jsonObject.get("accessCode");
        if(elem == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Illegal body content");
        int accessCodeInt = Integer.parseInt(elem.getAsString());
        Lobby lobby = lobbyService.addUser(user, accessCodeInt);
        return ResponseEntity.ok(DTOMapper.INSTANCE.convertLobbyToLobbyGetDTO(lobby));
    }

    @PutMapping("/lobbies/{lobbyId}/exit/{userId}")
    public ResponseEntity<Void> exitLobby(@PathVariable(value = "userId") Long userId,@PathVariable("lobbyId") int lobbyId, @RequestHeader(value = "Token", defaultValue = "null") String token){
        userService.checkToken(token);
        User player = userService.getUser(userId);
        lobbyService.removeUser(player,lobbyId);
        userService.exitLobby(player);
        return ResponseEntity.ok().build();
    }
}