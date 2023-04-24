package ch.uzh.ifi.hase.soprafs23.stomp.dto;

public class Hint {
    private String hint;

    public Hint(){}

    public Hint(String hint){
        this.hint = hint;
    }

    public String getHint() {
        return hint;
    }

    @Override
    public String toString() {
        return String.format("%s [hint=%s]", Hint.class.getName(), hint);
    }
}