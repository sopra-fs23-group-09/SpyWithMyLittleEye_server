package ch.uzh.ifi.hase.soprafs23.stomp.dto;

public class RoundNr {
    private int currentRound;
    private int totalRounds;

    public RoundNr(int currentRound, int totalRounds){
        this.currentRound = currentRound;
        this.totalRounds = totalRounds;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public int getTotalRounds() {
        return totalRounds;
    }
}
