package ch.uzh.ifi.hase.soprafs23.entity.wrappers;

public class Guess {

    private String guesserName;
    private String guess;
    private int correct;

    public Guess(String guesserName, String guess, int correct){
        this.guesserName = guesserName;
        this.guess = guess;
        this.correct = correct;
    }

    public String getGuesserName(){
        return guesserName;
    }
    public String getGuess(){
        return guess;
    }

    public int getCorrect(){return correct;}
    public void setCorrect(int correct){this.correct = correct;}

    public void setGuesserName(String guesserName) {
        this.guesserName = guesserName;
    }

    public void setGuess(String guess) {
        this.guess = guess;
    }
}
