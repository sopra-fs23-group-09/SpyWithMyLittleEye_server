package ch.uzh.ifi.hase.soprafs23.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PlayerTest {

	private Random random;

	private Player playerUnderTest;

	@BeforeEach
	public void setup() {
		playerUnderTest = new Player();
		playerUnderTest.setId(1L);

		random = new Random(1098);
	}

	@Test
	public void profilePicture() {
		String pic = "profilePic";
		playerUnderTest.setProfilePicture(pic);

		assertEquals(pic, playerUnderTest.getProfilePicture());
	}

	@Test
	public void gamesWon() {
		int gamesWon = random.nextInt(200) + 1;
		playerUnderTest.setGamesWon(gamesWon);

		assertEquals(gamesWon, playerUnderTest.getGamesWon());
	}

	@Test
	public void gamesPlayed() {
		int gamesPlayed = random.nextInt(200) + 1;
		playerUnderTest.setGamesPlayed(gamesPlayed);

		assertEquals(gamesPlayed, playerUnderTest.getGamesPlayed());
	}

	@Test
	public void lobbyId() {
		int lobbyId = random.nextInt(200) + 1;
		playerUnderTest.setLobbyID(lobbyId);

		assertEquals(lobbyId, playerUnderTest.getLobbyID());
	}

	@Test
	public void highScore() {
		int highScore = random.nextInt(200) + 1;
		playerUnderTest.setHighScore(highScore);

		assertEquals(highScore, playerUnderTest.getHighScore());
	}

	@Test
	public void equals() {
		Player p2 = new Player();
		p2.setId(playerUnderTest.getId());

		assertTrue(playerUnderTest.equals(p2));

		p2.setId(3L);

		assertFalse(playerUnderTest.equals(p2));
	}

}
