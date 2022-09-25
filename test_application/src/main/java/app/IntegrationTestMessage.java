package app;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class IntegrationTestMessage {
    private String integrationTestMessage;

    @JsonCreator
    public IntegrationTestMessage(@JsonProperty("integrationTestMessage") String integrationTestMessage) {
        this.integrationTestMessage = integrationTestMessage;
    }

    @JsonProperty("integrationTestMessage")
    public String getIntegrationTestMessage() {
        return integrationTestMessage;
    }
}
