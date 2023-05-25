package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.constant.PlayerStatus;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.wrappers.Guess;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import ch.uzh.ifi.hase.soprafs23.service.LobbyService;
import ch.uzh.ifi.hase.soprafs23.service.PlayerService;
import ch.uzh.ifi.hase.soprafs23.stomp.dto.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GameStompControllerTest {

	private final Logger logger = LoggerFactory.getLogger(GameStompControllerTest.class);

	@LocalServerPort
	// automatically allocated random port
	private Integer port;

	@MockBean
	private GameService gameService;

	@MockBean
	private PlayerService playerService;

	@MockBean
	private LobbyService lobbyService;

	private WebSocketStompClient webSocketStompClient;

	private Player testPlayer;

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
	}

	private String getWsPath() {
		return String.format("ws://localhost:%d/ws", port);
	}

	private MessageConverter getSpiedObjOutConverter() {
		return new MessageConverter() {
			private final Gson gson = new Gson();

			@Override
			public Object fromMessage(Message<?> message, Class<?> targetClass) {
				String text = new String((byte[]) message.getPayload(), StandardCharsets.UTF_8);
				return gson.fromJson(text, SpiedObjectOut.class);
			}

			@Override
			public Message<?> toMessage(Object payload, MessageHeaders headers) {
				return new Message<Object>() {
					@Override
					public Object getPayload() {
						return gson.toJson(payload).getBytes(StandardCharsets.UTF_8);
					}

					@Override
					public MessageHeaders getHeaders() {
						return headers;
					}
				};
			}
		};
	}

	private MessageConverter getEndRoundConverter() {
		return new MessageConverter() {
			private final Gson gson = new Gson();

			@Override
			public Object fromMessage(Message<?> message, Class<?> targetClass) {
				String text = new String((byte[]) message.getPayload(), StandardCharsets.UTF_8);
				return gson.fromJson(text, EndRoundMessage.class);
			}

			@Override
			public Message<?> toMessage(Object payload, MessageHeaders headers) {
				return null;
			}
		};
	}

	private MessageConverter getGuessConverter() {
		return new MessageConverter() {
			private final Gson gson = new Gson();

			@Override
			public Object fromMessage(Message<?> message, Class<?> targetClass) {
				String text = new String((byte[]) message.getPayload(), StandardCharsets.UTF_8);
				return gson.fromJson(text, new TypeToken<List<Guess>>() {
				}.getType());
			}

			@Override
			public Message<?> toMessage(Object payload, MessageHeaders headers) {
				return new Message<Object>() {
					@Override
					public Object getPayload() {
						return gson.toJson(payload).getBytes(StandardCharsets.UTF_8);
					}

					@Override
					public MessageHeaders getHeaders() {
						return headers;
					}
				};
			}
		};
	}

	private MessageConverter getHintConverter() {
		return new MessageConverter() {
			private final Gson gson = new Gson();

			@Override
			public Object fromMessage(Message<?> message, Class<?> targetClass) {
				String text = new String((byte[]) message.getPayload(), StandardCharsets.UTF_8);
				return gson.fromJson(text, Hint.class);
			}

			@Override
			public Message<?> toMessage(Object payload, MessageHeaders headers) {
				return new Message<Object>() {
					@Override
					public Object getPayload() {
						return gson.toJson(payload).getBytes(StandardCharsets.UTF_8);
					}

					@Override
					public MessageHeaders getHeaders() {
						return headers;
					}
				};
			}
		};
	}

	public void playAgain() throws InterruptedException, ExecutionException {
		BlockingQueue<EndRoundMessage> queue = new ArrayBlockingQueue<>(1);
		Random random = new Random(398);
		int gameId = random.nextInt(30) + 1;

		webSocketStompClient.setMessageConverter(getEndRoundConverter());

		StompSession session = webSocketStompClient.connect(getWsPath(), new StompSessionHandlerAdapter() {
		}).get();
		session.subscribe("/topic/games/" + gameId + "/playAgain", new StompSessionHandlerAdapter() {
			@Override
			public Type getPayloadType(StompHeaders headers) {
				return EndRoundMessage.class;
			}

			@Override
			public void handleFrame(StompHeaders headers, Object payload) {
				queue.add((EndRoundMessage) payload);
			}
		});
		session.send("/app/games/" + gameId + "/playAgain", "");

		await()
				.atMost(2, TimeUnit.SECONDS)
				.untilAsserted(() -> assertNotEquals(null, queue.peek()));

		EndRoundMessage out = queue.poll();
		assert out != null;
		assertEquals("playAgain", out.getEndRoundMessage());
		assertEquals(0, out.getCurrentRound());
		assertEquals(0, out.getAmountOfRounds());
	}

	@Test
	public void endGame() throws ExecutionException, InterruptedException {
		BlockingQueue<EndRoundMessage> queue = new ArrayBlockingQueue<>(1);
		Random random = new Random(398);
		int gameId = random.nextInt(30) + 1;

		webSocketStompClient.setMessageConverter(getEndRoundConverter());

		StompSession session = webSocketStompClient.connect(getWsPath(), new StompSessionHandlerAdapter() {
		}).get();
		session.subscribe("/topic/games/" + gameId + "/gameOver", new StompSessionHandlerAdapter() {
			@Override
			public Type getPayloadType(StompHeaders headers) {
				return EndRoundMessage.class;
			}

			@Override
			public void handleFrame(StompHeaders headers, Object payload) {
				queue.add((EndRoundMessage) payload);
			}
		});
		session.send("/app/games/" + gameId + "/gameOver", "");

		await()
				.atMost(2, TimeUnit.SECONDS)
				.untilAsserted(() -> assertNotEquals(null, queue.peek()));

		EndRoundMessage out = queue.poll();
		assert out != null;
		assertEquals("endGame", out.getEndRoundMessage());
		assertEquals(0, out.getCurrentRound());
		assertEquals(0, out.getAmountOfRounds());
	}

	@Test
	public void distributeHints() throws ExecutionException, InterruptedException {
		BlockingQueue<Hint> queue = new ArrayBlockingQueue<>(1);
		Random random = new Random(398);
		int gameId = random.nextInt(30) + 1;

		Hint in = new Hint("large");

		webSocketStompClient.setMessageConverter(getHintConverter());

		StompSession session = webSocketStompClient.connect(getWsPath(), new StompSessionHandlerAdapter() {
		}).get();
		session.subscribe("/topic/games/" + gameId + "/hints", new StompSessionHandlerAdapter() {
			@Override
			public Type getPayloadType(StompHeaders headers) {
				return Hint.class;
			}

			@Override
			public void handleFrame(StompHeaders headers, Object payload) {
				queue.add((Hint) payload);
			}
		});
		session.send("/app/games/" + gameId + "/hints", in);

		await()
				.atMost(2, TimeUnit.SECONDS)
				.untilAsserted(() -> assertNotEquals(null, queue.peek()));

		Hint out = queue.poll();
		assert out != null;
		assertEquals(in.getHint(), out.getHint());
	}

	@Test
	public void handleGuess() throws ExecutionException, InterruptedException {
		BlockingQueue<List<Guess>> queue = new ArrayBlockingQueue<>(1);
		Random random = new Random(398);
		int gameId = random.nextInt(30) + 1;

		GuessIn in = new GuessIn("cat", "" + testPlayer.getId());

		Mockito.when(playerService.getPlayer(Mockito.anyLong())).thenReturn(testPlayer);
		Mockito.when(gameService.checkGuessAndAllocatePoints(Mockito.anyInt(), Mockito.any(), Mockito.anyString(),
				Mockito.any()))
				.thenReturn(List.of(new Guess(testPlayer.getUsername(), in.getGuess(), 0)));

		webSocketStompClient.setMessageConverter(getGuessConverter());

		StompSession session = webSocketStompClient.connect(getWsPath(), new StompSessionHandlerAdapter() {
		}).get();
		session.subscribe("/topic/games/" + gameId + "/guesses", new StompSessionHandlerAdapter() {
			@Override
			public Type getPayloadType(StompHeaders headers) {
				return List.class;
			}

			@Override
			@SuppressWarnings("unchecked") // payload will always be List<Guess>
			public void handleFrame(StompHeaders headers, Object payload) {
				queue.add((List<Guess>) payload);
			}
		});
		session.send("/app/games/" + gameId + "/guesses", in);

		await()
				.atMost(2, TimeUnit.SECONDS)
				.untilAsserted(() -> assertNotEquals(null, queue.peek()));

		List<Guess> out = queue.poll();
		assert out != null;
		assertEquals(testPlayer.getUsername(), out.get(0).getGuesserName());
		assertEquals(in.getGuess(), out.get(0).getGuess());
	}

	@Test
	public void nextRound() throws ExecutionException, InterruptedException {
		BlockingQueue<EndRoundMessage> queue = new ArrayBlockingQueue<>(1);
		Random random = new Random(398);
		int gameId = random.nextInt(30) + 1;

		webSocketStompClient.setMessageConverter(getEndRoundConverter());

		StompSession session = webSocketStompClient.connect(getWsPath(), new StompSessionHandlerAdapter() {
		}).get();
		session.subscribe("/topic/games/" + gameId + "/nextRound", new StompSessionHandlerAdapter() {
			@Override
			public Type getPayloadType(StompHeaders headers) {
				return SpiedObjectOut.class;
			}

			@Override
			public void handleFrame(StompHeaders headers, Object payload) {
				queue.add((EndRoundMessage) payload);
			}
		});
		session.send("/app/games/" + gameId + "/nextRound", "");

		await()
				.atMost(2, TimeUnit.SECONDS)
				.untilAsserted(() -> assertNotEquals(null, queue.peek()));

		EndRoundMessage end = queue.poll();
		assert end != null;
		assertEquals("nextRound", end.getEndRoundMessage());
		assertEquals(0, end.getAmountOfRounds());
		assertEquals(0, end.getCurrentRound());
	}

	@Test
	public void handleSpiedObject() throws ExecutionException, InterruptedException {
		BlockingQueue<SpiedObjectOut> queue = new ArrayBlockingQueue<>(1);
		Random random = new Random(398);
		int gameId = random.nextInt(30) + 1;

		SpiedObjectIn in = new SpiedObjectIn("tree", "green", new Location(0, 0));

		Mockito.when(gameService.initializeStartTime(Mockito.anyInt())).thenReturn(new Date(0L));
		Mockito.when(gameService.getDuration(Mockito.anyInt())).thenReturn(1.5f);

		webSocketStompClient.setMessageConverter(getSpiedObjOutConverter());

		StompSession session = webSocketStompClient.connect(getWsPath(), new StompSessionHandlerAdapter() {
		}).get();
		session.subscribe("/topic/games/" + gameId + "/spiedObject", new StompSessionHandlerAdapter() {
			@Override
			public Type getPayloadType(StompHeaders headers) {
				return SpiedObjectOut.class;
			}

			@Override
			public void handleFrame(StompHeaders headers, Object payload) {
				logger.info("Handling frame for {}", payload);
				queue.add((SpiedObjectOut) payload);
			}
		});
		logger.info("subscribed to /topic/games/{}/spiedObject", gameId);
		session.send("/app/games/" + gameId + "/spiedObject", in);

		await()
				.atMost(2, TimeUnit.SECONDS)
				.untilAsserted(() -> assertNotEquals(null, queue.peek()));

		SpiedObjectOut out = queue.poll();
		assert out != null;
		assertEquals(in.getLocation(), out.getLocation());
		assertEquals(in.getColor(), out.getColor());
		String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(0L));
		assertEquals(time, out.getStartTime());
		assertEquals(1.5f, out.getDuration());
	}

}
