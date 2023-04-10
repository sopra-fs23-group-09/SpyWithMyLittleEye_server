package ch.uzh.ifi.hase.soprafs23.rest.dto;

public class LobbyPostDTO {
    private int amountRounds;

    public int getAmountRounds() {
        return amountRounds;
    }

    public void setAmountRounds(String amountRounds) {
        this.amountRounds = Integer.parseInt(amountRounds);
    }

    public void setAmountRounds(int amountRounds) {
        this.amountRounds = amountRounds;
    }
}
