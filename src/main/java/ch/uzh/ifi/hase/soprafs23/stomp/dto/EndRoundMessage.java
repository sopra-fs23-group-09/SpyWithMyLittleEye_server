package ch.uzh.ifi.hase.soprafs23.stomp.dto;

public class EndRoundMessage {
    private String endRoundMessage;

    public EndRoundMessage(String endRoundMessage){
        this.endRoundMessage = endRoundMessage;
    }

    public String getEndRoundMessage() {
        return endRoundMessage;
    }
}
