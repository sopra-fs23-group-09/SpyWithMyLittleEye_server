package ch.uzh.ifi.hase.soprafs23.stomp.dto;

import ch.uzh.ifi.hase.soprafs23.constant.Role;

public class DropOutMessage {
    String name;
    Role role;
    boolean host;
    int newHostId;
    boolean endGame;

    public DropOutMessage(String name, Role role, boolean host, int newHostId, boolean endGame){
        this.name = name;
        this.role = role;
        this.host = host;
        this.newHostId = newHostId;
        this.endGame = endGame;
    }

    public String getName(){
        return name;
    }
    public Role getRole(){
        return role;
    }
    public boolean getHost(){
        return host;
    }
    public int getNewHostId(){
        return newHostId;
    }
    public boolean getEndGame(){
        return endGame;
    }
}
