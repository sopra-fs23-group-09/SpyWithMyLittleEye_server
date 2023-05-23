package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.constant.PlayerStatus;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.rest.dto.PlayerPostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.PlayerPutDTO;
import ch.uzh.ifi.hase.soprafs23.service.PlayerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * PlayerControllerTest
 * This is a WebMvcTest which allows to test the PlayerController i.e. GET/POST
 * request without actually sending them over the network.
 * This tests if the PlayerController works.
 */
@WebMvcTest(PlayerController.class)
public class PlayerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlayerService playerService;

    private Player player1, player2;
    List<Player> players;
    private String username1, username2, password1, password2, token1, token2;
    private int highScore1, highScore2, gamesWon1, gamesWon2;
    private long id1, id2;

    @BeforeEach
    void setup() {
        //player1
        username1 = "player1";
        password1 = "123";
        token1 = "token1";
        id1 = 1L;
        highScore1 = 100;
        gamesWon1 = 5;

        player1 = new Player();
        player1.setId(id1);
        player1.setUsername(username1);
        player1.setPassword(password1);
        player1.setToken(token1);
        player1.setStatus(PlayerStatus.ONLINE);
        player1.setCreationDate(new Date(0L));
        player1.setGamesWon(gamesWon1);
        player1.setHighScore(highScore1);

        //player2
        username2 = "player2";
        password2 = "123";
        token2 = "token2";
        id2 = 2L;
        highScore2 = 200;
        gamesWon2 = 2;

        player2 = new Player();
        player2.setId(id2);
        player2.setUsername(username2);
        player2.setPassword(password2);
        player2.setToken(token2);
        player2.setStatus(PlayerStatus.ONLINE);
        player2.setCreationDate(new Date(0L));
        player2.setGamesWon(gamesWon2);
        player2.setHighScore(highScore2);

        players = new ArrayList<>();
        players.add(player1);
        players.add(player2);

        Mockito.reset(playerService);
    }

    @Test
    public void login_success() throws Exception {
        PlayerPostDTO playerPostDTO = new PlayerPostDTO();
        playerPostDTO.setUsername(username1);
        playerPostDTO.setPassword(password1);

        given(playerService.getPlayers()).willReturn(players);
        given(playerService.generateUniqueToken()).willReturn(token1);
        given(playerService.updateToken(anyLong(), anyString())).willReturn(player1);
        given(playerService.setOffline(anyString(), anyBoolean())).willReturn(player1);

        SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
        String creationD = dFormat.format(player1.getCreationDate());

        MockHttpServletRequestBuilder request = get("/users/login")
                .param("username", username1)
                .param("pass", password1);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(header().string("Token", token1))
                .andExpect(header().string("Id", Long.toString(id1)));
    }

    @Test
    public void login_wrongUsername() throws Exception{
        given(playerService.getPlayers()).willReturn(Collections.singletonList(player1));
        given(playerService.generateUniqueToken()).willReturn(token1);
        given(playerService.updateToken(anyLong(), anyString())).willReturn(player1);
        given(playerService.setOffline(anyString(), anyBoolean())).willReturn(player1);

        MockHttpServletRequestBuilder request = get("/users/login")
                .param("username", "invalidUsername")
                .param("pass", password1)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(result -> assertTrue(result.getResolvedException().getMessage().contains("The credentials don't allow you to log in!")));
    }

    @Test
    public void login_wrongPassword() throws Exception{
        given(playerService.getPlayers()).willReturn(Collections.singletonList(player1));
        given(playerService.generateUniqueToken()).willReturn(token1);
        given(playerService.updateToken(anyLong(), anyString())).willReturn(player1);
        given(playerService.setOffline(anyString(), anyBoolean())).willReturn(player1);

        MockHttpServletRequestBuilder request = get("/users/login")
                .param("username", username1)
                .param("pass", "invalidPassword")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(result -> assertTrue(result.getResolvedException().getMessage().contains("The credentials don't allow you to log in!")));
    }

    @Test
    public void logout_success() throws Exception {
        Mockito.doNothing().when(playerService).checkToken(anyString());
        given(playerService.setOffline(anyString(), anyBoolean())).willReturn(player1);


        MockHttpServletRequestBuilder request = put("/users/logout")
                .header("Token", token1);

        mockMvc.perform(request)
                .andExpect(status().isNoContent());
    }


    @Test
    public void givenUsers_whenGetUsers_thenReturnJsonArray() throws Exception {
        given(playerService.getPlayers()).willReturn(players);

        MockHttpServletRequestBuilder getRequest = get("/users").contentType(MediaType.APPLICATION_JSON);

        SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
        String creationD = dFormat.format(player1.getCreationDate());

        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].username", is(player1.getUsername())))
                .andExpect(jsonPath("$[0].status", is(player1.getStatus().toString())))
                .andExpect(jsonPath("$[0].creationDate", is(creationD)))
                .andExpect(jsonPath("$[0].id", is(player1.getId().intValue())));
    }

    @Test
    public void createUser_validInput_userCreated() throws Exception {
        PlayerPostDTO playerPostDTO = new PlayerPostDTO();
        playerPostDTO.setUsername(username1);
        playerPostDTO.setPassword(password1);

        given(playerService.createPlayer(Mockito.any())).willReturn(player1);

        MockHttpServletRequestBuilder postRequest = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(playerPostDTO));

        SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
        String creationD = dFormat.format(player1.getCreationDate());

        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(player1.getId().intValue())))
                .andExpect(jsonPath("$.username", is(player1.getUsername())))
                .andExpect(jsonPath("$.status", is(player1.getStatus().toString())))
                .andExpect(jsonPath("$.creationDate", is(creationD)))
                .andExpect(jsonPath("$.birthday").value(IsNull.nullValue()));
    }

    @Test
    public void createUser_duplicateUsername_conflict() throws Exception{
        String errorMessage = "Reason";
        ResponseStatusException e = new ResponseStatusException(HttpStatus.CONFLICT, errorMessage);
        given(playerService.createPlayer(Mockito.any())).willThrow(e);

        PlayerPostDTO playerPostDTO = new PlayerPostDTO();
        playerPostDTO.setUsername(username1);
        playerPostDTO.setPassword(password1);


        MockHttpServletRequestBuilder postRequest = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString((playerPostDTO)));

        mockMvc.perform(postRequest)
                .andExpect(status().isConflict())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(result -> assertTrue(result.getResolvedException().getMessage().contains(errorMessage)));
    }

    @Test
    public void editUser_success() throws Exception {
        PlayerPutDTO playerPutDTO = new PlayerPutDTO();
        playerPutDTO.setUsername("newName");
        playerPutDTO.setBirthday(new Date(0L));
        playerPutDTO.setPassword("newPassword");
        playerPutDTO.setProfilePicture("newPicture");

        //set up mock service
        doNothing().when(playerService).updatePlayer(Mockito.any(), Mockito.anyString(), anyLong());

        MockHttpServletRequestBuilder putRequest = put("/users/"+ id1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(playerPutDTO));

        mockMvc.perform(putRequest).andExpect(status().isNoContent());
    }

    @Test
    public void editUser_notFound() throws Exception{
        String errorMessage = "Reason";
        ResponseStatusException e = new ResponseStatusException(HttpStatus.NOT_FOUND, errorMessage);
        doThrow(e).when(playerService).updatePlayer(Mockito.any(), Mockito.anyString(), anyLong());

        PlayerPutDTO playerPutDTO = new PlayerPutDTO();
        playerPutDTO.setBirthday(new Date(0L));
        playerPutDTO.setUsername("newName");

        MockHttpServletRequestBuilder putRequest = put("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(playerPutDTO));

        mockMvc.perform(putRequest)
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(result -> assertTrue(result.getResolvedException().getMessage().contains(errorMessage)));
    }

    @Test
    public void getUser_withId_success() throws Exception{

        List<Player> allPlayers = Collections.singletonList(player1);
        given(playerService.getPlayers()).willReturn(allPlayers);

        MockHttpServletRequestBuilder getRequest = get("/users/"+ player1.getId());

        SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
        String creationD = dFormat.format(player1.getCreationDate());

        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(player1.getId().intValue())))
                .andExpect(jsonPath("$.username", is(player1.getUsername())))
                .andExpect(jsonPath("$.status", is(player1.getStatus().toString())))
                .andExpect(jsonPath("$.creationDate", is(creationD)))
                .andExpect(jsonPath("$.birthday").value(IsNull.nullValue()));
    }
    @Test
    public void getUser_withId_notFound() throws Exception{
        given(playerService.getPlayers()).willReturn(Collections.emptyList());

        MockHttpServletRequestBuilder getRequest = get("/users/1");

        mockMvc.perform(getRequest)
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException));
    }

    @Test
    public void keepAlive_success() throws Exception{
        Mockito.doNothing().when(playerService).checkToken(anyString());
        Mockito.doNothing().when(playerService).keepAlive(anyString());

        MockHttpServletRequestBuilder request = put("/users/keepAlive")
                .header("token", token1);

        mockMvc.perform(request)
                .andExpect(status().isNoContent());
    }

    @Test
    public void raking_highScore_success() throws Exception {
        List<Player> playersRankedHighScore = new ArrayList<>();
        playersRankedHighScore.add(player2);
        playersRankedHighScore.add(player1);

        Mockito.doNothing().when(playerService).checkToken(anyString());
        given(playerService.getTop15PlayersHighScore()).willReturn(playersRankedHighScore);

        MockHttpServletRequestBuilder request = get("/users/ranking")
                .header("Token", token1);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(player2.getId().intValue())))
                .andExpect(jsonPath("$[0].username", is(player2.getUsername())))
                .andExpect(jsonPath("$[0].status", is(player2.getStatus().toString())))
                .andExpect(jsonPath("$[1].id", is(player1.getId().intValue())))
                .andExpect(jsonPath("$[1].username", is(player1.getUsername())))
                .andExpect(jsonPath("$[1].status", is(player2.getStatus().toString())));
    }

    @Test
    public void raking_gamesWon_success() throws Exception {
        List<Player> playersRankedGamesWon = new ArrayList<>();
        playersRankedGamesWon.add(player1);
        playersRankedGamesWon.add(player2);

        Mockito.doNothing().when(playerService).checkToken(anyString());

        Mockito.doNothing().when(playerService).checkToken(anyString());
        given(playerService.getTop15PlayersGamesWon()).willReturn(playersRankedGamesWon);

        MockHttpServletRequestBuilder request = get("/users/rankingGamesWon")
                .header("Token", token1);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(player1.getId().intValue())))
                .andExpect(jsonPath("$[0].username", is(player1.getUsername())))
                .andExpect(jsonPath("$[1].id", is(player2.getId().intValue())))
                .andExpect(jsonPath("$[1].username", is(player2.getUsername())));
    }

    /**
     * Helper Method to convert userPostDTO into a JSON string such that the input
     * can be processed
     * Input will look like this: {"name": "Test Player", "username": "testUsername"}
     *
     * @param object
     * @return string
     */
    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("The request body could not be created.%s", e.toString()));
        }
    }
}