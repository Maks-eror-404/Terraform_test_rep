package com.task05;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class Event {

    @JsonProperty("id")
    private String id;
    @JsonProperty("principalId")
    private int principalId;
    @JsonProperty("createdAt")
    private String createdAt;
    @JsonProperty("body")
    private Map<String, String> body;

    public String getId() {
        return id;
    }

    public Event withId(String id) {
        this.id = id;
        return this;
    }

    public int getPrincipalId() {
        return principalId;
    }

    public Event withPrincipalId(int principalId) {
        this.principalId = principalId;
        return this;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public Event withCreatedAt(String createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Map<String, String> getBody() {
        return body;
    }

    public Event withBody(Map<String, String> body) {
        this.body = body;
        return this;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id='" + id + '\'' +
                ", principalId=" + principalId +
                ", createdAt='" + createdAt + '\'' +
                ", body=" + body +
                '}';
    }
}
