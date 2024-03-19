package com.task05;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonIgnoreProperties
public class EventRequest {

    private final Integer principalId;
    private final Map<String, String> content;

    @JsonCreator()
    public EventRequest(
            @JsonProperty(value = "principalId", required = true) Integer principalId,
            @JsonProperty(value = "content", required = true) Map<String, String> content) {
        this.principalId = principalId;
        this.content = content;
    }

    public int getPrincipalId() {
        return principalId;
    }

    public Map<String, String> getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "EventRequest{" +
                "principalId=" + principalId +
                ", content=" + content +
                '}';
    }
}
