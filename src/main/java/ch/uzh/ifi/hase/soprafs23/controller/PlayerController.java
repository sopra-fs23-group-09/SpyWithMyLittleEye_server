package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.rest.dto.PlayerGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.PlayerPostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.PlayerPutDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.PlayerService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@RestController
public class PlayerController {

    private final PlayerService playerService;

    PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    } //konstruktor

    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<PlayerGetDTO> getAllUsers(@RequestHeader(value = "Token", defaultValue = "null") String token) {
        playerService.checkToken(token); //is request allowed to ask for data

        List<Player> players = playerService.getUsers(); //fetch all players in the internal representation
        List<PlayerGetDTO> playerGetDTOS = new ArrayList<>();

        for (Player player : players) {
            playerGetDTOS.add(DTOMapper.INSTANCE.convertEntityToPlayerGetDTO(player)); // convert each player to the API representation
        }
        return playerGetDTOS;
    }

    @PostMapping("/users")
    public ResponseEntity<PlayerGetDTO> createUser(@RequestBody PlayerPostDTO playerPostDTO) {
        Player playerInput = DTOMapper.INSTANCE.convertPlayerPostDTOtoEntity(playerPostDTO); // convert API user to internal representation
        Player createdPlayer = playerService.createUser(playerInput); // create user

        HttpHeaders header = new HttpHeaders(); //make header to send newly created token
        header.set("Token", playerInput.getToken());
        header.set("Access-Control-Expose-Headers", "Token"); //allow client to access token from header

        return ResponseEntity.created(null).headers(header).body(DTOMapper.INSTANCE.convertEntityToPlayerGetDTO(createdPlayer));
    }
    @GetMapping("/users/{userId}")
    public ResponseEntity<PlayerGetDTO> getUser(@PathVariable(value = "userId") Long userId, @RequestHeader(value = "token", defaultValue = "null") String token) {
        playerService.checkToken(token);
        Long id = playerService.getUserID(token);
        HttpHeaders header = new HttpHeaders();
        header.set("ID", ""+id);
        header.set("Access-Control-Expose-Headers", "ID"); //allow client to access id (of the process that is asking for data, edit button)
        List<Player> players = playerService.getUsers();

        for (Player player : players) {
            if(player.getId().equals(userId)){
                return ResponseEntity.ok().headers(header).body(DTOMapper.INSTANCE.convertEntityToPlayerGetDTO(player));
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The user doesn't exist!");
    }
    @GetMapping("/users/login")
    public ResponseEntity<Void> loginCheck(@RequestParam("username") String username, @RequestParam("pass") String password) {
        List<Player> players = playerService.getUsers();
        for (Player player : players) {
            if(player.getUsername().equals(username) && player.getPassword().equals(password)) {
                player = playerService.updateToken(player.getId(),UUID.randomUUID().toString());
                playerService.setOffline(player.getToken(), false);
                HttpHeaders header = new HttpHeaders();
                header.set("Token", player.getToken());
                header.set("Id", player.getId().toString());
                header.set("Access-Control-Expose-Headers", "Token, Id");
                return ResponseEntity.ok().headers(header).build(); //only header, no body
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The credentials don't allow you to log in!");
    }
    @GetMapping("users/ranking")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<PlayerGetDTO> top15User(@RequestHeader(value = "Token", defaultValue = "null") String token){
        playerService.checkToken(token);

        List<Player> players =  playerService.getTop15User();
        List<PlayerGetDTO> playerGetDTOS = new ArrayList<>();

        for(Player u: players){
            playerGetDTOS.add(DTOMapper.INSTANCE.convertEntityToPlayerGetDTO(u));
        }

        return playerGetDTOS;
    }

    @GetMapping("users/rankingGamesWon")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<PlayerGetDTO> top15UserGamesWon(@RequestHeader(value = "Token", defaultValue = "null") String token){
        playerService.checkToken(token);

        List<Player> players =  playerService.getTop15UsersGamesWon();
        List<PlayerGetDTO> playerGetDTOS = new ArrayList<>();

        for(Player u: players){
            playerGetDTOS.add(DTOMapper.INSTANCE.convertEntityToPlayerGetDTO(u));
        }

        return playerGetDTOS;
    }

    @PutMapping("/users/logout")
    public ResponseEntity<Void> logout(@RequestHeader(value = "Token", defaultValue = "null") String token){
        playerService.checkToken(token);
        playerService.setOffline(token,true);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/users/{userId}")
    public ResponseEntity<Void> changeUser(@PathVariable(value = "userId") Long userId, @RequestBody PlayerPutDTO playerPutDTO, @RequestHeader(value = "token", defaultValue = "null") String token) {
        Player u = DTOMapper.INSTANCE.convertPlayerPutDTOtoEntity(playerPutDTO);
        playerService.checkToken(token);
        playerService.updateUser(u, token, userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/users/keepAlive")
    public ResponseEntity<Void> keepAlive(@RequestHeader(value = "token", defaultValue = "null") String token){
        //add token check ?
        //playerService.checkToken(token);
        playerService.keepAlive(token);
        return ResponseEntity.noContent().build();
    }
}
