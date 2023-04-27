package ch.uzh.ifi.hase.soprafs23.stomp.dto;

public class GuessIn {
    private String guess;
    private String id;

    public GuessIn() {
    }

    public GuessIn(String guess, String id) {
        this.guess = guess;
        this.id = id;
    }

    public String getGuess() {
        return guess;
    }

    public void setGuess(String guess) {
        this.guess = guess;
    }

    public Long getId() {
        return Long.valueOf(id);
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return String.format("%s [id=%s, guess=%s]", this.getClass().getName(), id, guess);
    }
}
