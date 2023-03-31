package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * User Controller
 * This class is responsible for handling all REST request that are related to
 * the user.
 * The controller will receive the request and delegate the execution to the
 * UserService and finally return the result.
 */
@RestController
public class UserController {

    private final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    } //konstruktor

    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<UserGetDTO> getAllUsers(@RequestHeader(value = "Token", defaultValue = "null") String token) {
        userService.checkToken(token); //is request allowed to ask for data

        List<User> users = userService.getUsers(); //fetch all users in the internal representation
        List<UserGetDTO> userGetDTOs = new ArrayList<>();

        for (User user : users) {
            userGetDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user)); // convert each user to the API representation
        }
        return userGetDTOs;
    }

    @PostMapping("/users")
    public ResponseEntity<UserGetDTO> createUser(@RequestBody UserPostDTO userPostDTO) {
        User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO); // convert API user to internal representation
        User createdUser = userService.createUser(userInput); // create user

        HttpHeaders header = new HttpHeaders(); //make header to send newly created token
        header.set("Token", userInput.getToken());
        header.set("Access-Control-Expose-Headers", "Token"); //allow client to access token from header

        return ResponseEntity.created(null).headers(header).body(DTOMapper.INSTANCE.convertEntityToUserGetDTO(createdUser));
    }
    @GetMapping("/users/{userId}")
    public ResponseEntity<UserGetDTO> getUser(@PathVariable(value = "userId") Long userId, @RequestHeader(value = "token", defaultValue = "null") String token) {//TODO: token check
        userService.checkToken(token);
        Long id = userService.getUserID(token);
        HttpHeaders header = new HttpHeaders();
        header.set("ID", ""+id);
        header.set("Access-Control-Expose-Headers", "ID"); //allow client to access id (of the process that is asking for data, edit button)
        List<User> users = userService.getUsers();

        for (User user : users) {
            if(user.getId() == userId) {
                return ResponseEntity.ok().headers(header).body(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The user doesn't exist!");
    }
    @GetMapping("/v1/loginChecker")
    public ResponseEntity<Void> loginCheck(@RequestParam("username") String username, @RequestParam("pass") String password) {
        List<User> users = userService.getUsers();
        for (User user : users) {
            if(user.getUsername().equals(username) && user.getPassword().equals(password)) {
                user = userService.updateToken(user.getId(),UUID.randomUUID().toString());
                userService.setOffline(user.getToken(), false);
                HttpHeaders header = new HttpHeaders();
                header.set("Token", user.getToken());
                header.set("Id", user.getId().toString());
                header.set("Access-Control-Expose-Headers", "Token, Id");
                return ResponseEntity.ok().headers(header).build(); //only header, no body
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The credentials don't allow you to log in!");
    }

    @PutMapping("/v1/logoutService")
    public ResponseEntity<Void> logout(@RequestHeader(value = "Token", defaultValue = "null") String token){
        userService.checkToken(token);
        userService.setOffline(token,true);
        userService.clearToken(token);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/users/{userId}")
    public ResponseEntity<Void> changeUser(@PathVariable(value = "userId") Long userId, @RequestBody UserPutDTO userPutDTO, @RequestHeader(value = "token", defaultValue = "null") String token) {
        User u = DTOMapper.INSTANCE.convertUserPutDTOtoEntity(userPutDTO);
        userService.checkToken(token);
        userService.updateUser(u, token, userId);
        return ResponseEntity.noContent().build();
    }
}
