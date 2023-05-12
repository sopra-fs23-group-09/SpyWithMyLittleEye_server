package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.constant.Role;
import ch.uzh.ifi.hase.soprafs23.constant.PlayerStatus;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import ch.uzh.ifi.hase.soprafs23.service.LobbyService;
import ch.uzh.ifi.hase.soprafs23.service.PlayerService;
import org.junit.jupiter.api.BeforeEach;
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
    private PlayerService playerService;

    @MockBean
    private LobbyService lobbyService;

    @MockBean
    private GameRepository gameRepository;

    private Player player1, player2, player3;

    private List<Player> players;

    private int gameId, currentRound, amountRounds;

    @BeforeEach
    void setup() {
        // given
        player1 = new Player();
        player1.setId(1L);
        player1.setUsername("petra");
        player1.setPassword("password");
        player1.setStatus(PlayerStatus.ONLINE);
        player1.setToken("token");
        player1.setCreationDate(new Date(0L));
        player1.setBirthday(new Date(0L));

        player2 = new Player();
        player2.setId(2L);
        player2.setUsername("eva");
        player2.setPassword("1234");
        player2.setStatus(PlayerStatus.ONLINE);
        player2.setToken("token");
        player2.setCreationDate(new Date(0L));
        player2.setBirthday(new Date(0L));

        player3 = new Player();
        player3.setId(3L);
        player3.setUsername("elena");
        player3.setPassword("admin");
        player3.setStatus(PlayerStatus.ONLINE);
        player3.setToken("token");
        player3.setCreationDate(new Date(0L));
        player3.setBirthday(new Date(0L));

        players = List.of(player1, player2, player3);

        gameId = 1;
        currentRound = 1;
        amountRounds = 3;

        Game game = new Game(gameId,players,amountRounds,player1, playerService, 1.5f);
        game.nextRound();
    }

    @Test
    void getRole() throws Exception {
        //mocking gameService
        given(gameService.getRole(gameId, player1.getId())).willReturn(Role.SPIER);
        given(gameService.getRole(gameId, player2.getId())).willReturn(Role.GUESSER);
        given(gameService.getRole(gameId, player3.getId())).willReturn(Role.GUESSER);

        //testing response for player1
        //when
        MockHttpServletRequestBuilder getRequestPlayer1 = get("/games/"+gameId+"/roleForUser/"+player1.getId());

        mockMvc.perform(getRequestPlayer1)
                .andExpect(status().isOk())
                .andExpect(content().string("\"SPIER\""));

        //testing response for player2
        //when
        MockHttpServletRequestBuilder getRequestPlayer2 = get("/games/"+gameId+"/roleForUser/"+player2.getId());

        mockMvc.perform(getRequestPlayer2)
                .andExpect(status().isOk())
                .andExpect(content().string("\"GUESSER\""));

        //testing response for player3
        //when
        MockHttpServletRequestBuilder getRequestPlayer3 = get("/games/"+gameId+"/roleForUser/"+player3.getId());

        mockMvc.perform(getRequestPlayer3)
                .andExpect(status().isOk())
                .andExpect(content().string("\"GUESSER\""));
    }

    @Test
    void getRound() throws Exception {
        //mocking gameService
        given(gameService.getCurrentRoundNr(gameId)).willReturn(currentRound);
        given(gameService.getTotalNrRounds(gameId)).willReturn(amountRounds);

        //when
        MockHttpServletRequestBuilder getRequest = get("/games/"+gameId+"/roundnr");

        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentRound").value(currentRound))
                .andExpect(jsonPath("$.totalRounds").value(amountRounds));
    }
}