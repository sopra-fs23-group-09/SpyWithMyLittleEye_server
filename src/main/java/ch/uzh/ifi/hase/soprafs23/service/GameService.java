package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.Role;
import ch.uzh.ifi.hase.soprafs23.controller.GameStompController;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.wrappers.Guess;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;

public class GameService {
    private final Logger log = LoggerFactory.getLogger(GameService.class);


    public void saveSpiedObjectInfo(int gameId, String keyword){
        Game game = getGame(gameId);
        game.setKeyword(keyword);
    }

    public List<Guess> checkGuessAndAllocatePoints(int gameId, Player player, String guess, Date guessTime){
        Game game = getGame(gameId);
        log.info("checking guess {} with keyword {}", guess, game.getKeyword());
        if (game.checkGuess(guess)){
            log.info("{} and {} are matching", guess, game.getKeyword());
            guess = "CORRECT";
            game.allocatePoints(player, guessTime);
        }
        game.storeGuess(player.getUsername(), guess);
        return game.getGuesses();
    }

    public void endRoundIfAllPlayersGuessedCorrectly(GameStompController conG, int gameId){
        Game game = getGame(gameId);
        game.endRoundIfAllUsersGuessedCorrectly(conG);
    }

    public void handleGameOver(int gameId, boolean goodEnd){
        Game game = getGame(gameId);
        if (goodEnd){
            game.updatePointsIfGameEnded();
        }
        GameRepository.deleteGame(gameId);
    }

    public void nextRound(int gameId){
        getGame(gameId).nextRound();
    }

    public Role getRole(int gameId, Long playerId){
        Game game = getGame(gameId);
        return game.getRole(playerId);
    }

    public void runTimer(GameStompController conG, int gameId){
        Game game = getGame(gameId);
        game.runTimer(conG);
    }

    public float getDuration(int gameId){
        Game game = getGame(gameId);
        return game.getDuration();
    }

    public Date initializeStartTime(int gameId){
        Date startTime = new Date();
        getGame(gameId).initializeStartTime(startTime);
        return startTime;
    }

    public int getCurrentRoundNr(int gameId){ return getGame(gameId).getCurrentRoundNr(); }

    public int getTotalNrRounds(int gameId){ return getGame(gameId).getAmountRounds(); }

    public Game getGame(int gameId) {
        Game game = GameRepository.getGameById(gameId);
        if (game == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This game doesn't exist.");
        }
        return game;
    }
}
