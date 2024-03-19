package com.task05;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EventResponse {

    @JsonProperty("statusCode")
    private int statusCode;
    @JsonProperty("event")
    private Event event;

    public EventResponse() {
    }

    public EventResponse withStatusCode(int statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public EventResponse withEvent(Event event) {
        this.event = event;
        return this;
    }

    @Override
    public String toString() {
        return "EventResponse{" +
                "statusCode=" + statusCode +
                ", event=" + event +
                '}';
    }
}
