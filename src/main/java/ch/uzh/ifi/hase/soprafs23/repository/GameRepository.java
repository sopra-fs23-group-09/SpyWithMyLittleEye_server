package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Lobby;

import java.util.HashMap;
import java.util.Map;

public class GameRepository {
    private static Map<Integer, Game> gameRepositoryByID = new HashMap<>();

    private GameRepository(){ }

    public static void addGame(Game game){
        gameRepositoryByID.put(game.getId(), game);
    }

    public static Game getGameById(int gameId){
        return gameRepositoryByID.get(gameId);
    }

    public static void deleteGame(int gameId){
        gameRepositoryByID.remove(gameId);
    }
}
