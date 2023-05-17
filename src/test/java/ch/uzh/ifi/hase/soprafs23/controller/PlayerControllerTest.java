package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.constant.PlayerStatus;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.rest.dto.PlayerPostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.PlayerPutDTO;
import ch.uzh.ifi.hase.soprafs23.service.PlayerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.core.IsNull;
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
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @Test
    public void givenUsers_whenGetUsers_thenReturnJsonArray() throws Exception {
        // given
        Player player = new Player();
        player.setId(1L);
        player.setPassword("password");
        player.setUsername("firstname@lastname");
        player.setStatus(PlayerStatus.ONLINE);
        player.setToken("token");
        player.setCreationDate(new Date(0L));
        player.setBirthday(new Date(0L));

        List<Player> allPlayers = Collections.singletonList(player);

        // this mocks the PlayerService -> we define above what the playerService should
        // return when getUsers() is called
        given(playerService.getPlayers()).willReturn(allPlayers);

        // when
        MockHttpServletRequestBuilder getRequest = get("/users").contentType(MediaType.APPLICATION_JSON);

        SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
        String creationD = dFormat.format(player.getCreationDate());
        String birthd = dFormat.format(player.getBirthday());
        // then
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].username", is(player.getUsername())))
                .andExpect(jsonPath("$[0].status", is(player.getStatus().toString())))
                .andExpect(jsonPath("$[0].creationDate", is(creationD)))
                .andExpect(jsonPath("$[0].birthday", is(birthd)))
                .andExpect(jsonPath("$[0].id", is(player.getId().intValue())));
    }

    @Test
    public void createUser_validInput_userCreated() throws Exception {
        // given
        Player player = new Player();
        player.setId(1L);
        player.setPassword("password");
        player.setUsername("testUsername");
        player.setToken("1");
        player.setStatus(PlayerStatus.ONLINE);
        player.setCreationDate(new Date(0L));

        PlayerPostDTO playerPostDTO = new PlayerPostDTO();
        playerPostDTO.setPassword("password");
        playerPostDTO.setUsername("testUsername");

        given(playerService.createPlayer(Mockito.any())).willReturn(player);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(playerPostDTO));

        SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
        String creationD = dFormat.format(player.getCreationDate());

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(player.getId().intValue())))
                .andExpect(jsonPath("$.username", is(player.getUsername())))
                .andExpect(jsonPath("$.status", is(player.getStatus().toString())))
                .andExpect(jsonPath("$.creationDate", is(creationD)))
                .andExpect(jsonPath("$.birthday").value(IsNull.nullValue()));
    }

    @Test
    public void createUser_duplicateUsername_conflict() throws Exception{
        String errorMessage = "Reason";
        ResponseStatusException e = new ResponseStatusException(HttpStatus.CONFLICT, errorMessage);
        given(playerService.createPlayer(Mockito.any())).willThrow(e);

        PlayerPostDTO playerPostDTO = new PlayerPostDTO();
        playerPostDTO.setPassword("password");
        playerPostDTO.setUsername("testUsername");

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
        playerPutDTO.setBirthday(new Date(0L));
        playerPutDTO.setUsername("newUsername");

        MockHttpServletRequestBuilder putRequest = put("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(playerPutDTO));

        mockMvc.perform(putRequest).andExpect(status().isNoContent());
    }

    @Test
    public void editUser_notFound() throws Exception{
        String errorMessage = "Reason";
        ResponseStatusException e = new ResponseStatusException(HttpStatus.NOT_FOUND, errorMessage);
        doThrow(e).when(playerService).updatePlayer(Mockito.any(), Mockito.anyString(), Mockito.anyLong());

        PlayerPutDTO playerPutDTO = new PlayerPutDTO();
        playerPutDTO.setBirthday(new Date(0L));
        playerPutDTO.setUsername("newUsername");

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
        Player player = new Player();
        player.setId(1L);
        player.setPassword("password");
        player.setUsername("testUsername");
        player.setToken("1");
        player.setStatus(PlayerStatus.ONLINE);
        player.setCreationDate(new Date(0L));

        List<Player> allPlayers = Collections.singletonList(player);
        given(playerService.getPlayers()).willReturn(allPlayers);

        MockHttpServletRequestBuilder getRequest = get("/users/"+ player.getId());

        SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
        String creationD = dFormat.format(player.getCreationDate());
        System.out.println(creationD);

        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(player.getId().intValue())))
                .andExpect(jsonPath("$.username", is(player.getUsername())))
                .andExpect(jsonPath("$.status", is(player.getStatus().toString())))
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