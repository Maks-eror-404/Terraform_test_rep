package com.task10.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties
public class ReservationList {
    @JsonProperty("reservations")
    private final List<Reservation> reservations;

    public ReservationList(List<Reservation> reservations) {
        this.reservations = reservations;
    }
}
