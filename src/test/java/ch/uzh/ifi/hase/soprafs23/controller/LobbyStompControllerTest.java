package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.constant.PlayerStatus;
import ch.uzh.ifi.hase.soprafs23.entity.Lobby;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.service.PlayerService;
import ch.uzh.ifi.hase.soprafs23.stomp.dto.GameStarted;
import ch.uzh.ifi.hase.soprafs23.rest.dto.LobbyGetDTO;
import ch.uzh.ifi.hase.soprafs23.service.LobbyService;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LobbyStompControllerTest {

    /////// VARIABLES

    @LocalServerPort
    // automatically allocated random port
    private Integer port;

    private WebSocketStompClient webSocketStompClient;

    @MockBean
    private LobbyService lobbyService;

    private Player testPlayer;

    private Lobby lobby;

    ////// SETUP AND HELPERS

    @BeforeEach
    void setup() {
        webSocketStompClient = new WebSocketStompClient(new SockJsClient(List.of(
                new WebSocketTransport(new StandardWebSocketClient()))));

        testPlayer = new Player();
        testPlayer.setId(3L);
        testPlayer.setPassword("password");
        testPlayer.setUsername("testPlayer");
        testPlayer.setToken("1");
        testPlayer.setStatus(PlayerStatus.ONLINE);
        testPlayer.setCreationDate(new Date(0L));

        Random random = new Random(789);
        int id = random.nextInt(30);
        int accessCode = random.nextInt(90000) + 10000;
        int amountRounds = random.nextInt(10);

        lobby = new Lobby(testPlayer, id, accessCode, amountRounds, 1.5f);
    }

    private String getWsPath() { return String.format("ws://localhost:%d/ws", port); }

    private MessageConverter getLobbyGetDTOConverter() {
        return new MessageConverter() {
            @Override
            public Object fromMessage(Message<?> message, Class<?> targetClass) {
                String text = new String((byte[]) message.getPayload(), StandardCharsets.UTF_8);
                Gson gson = new Gson();
                return gson.fromJson(text, LobbyGetDTO.class);
            }

            @Override
            public Message<?> toMessage(Object payload, MessageHeaders headers) {
                return null;
            }
        };
    }

    private MessageConverter getGameStartedGetDTOConverter() {
        return new MessageConverter() {
            @Override
            public Object fromMessage(Message<?> message, Class<?> targetClass) {
                String text = new String((byte[]) message.getPayload(), StandardCharsets.UTF_8);
                Gson gson = new Gson();
                return gson.fromJson(text, GameStarted.class);
            }

            @Override
            public Message<?> toMessage(Object payload, MessageHeaders headers) {
                return null;
            }
        };
    }

    ////// TESTS

    @Test
    public void getLobbyInformation_lobbyExists() throws ExecutionException, InterruptedException {
        BlockingQueue<LobbyGetDTO> queue = new ArrayBlockingQueue<>(1);

        Mockito.when(lobbyService.getLobby(Mockito.anyInt())).thenReturn(lobby);

        webSocketStompClient.setMessageConverter(getLobbyGetDTOConverter());

        StompSession session = webSocketStompClient.connect(getWsPath(), new StompSessionHandlerAdapter() {}).get();
        session.subscribe("/topic/lobbies/" + lobby.getId(), new StompSessionHandlerAdapter() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return LobbyGetDTO.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                queue.add((LobbyGetDTO) payload);
            }
        });
        session.send(String.format("/app/lobbies/%d/joined", lobby.getId()), "");

        await()
                .atMost(2, TimeUnit.SECONDS)
                .untilAsserted(() -> assertNotEquals(null, queue.peek()));

        LobbyGetDTO lobbyGetDTO = queue.poll();
        assert lobbyGetDTO != null;
        assertEquals("joined", lobbyGetDTO.getEvent());
        assertEquals(lobby.getId(), lobbyGetDTO.getId());
        assertEquals(lobby.getAccessCode(), lobbyGetDTO.getAccessCode());
        assertEquals(lobby.getHostId(), lobbyGetDTO.getHostId());
        assertEquals(lobby.getAmountRounds(), lobbyGetDTO.getAmountRounds());
        assertEquals(testPlayer.getUsername(), lobbyGetDTO.getPlayerNames().get(0));
    }

    @Test
    public void startGame_lobbyExists() throws ExecutionException, InterruptedException {
        BlockingQueue<GameStarted> queue = new ArrayBlockingQueue<>(1);

        PlayerService playerService = Mockito.mock(PlayerService.class);
        Mockito.when(lobbyService.startGame(Mockito.anyInt(), Mockito.any())).thenReturn(lobby.initiateGame(playerService));

        webSocketStompClient.setMessageConverter(getGameStartedGetDTOConverter());

        StompSession session = webSocketStompClient.connect(getWsPath(), new StompSessionHandlerAdapter() {}).get();
        session.subscribe("/topic/lobbies/" + lobby.getId(), new StompSessionHandlerAdapter() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return GameStarted.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                queue.add((GameStarted) payload);
            }
        });
        session.send(String.format("/app/games/%d", lobby.getId()), "");

        await()
                .atMost(2, TimeUnit.SECONDS)
                .untilAsserted(() -> assertNotEquals(null, queue.peek()));

        GameStarted game = queue.poll();
        assertEquals(game.getEvent(), "started");
        assertEquals(game.getId(), lobby.getId());
    }

}
