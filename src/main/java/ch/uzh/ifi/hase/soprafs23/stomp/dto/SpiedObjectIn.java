package ch.uzh.ifi.hase.soprafs23.stomp.dto;

public class SpiedObjectIn {

    private String keyword;
    private String color;

    public SpiedObjectIn(String keyword,String color){
        this.keyword = keyword;
        this.color = color;
    }

    public String getKeyword() {
        return keyword;
    }

    public String getColor() {
        return color;
    }
}
