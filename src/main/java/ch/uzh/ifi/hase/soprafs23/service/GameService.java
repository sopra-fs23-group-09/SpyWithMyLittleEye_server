package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.Role;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.wrappers.Guess;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;
import java.util.Timer;

public class GameService {

    public void saveSpiedObjectInfo(int gameId, String keyword){
        Game game = getGame(gameId);
        game.setKeyword(keyword);
    }

    public List<Guess> checkGuessAndAllocatePoints(int gameId, User user, String guess, Date guessTime){
        Game game = getGame(gameId);
        if (game.checkGuess(guess)){
            guess = "CORRECT";
            game.allocatePoints(user, guessTime);
            if (game.didAllPlayersGuessCorrectly()){

            }
        }
        game.storeGuess(user.getUsername(), guess);
        return game.getGuesses();
    }

    public boolean allPlayersGuessedCorrectly(int gameId){
        Game game = getGame(gameId);
        if (game.didAllPlayersGuessCorrectly()){
            return true;
        }
        return false;
    }

    public void resetRoundFields(int gameId){
        Game game = getGame(gameId);
        game.resetKeywordStarttimeNrplayerguessedcorrectly();
    }
    public List<Guess> getGuesses(int gameId){
        Game game = getGame(gameId);
        return game.getGuesses();
    }
    public Role getRole(int gameId, Long playerId){
        Game game = getGame(gameId);
        return game.getRole(playerId);
    }

    public Date initializeStartTime(int gameId){
        Date startTime = new Date();
        getGame(gameId).initializeStartTime(startTime);
        return startTime;
    }

    public int getCurrentRoundNr(int gameId){ return getGame(gameId).getCurrentRoundNr(); }
    public int getTotalNrRounds(int gameId){ return getGame(gameId).getAmountRounds(); }

    private Game getGame(int gameId) {
        Game game = GameRepository.getGameById(gameId);
        if (game == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This game doesn't exist.");
        }
        return game;
    }
    public void deleteGame(int gameId){
        GameRepository.deleteGame(gameId);
    }

    private int calculateLevenshteinDistance(String string1, String string2){
        int lengthString1 = string1.length();
        int lengthString2 = string2.length();

        if (lengthString1 == 0){ // base case 1: string 1 is empty
            return lengthString2;
        } else if (lengthString2 == 0) { //base case 2: string 2 is empty
            return lengthString1;
        } else if (string1.charAt(lengthString1 - 1) == string2.charAt(lengthString2 - 1)) { // case A: the last character match
            return calculateLevenshteinDistance(string1.substring(0, lengthString1 - 1),string2.substring(0, lengthString2 - 1));
        } else { // case B: the last character don't match: is the shortest way to delete, insert or substitute a character?
            // insertion
            int distance_insertion = calculateLevenshteinDistance(string1, string2.substring(lengthString2 - 1));

            // deletion
            int distance_deletion = calculateLevenshteinDistance(string1.substring(0, lengthString1 - 1), string2);

            // substitution
            int distance_substitution = calculateLevenshteinDistance(string1.substring(0, lengthString1 - 1), string2.substring(0, lengthString2 - 1));

            return 1 + Math.min(Math.min(distance_insertion, distance_deletion), distance_substitution); //add 1 to the Levenshtein distance of the substrings because deletion, insertion or substitution was needed
        }
    }
}
