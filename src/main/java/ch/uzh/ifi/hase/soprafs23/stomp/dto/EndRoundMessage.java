package ch.uzh.ifi.hase.soprafs23.stomp.dto;

public class EndRoundMessage {
    private String endRoundMessage;
    private int currentRound;
    private int amountOfRounds;

    public EndRoundMessage(String endRoundMessage, int amountOfRounds, int currentRound){
        this.endRoundMessage = endRoundMessage;
        this.currentRound = currentRound;
        this.amountOfRounds = amountOfRounds;
    }

    public String getEndRoundMessage() {
        return endRoundMessage;
    }
    public int getAmountOfRounds(){return amountOfRounds;}
    public int getCurrentRound(){return currentRound;}
}
