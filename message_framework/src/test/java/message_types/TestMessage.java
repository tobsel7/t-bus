package message_types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TestMessage {

    private final String msg;

    @JsonCreator
    public TestMessage(@JsonProperty("msg") String msg) {
        this.msg = msg;
    }

    @JsonProperty("msg")
    public String getMsg() {
        return msg;
    }
}
