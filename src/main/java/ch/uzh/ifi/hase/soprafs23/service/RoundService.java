package ch.uzh.ifi.hase.soprafs23.service;


import ch.uzh.ifi.hase.soprafs23.entity.Round;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.RoundRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@Transactional
public class RoundService {
    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final RoundRepository roundRepository;

    @Autowired
    public RoundService(@Qualifier("roundRepository") RoundRepository roundRepository) {
        this.roundRepository = roundRepository;
    }

    public void createRound(List<User> users){
        //TODO
    }

    // if Levenshtein distance is less than 2 then correct, else false
    // (guess and keyword are first converted to lowercase)
    // Levenshtein distance: number of insertions, deletions and substitutions needed to convert one string to another
    public boolean checkGuess(String guess, String keyword){
        return calculateLevenshteinDistance(guess.toLowerCase(), keyword.toLowerCase()) < 2;
    }

    // recursive calculation of the Levenshtein distance
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

    public void setKeywordAndColor(Round round, String keyword, String color){
        round.setKeyword(keyword);
        round.setColor(color);
    }

    //What should this method do? award point? because in this case, why do we pass a boolean
    public void awardPoints(User user, String guess, boolean correct){
        //TODO
    }
    public void deleteRound(){
        //TODO
    }

    public boolean hasEveryoneGuessedCorrectly(Round round){
        return round.getUsers().size() == round.getNrCorrectGuesses();
    }
}
