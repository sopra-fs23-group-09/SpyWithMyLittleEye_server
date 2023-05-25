package ch.uzh.ifi.hase.soprafs23.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ch.uzh.ifi.hase.soprafs23.service.PlayerService;
import ch.uzh.ifi.hase.soprafs23.service.WebSocketService;

public class LobbyTest {

	@Mock
	private PlayerService playerService;

	@Mock
	private WebSocketService wsService;

	private Player host;
	private Lobby lobbyUnderTest;
	private int lobbyId, accessCode, amountRounds;
	private float duration;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);

		host = new Player();
		host.setId(1L);

		Random random = new Random(782);
		lobbyId = random.nextInt(30) + 1;
		accessCode = random.nextInt(89_999) + 10_000;
		amountRounds = random.nextInt(19) + 1;
		duration = (new float[] { 1f, 1.5f, 2f, 4f })[random.nextInt(4)];
		lobbyUnderTest = new Lobby(host, lobbyId, accessCode, amountRounds, duration);
	}

	@Test
	public void getId() {
		assertEquals(lobbyId, lobbyUnderTest.getId());
	}

	@Test
	public void initiateGame() {
		Game g1 = lobbyUnderTest.initiateGame(playerService);

		assertEquals(lobbyId, g1.getId());

		Game g2 = lobbyUnderTest.initiateGame(playerService);

		assertNull(g2);
	}

	@Test
	public void resetGameToNull() {
		lobbyUnderTest.resetGameToNull(List.of(host));

		assertFalse(lobbyUnderTest.gameStarted());
	}

	@Test
	public void kickPlayer_multiplePlayers() {
		Player p2 = new Player();
		p2.setId(200L);

		lobbyUnderTest.addPlayer(p2);

		int retVal = lobbyUnderTest.kickPlayer(host, wsService);

		assertEquals(-1, retVal);
		assertFalse(lobbyUnderTest.getPlayers().contains(host));
		assertTrue(lobbyUnderTest.getPlayers().contains(p2));
	}

	@Test
	public void kickPlayer_singlePlayer() {
		int retVal = lobbyUnderTest.kickPlayer(host, wsService);

		assertFalse(lobbyUnderTest.getPlayers().contains(host));
		assertEquals(0, retVal);
	}

	@Test
	public void kickPlayer_gameStarted() {
		Game g = lobbyUnderTest.initiateGame(playerService);

		int retVal = lobbyUnderTest.kickPlayer(host, wsService);

		assertFalse(lobbyUnderTest.getPlayers().contains(host));
		assertFalse(g.getPlayers().contains(host));
		assertEquals(1, retVal);
	}

	@Test
	public void removePlayer_notFound() {
		Player testPlayer = new Player();
		testPlayer.setId(200L);

		int retVal = lobbyUnderTest.removePlayer(testPlayer);

		assertEquals(2, retVal);
		assertTrue(lobbyUnderTest.getPlayers().contains(host));
	}

	@Test
	public void removePlayer_single() {
		int retVal = lobbyUnderTest.removePlayer(host);

		assertEquals(0, retVal);
		assertFalse(lobbyUnderTest.getPlayers().contains(host));
	}

	public void removePlayer_multiple() {
		Player testPlayer = new Player();
		testPlayer.setId(200L);

		int retVal = lobbyUnderTest.removePlayer(host);

		assertEquals(1, retVal);
		assertTrue(lobbyUnderTest.getPlayers().contains(testPlayer));
		assertFalse(lobbyUnderTest.getPlayers().contains(host));
	}
}
