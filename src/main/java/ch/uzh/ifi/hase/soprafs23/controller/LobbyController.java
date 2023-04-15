package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.Lobby;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.rest.dto.LobbyGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.LobbyPostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.LobbyService;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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

    // to-do: response is 201 (created) if Lobby is created, also check responses if smt wrong!
    @PutMapping("/lobbies/join/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseEntity<LobbyGetDTO> joinLobby(@PathVariable(value = "userId") Long userId, @RequestBody String accessCode, @RequestHeader(value = "token", defaultValue = "null") String token) {
        userService.checkToken(token);
        User user = userService.getUser(userId);
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(accessCode, JsonObject.class);
        int accessCodeInt = Integer.parseInt(jsonObject.get("accessCode").getAsString());
        Lobby lobby = lobbyService.addUser(user, accessCodeInt);
        return ResponseEntity.created(null).body(DTOMapper.INSTANCE.convertLobbyToLobbyGetDTO(lobby));
    } //vielleicht nicht status created sondern ok 200 als response

    @GetMapping("/lobbies/{lobbyId}/users")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<UserGetDTO> getAllUsersInLobby(@PathVariable(value = "lobbyId") int lobbyId, @RequestHeader(value = "token", defaultValue = "null") String token) {
        userService.checkToken(token);

        List<User> users = lobbyService.getUsersInLobby(lobbyId);
        List<UserGetDTO> userGetDTOs = new ArrayList<>();

        // convert each user to the API representation
        for (User user : users) {
            userGetDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
        }
        return userGetDTOs;
    }
}
