package ch.uzh.ifi.hase.soprafs23.rest.dto;

public class LobbyPostDTO {
    private int amountRounds;
    private float time;

    public int getAmountRounds() {
        return amountRounds;
    }

    public void setAmountRounds(String amountRounds) {
        this.amountRounds = Integer.parseInt(amountRounds);
    }

    public void setAmountRounds(int amountRounds) {
        this.amountRounds = amountRounds;
    }
    public void setTime(float time){
        this.time = time;
    }
    public float getTime(){
        return time;
    }
}
