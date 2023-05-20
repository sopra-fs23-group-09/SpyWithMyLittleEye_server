package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.Lobby;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.rest.dto.LobbyGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.LobbyPostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.LobbyService;
import ch.uzh.ifi.hase.soprafs23.service.PlayerService;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@RestController
public class LobbyController {

    private final LobbyService lobbyService;
    private final PlayerService playerService;
    private final SimpMessagingTemplate messagingTemplate;

    LobbyController(LobbyService lobbyService, PlayerService playerService, SimpMessagingTemplate messagingTemplate) {
        this.lobbyService = lobbyService;
        this.playerService = playerService;
        this.messagingTemplate = messagingTemplate;
    }

    @PostMapping("/lobbies")
    public ResponseEntity<LobbyGetDTO> createLobby(@RequestHeader(value = "token", defaultValue = "null") String token, @RequestBody LobbyPostDTO lobbyPostDTO) {
        playerService.checkToken(token);
        Player host = playerService.getPlayer(playerService.getPlayerID(token));

        int amountRounds = lobbyPostDTO.getAmountRounds();
        float time = lobbyPostDTO.getTime();

        Lobby createdLobby = lobbyService.createLobby(host, amountRounds, time);

        return ResponseEntity.created(null).body(DTOMapper.INSTANCE.convertLobbyToLobbyGetDTO(createdLobby));
    }

    @PutMapping("/lobbies/join/{userId}")
    public ResponseEntity<LobbyGetDTO> joinLobby(@PathVariable(value = "userId") Long playerId, @RequestBody String accessCode, @RequestHeader(value = "token", defaultValue = "null") String token) {
        playerService.checkToken(token);
        Player player = playerService.getPlayer(playerId);
        Gson gson = new Gson();
        JsonObject jsonObject;
        try{jsonObject = gson.fromJson(accessCode, JsonObject.class);}
        catch(JsonSyntaxException e){throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Illegal body format");}
        JsonElement elem = jsonObject.get("accessCode");
        if(elem == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Illegal body content");
        int accessCodeInt = Integer.parseInt(elem.getAsString());
        Lobby lobby = lobbyService.addUser(player, accessCodeInt);
        return ResponseEntity.ok(DTOMapper.INSTANCE.convertLobbyToLobbyGetDTO(lobby));
    }

    @PutMapping("/lobbies/{lobbyId}/exit/{userId}")
    public ResponseEntity<Void> exitLobby(@PathVariable(value = "userId") Long playerId, @PathVariable("lobbyId") int lobbyId, @RequestHeader(value = "Token", defaultValue = "null") String token){
        playerService.checkToken(token);
        Player player = playerService.getPlayer(playerId);
        int exitResult = lobbyService.removeUser(player,lobbyId);
        playerService.exitLobby(player);
        if (exitResult == 1){
            // send a message over websocket to notify the other players that someone left
            String destination = "/topic/lobbies/" + lobbyId;
            Lobby lobby = lobbyService.getLobby(lobbyId);
            LobbyGetDTO lobbyGetDTO = DTOMapper.INSTANCE.convertLobbyToLobbyGetDTO(lobby);
            messagingTemplate.convertAndSend(destination, lobbyGetDTO);
        }
        return ResponseEntity.ok().build();
    }
}