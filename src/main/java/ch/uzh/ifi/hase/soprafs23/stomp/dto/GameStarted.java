package ch.uzh.ifi.hase.soprafs23.stomp.dto;

public class GameStarted {
    private String event = "started";
    private int id;

    public GameStarted(){}

    public GameStarted(int id){
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEvent() {return this.event;}
}

