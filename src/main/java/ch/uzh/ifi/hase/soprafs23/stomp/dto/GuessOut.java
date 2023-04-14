package ch.uzh.ifi.hase.soprafs23.stomp.dto;

public class GuessOut {
    private String username;
    private String guess;

    public GuessOut(String username, String guess){
        this.username = username;
        this.guess = guess;
    }

    public String getGuess() {
        return guess;
    }

    public String getUsername() {
        return username;
    }
}
