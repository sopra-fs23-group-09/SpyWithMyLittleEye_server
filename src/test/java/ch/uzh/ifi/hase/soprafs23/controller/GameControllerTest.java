package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.constant.Role;
import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.entity.wrappers.UserPointsWrapper;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.rest.dto.RoundGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import ch.uzh.ifi.hase.soprafs23.service.LobbyService;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.hamcrest.core.IsNull;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(GameController.class)
class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameService gameService;

    @MockBean
    private UserService userService;

    @MockBean
    private LobbyService lobbyService;

    @Test
    void getRole() throws Exception {
        // given
        User player1 = new User();
        player1.setId(1L);
        player1.setUsername("petra");
        player1.setPassword("password");
        player1.setStatus(UserStatus.ONLINE);
        player1.setToken("token");
        player1.setCreationDate(new Date(0L));
        player1.setBirthday(new Date(0L));

        User player2 = new User();
        player2.setId(2L);
        player2.setUsername("eva");
        player2.setPassword("1234");
        player2.setStatus(UserStatus.ONLINE);
        player2.setToken("token");
        player2.setCreationDate(new Date(0L));
        player2.setBirthday(new Date(0L));

        User player3 = new User();
        player3.setId(3L);
        player3.setUsername("elena");
        player3.setPassword("admin");
        player3.setStatus(UserStatus.ONLINE);
        player3.setToken("token");
        player3.setCreationDate(new Date(0L));
        player3.setBirthday(new Date(0L));

        List<User> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        players.add(player3);

        int gameid = 1;

        Game game = new Game(gameid,players,3,player1);
        game.nextRound();

        //mocking gameService
        given(gameService.getRole(gameid, player1.getId())).willReturn(Role.SPIER);
        given(gameService.getRole(gameid, player2.getId())).willReturn(Role.GUESSER);
        given(gameService.getRole(gameid, player3.getId())).willReturn(Role.GUESSER);

        //testing response for player1
        //when
        MockHttpServletRequestBuilder getRequestPlayer1 = get("/game/"+gameid+"/roleForUser/"+player1.getId());

        mockMvc.perform(getRequestPlayer1)
                .andExpect(status().isOk())
                .andExpect(content().string("\"SPIER\""));

        //testing response for player2
        //when
        MockHttpServletRequestBuilder getRequestPlayer2 = get("/game/"+gameid+"/roleForUser/"+player2.getId());

        mockMvc.perform(getRequestPlayer2)
                .andExpect(status().isOk())
                .andExpect(content().string("\"GUESSER\""));

        //testing response for player3
        //when
        MockHttpServletRequestBuilder getRequestPlayer3 = get("/game/"+gameid+"/roleForUser/"+player3.getId());

        mockMvc.perform(getRequestPlayer3)
                .andExpect(status().isOk())
                .andExpect(content().string("\"GUESSER\""));
    }

    @Test
    void getRound() throws Exception {
        // given
        User player1 = new User();
        player1.setId(1L);
        player1.setUsername("petra");
        player1.setPassword("password");
        player1.setStatus(UserStatus.ONLINE);
        player1.setToken("token");
        player1.setCreationDate(new Date(0L));
        player1.setBirthday(new Date(0L));

        User player2 = new User();
        player2.setId(2L);
        player2.setUsername("eva");
        player2.setPassword("1234");
        player2.setStatus(UserStatus.ONLINE);
        player2.setToken("token");
        player2.setCreationDate(new Date(0L));
        player2.setBirthday(new Date(0L));

        User player3 = new User();
        player3.setId(3L);
        player3.setUsername("elena");
        player3.setPassword("admin");
        player3.setStatus(UserStatus.ONLINE);
        player3.setToken("token");
        player3.setCreationDate(new Date(0L));
        player3.setBirthday(new Date(0L));

        List<User> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        players.add(player3);

        int gameid = 1;
        int currentRound = 1;
        int amountRounds = 3;

        Game game = new Game(gameid,players,amountRounds,player1);
        game.nextRound();

        //mocking gameService
        given(gameService.getCurrentRoundNr(gameid)).willReturn(currentRound);
        given(gameService.getTotalNrRounds(gameid)).willReturn(amountRounds);

        //when
        MockHttpServletRequestBuilder getRequest = get("/game/"+gameid+"/roundnr");

        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentRound").value(currentRound))
                .andExpect(jsonPath("$.totalRounds").value(amountRounds));
    }

    /*
    @Test
    void getRoundInformation() throws Exception {

        // given
        User player1 = new User();
        player1.setId(1L);
        player1.setUsername("petra");
        player1.setPassword("password");
        player1.setStatus(UserStatus.ONLINE);
        player1.setToken("token");
        player1.setCreationDate(new Date(0L));
        player1.setBirthday(new Date(0L));

        User player2 = new User();
        player2.setId(2L);
        player2.setUsername("eva");
        player2.setPassword("1234");
        player2.setStatus(UserStatus.ONLINE);
        player2.setToken("token");
        player2.setCreationDate(new Date(0L));
        player2.setBirthday(new Date(0L));

        User player3 = new User();
        player3.setId(3L);
        player3.setUsername("elena");
        player3.setPassword("admin");
        player3.setStatus(UserStatus.ONLINE);
        player3.setToken("token");
        player3.setCreationDate(new Date(0L));
        player3.setBirthday(new Date(0L));

        List<User> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        players.add(player3);

        int gameid = 1;

        UserPointsWrapper userPointsWrapper1 = new UserPointsWrapper("petra", 500);
        UserPointsWrapper userPointsWrapper2 = new UserPointsWrapper("eva", 250);
        UserPointsWrapper userPointsWrapper3 = new UserPointsWrapper("elena", 50);

        List<UserPointsWrapper> playerPoints = new ArrayList<>();
        playerPoints.add(userPointsWrapper1);
        playerPoints.add(userPointsWrapper2);
        playerPoints.add(userPointsWrapper3);

        String roundOverStatus = "time is up";
        String keyword = "car";
        Long hostId = 1L;
        int currentRoundNr = 1;

        Game game = new Game(gameid,players,3,player1);
        game.nextRound();


        RoundGetDTO roundGetDTO = new RoundGetDTO();
        roundGetDTO.setPlayerPoints(player1,500);
        roundGetDTO.setRoundOverStatus(roundOverStatus);
        roundGetDTO.setKeyword(keyword);
        roundGetDTO.setHostId(hostId);
        roundGetDTO.setCurrentRoundNr(currentRoundNr);


        //mocking gameService
        given(DTOMapper.INSTANCE.convertGameToRoundGetDTO(GameRepository.getGameById(gameid))).willReturn(roundGetDTO);

        //when
        MockHttpServletRequestBuilder getRequest = get("/game/"+gameid+"/round/results");

        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.playerPoints").value(playerPoints))
                .andExpect(jsonPath("$.roundOverStatus").value(roundOverStatus))
                .andExpect(jsonPath("$.keyword").value(keyword))
                .andExpect(jsonPath("$.hostId").value(hostId))
                .andExpect(jsonPath("$.currentRoundNr").value(currentRoundNr));
    }
     */
}