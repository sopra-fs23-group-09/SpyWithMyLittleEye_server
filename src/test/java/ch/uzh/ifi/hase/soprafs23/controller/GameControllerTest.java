package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.constant.Role;
import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import ch.uzh.ifi.hase.soprafs23.service.LobbyService;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.util.*;

import static org.mockito.BDDMockito.given;
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

    @MockBean
    private GameRepository gameRepository;

    @Test
    void getRole() throws Exception {
        // create a new game
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

        String roundOverStatus = "time is up";
        int gameid = 1;
        String keyword = "car";
        Long hostId = 1L;
        int currentRoundNr = 1;

        Game game = new Game(gameid,players,3,player1);
        game.nextRound();
        Date startTime = new Date();
        game.initializeStartTime(startTime);

        //add game to GameRepository
        //GameRepository.addGame(game);

        List<UserPointsWrapper> userPointsWrappers = new ArrayList<>();
        userPointsWrappers.add(new UserPointsWrapper(player1.getUsername(), 0));
        userPointsWrappers.add(new UserPointsWrapper(player2.getUsername(), 0));
        userPointsWrappers.add(new UserPointsWrapper(player3.getUsername(), 0));

        given(GameRepository.getGameById(gameid)).willReturn(game);

        //when
        MockHttpServletRequestBuilder getRequest = get("/game/"+gameid+"/round/results");

        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.playerPoints").value(userPointsWrappers))
                .andExpect(jsonPath("$.roundOverStatus").value(roundOverStatus))
                .andExpect(jsonPath("$.keyword").value(keyword))
                .andExpect(jsonPath("$.hostId").value(hostId))
                .andExpect(jsonPath("$.currentRoundNr").value(currentRoundNr));
    }
     */
}