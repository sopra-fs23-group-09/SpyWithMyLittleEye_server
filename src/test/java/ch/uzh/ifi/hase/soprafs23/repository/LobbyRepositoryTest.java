package ch.uzh.ifi.hase.soprafs23.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ch.uzh.ifi.hase.soprafs23.entity.Lobby;
import ch.uzh.ifi.hase.soprafs23.entity.Player;

public class LobbyRepositoryTest {

	private Lobby lobby;

	private int lobbyId, accessCode, rounds;
	private float duration;

	@BeforeEach
	public void setup() {
		LobbyRepository.reset();

		Player host = new Player();
		host.setId(1L);

		Random random = new Random(-13);
		lobbyId = random.nextInt(200) + 1;
		accessCode = random.nextInt(89_999) + 10_000;
		rounds = random.nextInt(19) + 1;
		duration = (new float[] { 1, 1.5f, 2, 4 })[random.nextInt(4)];
		lobby = new Lobby(host, lobbyId, accessCode, rounds, duration);
	}

	@Test
	public void tests() {
		LobbyRepository.addLobby(lobby);

		Lobby l = LobbyRepository.getLobbyById(lobbyId);

		assertEquals(accessCode, l.getAccessCode());
		assertEquals(rounds, l.getAmountRounds());

		Lobby l2 = LobbyRepository.getLobbyByAccessCode(accessCode);

		assertEquals(accessCode, l2.getAccessCode());
		assertEquals(rounds, l2.getAmountRounds());

		LobbyRepository.deleteLobby(lobbyId);

		assertNull(LobbyRepository.getLobbyByAccessCode(accessCode));
		assertNull(LobbyRepository.getLobbyById(lobbyId));
	}

}
