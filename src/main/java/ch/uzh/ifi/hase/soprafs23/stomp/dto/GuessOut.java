package ch.uzh.ifi.hase.soprafs23.stomp.dto;

import ch.uzh.ifi.hase.soprafs23.entity.wrappers.Guess;

import java.util.List;

public class GuessOut {

    private List<Guess> guesses;

    public GuessOut(List<Guess> guesses){
        this.guesses = guesses;
    }

}
