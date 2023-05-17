package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.constant.PlayerStatus;
import ch.uzh.ifi.hase.soprafs23.entity.Lobby;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.rest.dto.LobbyPostDTO;
import ch.uzh.ifi.hase.soprafs23.service.LobbyService;
import ch.uzh.ifi.hase.soprafs23.service.PlayerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.mockito.Mockito.doThrow;
import static org.mockito.ArgumentMatchers.anyString;

import java.util.Date;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LobbyController.class)
public class LobbyControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlayerService playerService;
    @MockBean
    private LobbyService lobbyService;

    @Test
    public void createLobby_hostInNoOtherLobby() throws Exception{
        // given
        Player host = new Player();
        host.setId(1L);
        host.setPassword("password");
        host.setUsername("testUsername");
        host.setToken("1");
        host.setStatus(PlayerStatus.ONLINE);
        host.setCreationDate(new Date(0L));

        Lobby lobby = new Lobby(host, 1, 12345, 3, 1.5f);

        LobbyPostDTO lobbyPostDTO = new LobbyPostDTO();
        lobbyPostDTO.setAmountRounds(3);

        given(playerService.getPlayer(Mockito.any())).willReturn(host);
        given(lobbyService.createLobby(Mockito.any(), Mockito.anyInt(), Mockito.anyFloat())).willReturn(lobby);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/lobbies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(lobbyPostDTO));

        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.event", is("joined")))
                .andExpect(jsonPath("$.id", is(lobby.getId())))
                .andExpect(jsonPath("$.accessCode", is(lobby.getAccessCode())))
                .andExpect(jsonPath("$.hostId", is(lobby.getHostId().intValue())))
                .andExpect(jsonPath("$.amountRounds", is(lobby.getAmountRounds())))
                .andExpect(jsonPath("$.playerNames[0]", is(host.getUsername())));
    }

    @Test
    public void createLobby_hostInOtherLobby() throws Exception{
        String errorMessage = "Reason";
        ResponseStatusException e = new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
        // given
        Player host = new Player();
        host.setId(1L);
        host.setPassword("password");
        host.setUsername("testUsername");
        host.setToken("1");
        host.setStatus(PlayerStatus.ONLINE);
        host.setCreationDate(new Date(0L));
        host.setLobbyID(2);

        LobbyPostDTO lobbyPostDTO = new LobbyPostDTO();
        lobbyPostDTO.setAmountRounds(3);

        given(playerService.getPlayer(Mockito.any())).willReturn(host);
        given(lobbyService.createLobby(Mockito.any(), Mockito.anyInt(), Mockito.anyFloat())).willThrow(e);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/lobbies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(lobbyPostDTO));

        mockMvc.perform(postRequest)
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(result -> assertTrue(result.getResolvedException().getMessage().contains(errorMessage)));
    }

    @Test
    public void joinLobby_userInNoOtherLobby() throws Exception {
        Player host = new Player();
        host.setId(1L);
        host.setPassword("pass");
        host.setUsername("testNme");
        host.setToken("1");
        host.setStatus(PlayerStatus.ONLINE);
        host.setCreationDate(new Date(0L));
        host.setLobbyID(1);

        Player player = new Player();
        player.setId(2L);
        player.setPassword("password");
        player.setUsername("testUsername");
        player.setToken("2");
        player.setStatus(PlayerStatus.ONLINE);
        player.setCreationDate(new Date(0L));

        Lobby lobby = new Lobby(host, 1, 12345, 3, 1.5f);
        lobby.addPlayer(player);

        given(playerService.getPlayer(Mockito.any())).willReturn(player);
        given(lobbyService.addUser(Mockito.any(),Mockito.anyInt())).willReturn(lobby);

        MockHttpServletRequestBuilder putRequest = put("/lobbies/join/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"accessCode\":\"12345\"}");

        mockMvc.perform(putRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.event", is("joined")))
                .andExpect(jsonPath("$.id", is(lobby.getId())))
                .andExpect(jsonPath("$.accessCode", is(lobby.getAccessCode())))
                .andExpect(jsonPath("$.hostId", is(lobby.getHostId().intValue())))
                .andExpect(jsonPath("$.amountRounds", is(lobby.getAmountRounds())))
                .andExpect(jsonPath("$.playerNames[1]", is(player.getUsername())));

    }

    @Test
    public void joinLobby_userInOtherLobby() throws Exception {
        String errorMessage = "Reason";
        ResponseStatusException e = new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);

        Player player = new Player();
        player.setId(2L);
        player.setPassword("password");
        player.setUsername("testUsername");
        player.setToken("2");
        player.setStatus(PlayerStatus.ONLINE);
        player.setCreationDate(new Date(0L));

        given(playerService.getPlayer(Mockito.any())).willReturn(player);
        given(lobbyService.addUser(Mockito.any(),Mockito.anyInt())).willThrow(e);

        MockHttpServletRequestBuilder putRequest = put("/lobbies/join/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"accessCode\":\"12345\"}");

        mockMvc.perform(putRequest)
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(result -> assertTrue(result.getResolvedException().getMessage().contains(errorMessage)));
    }

    @Test
    public void joinLobby_LobbyFull() throws Exception {
        String errorMessage = "Reason";
        ResponseStatusException e = new ResponseStatusException(HttpStatus.FORBIDDEN, errorMessage);

        Player player = new Player();
        player.setId(2L);
        player.setPassword("password");
        player.setUsername("testUsername");
        player.setToken("2");
        player.setStatus(PlayerStatus.ONLINE);
        player.setCreationDate(new Date(0L));

        given(playerService.getPlayer(Mockito.any())).willReturn(player);
        given(lobbyService.addUser(Mockito.any(),Mockito.anyInt())).willThrow(e);

        MockHttpServletRequestBuilder putRequest = put("/lobbies/join/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"accessCode\":\"12345\"}");

        mockMvc.perform(putRequest)
                .andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(result -> assertTrue(result.getResolvedException().getMessage().contains(errorMessage)));
    }

    @Test
    public void joinLobby_LobbyNotFound() throws Exception {
        String errorMessage = "Reason";
        ResponseStatusException e = new ResponseStatusException(HttpStatus.NOT_FOUND, errorMessage);

        Player player = new Player();
        player.setId(2L);
        player.setPassword("password");
        player.setUsername("testUsername");
        player.setToken("2");
        player.setStatus(PlayerStatus.ONLINE);
        player.setCreationDate(new Date(0L));

        given(playerService.getPlayer(Mockito.any())).willReturn(player);
        given(lobbyService.addUser(Mockito.any(),Mockito.anyInt())).willThrow(e);

        MockHttpServletRequestBuilder putRequest = put("/lobbies/join/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"accessCode\":\"12345\"}");

        mockMvc.perform(putRequest)
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(result -> assertTrue(result.getResolvedException().getMessage().contains(errorMessage)));
    }

    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("The request body could not be created.%s", e.toString()));
        }
    }

    @Test
    public void exitLobby_ok() throws Exception {
        Player host = new Player();
        host.setId(1L);
        host.setPassword("pass");
        host.setUsername("testNme");
        host.setToken("1");
        host.setStatus(PlayerStatus.ONLINE);
        host.setCreationDate(new Date(0L));
        host.setLobbyID(1);

        Player player = new Player();
        player.setId(2L);
        player.setPassword("password");
        player.setUsername("testUsername");
        player.setToken("2");
        player.setStatus(PlayerStatus.ONLINE);
        player.setCreationDate(new Date(0L));

        int lobbyID = 1;
        Lobby lobby = new Lobby(host, lobbyID, 12345, 3, 1.5f);
        lobby.addPlayer(player);

        // set up mock objects and their behavior
        doNothing().when(playerService).checkToken(anyString());
        given(playerService.getPlayer(player.getId())).willReturn(player);
        doNothing().when(lobbyService).removeUser(player, lobbyID);
        doNothing().when(playerService).exitLobby(player);

        MockHttpServletRequestBuilder putRequest = put("/lobbies/" + lobbyID + "/exit/" + player.getId());

        mockMvc.perform(putRequest)
                .andExpect(status().isOk());
    }

    @Test
    public void exitLobby_error() throws Exception {
        Player host = new Player();
        host.setId(1L);
        host.setPassword("pass");
        host.setUsername("testNme");
        host.setToken("1");
        host.setStatus(PlayerStatus.ONLINE);
        host.setCreationDate(new Date(0L));
        host.setLobbyID(1);

        Player player = new Player();
        player.setId(2L);
        player.setPassword("password");
        player.setUsername("testUsername");
        player.setToken("2");
        player.setStatus(PlayerStatus.ONLINE);
        player.setCreationDate(new Date(0L));

        int lobbyID = 1;
        Lobby lobby = new Lobby(host, lobbyID, 12345, 3, 1.5f);
        lobby.addPlayer(player);

        // set up mock objects and their behavior
        doNothing().when(playerService).checkToken(anyString());
        given(playerService.getPlayer(player.getId())).willReturn(player);
        doThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "the player is not in this lobby"))
                .when(lobbyService).removeUser(player, lobbyID);
        doNothing().when(playerService).exitLobby(player);

        MockHttpServletRequestBuilder putRequest = put("/lobbies/" + lobbyID + "/exit/" + player.getId());

        mockMvc.perform(putRequest)
                .andExpect(status().isForbidden());
    }
}
