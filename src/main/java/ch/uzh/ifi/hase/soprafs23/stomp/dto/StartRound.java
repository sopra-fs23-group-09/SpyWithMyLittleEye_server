package ch.uzh.ifi.hase.soprafs23.stomp.dto;

import java.util.Date;

public class StartRound {
    private String startTime;
    private int duration;
    public StartRound(String startTime, int duration){
        this.startTime =  startTime;
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }

    public String getStartTime() {
        return startTime;
    }
}
