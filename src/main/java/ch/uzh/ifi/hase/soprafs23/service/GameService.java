package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Lobby;
import ch.uzh.ifi.hase.soprafs23.entity.Round;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.LobbyRepository;

import java.util.Date;

public class GameService {

    public void setKeywordAndColor(int lobbyId, String keyword, String color){
        Lobby lobby = LobbyRepository.getLobbyById(lobbyId);
        lobby.setColorAndKeyword(keyword, color);
    }

    //to-do: use Levenshtein distance!
    public boolean checkGuess(int lobbyId, String guess){
        String keyword = LobbyRepository.getLobbyById(lobbyId).getGame().getCurrentRound().getKeyword();

        //return calculateLevenshteinDistance(guess.toLowerCase(), keyword.toLowerCase()) < 2;
        return guess.equalsIgnoreCase(keyword);
    }

    public void allocatePoints(int lobbyId, User user){
        Date guessTime = new Date();
        Date startDateRound = LobbyRepository.getLobbyById(lobbyId).getGame().getCurrentRound().getStartTime();

        //note c: adjust formula to calculate points, now: 500 - seconds needed to guess
        int points =  (int) (500 - (guessTime.getTime()-startDateRound.getTime())/1000);

        user.setPointsCurrentRound(points);
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
