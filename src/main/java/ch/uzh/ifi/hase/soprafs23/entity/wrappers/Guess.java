package ch.uzh.ifi.hase.soprafs23.entity.wrappers;

public class Guess {

    private String guesserName;
    private String guess;

    public Guess(String guesserName, String guess){
        this.guesserName = guesserName;
        this.guess = guess;
    }

    public String getGuesserName(){
        return guesserName;
    }
    public String getGuess(){
        return guess;
    }

}