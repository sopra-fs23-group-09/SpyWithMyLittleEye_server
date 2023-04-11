package ch.uzh.ifi.hase.soprafs23.stomp.dto;

public class GuessIn {
    private String guess;
    private String id;

    public String getGuess() {
        return guess;
    }

    public Long getId() {
        return Long.valueOf(id);
    }
}
