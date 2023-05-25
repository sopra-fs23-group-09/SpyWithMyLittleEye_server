package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.entity.Game;

import java.util.HashMap;
import java.util.Map;

public class GameRepository {
	private Map<Integer, Game> gameRepositoryByID = new HashMap<>();
	private static GameRepository INSTANCE = new GameRepository();

	private GameRepository() {
		gameRepositoryByID = new HashMap<>();
	}

	////// External interface
	public static void reset() {
		INSTANCE = new GameRepository();
	}

	public static void addGame(Game game) {
		INSTANCE.addGame_internal(game);
	}

	public static Game getGameById(int gameId) {
		return INSTANCE.getGameById_internal(gameId);
	}

	public static void deleteGame(int gameId) {
		INSTANCE.deleteGame_internal(gameId);
	}

	////// INTERNALS

	private void addGame_internal(Game game) {
		gameRepositoryByID.put(game.getId(), game);
	}

	private Game getGameById_internal(int gameId) {
		return gameRepositoryByID.get(gameId);
	}

	private void deleteGame_internal(int gameId) {
		Game g = gameRepositoryByID.get(gameId);
		if (g == null)
			return;
		gameRepositoryByID.remove(gameId);
	}
}
