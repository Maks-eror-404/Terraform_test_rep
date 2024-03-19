package com.task10.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@JsonIgnoreProperties
public class Reservation {

    @JsonProperty("tableNumber")
    private Integer tableNumber;
    @JsonProperty("clientName")
    private String clientName;
    @JsonProperty("phoneNumber")
    private String phoneNumber;
    @JsonProperty("date")
    private String date;
    @JsonProperty("slotTimeStart")
    private String slotTimeStart;
    @JsonProperty("slotTimeEnd")
    private String slotTimeEnd;

    public Reservation withTableNumber(Integer tableNumber) {
        this.tableNumber = tableNumber;
        return this;
    }

    public Reservation withClientName(String clientName) {
        this.clientName = clientName;
        return this;
    }

    public Reservation withPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public Reservation withDate(String date) {
        this.date = date;
        return this;
    }

    public Reservation withSlotTimeStart(String slotTimeStart) {
        this.slotTimeStart = slotTimeStart;
        return this;
    }

    public Reservation withSlotTimeEnd(String slotTimeEnd) {
        this.slotTimeEnd = slotTimeEnd;
        return this;
    }

    public Integer getTableNumber() {
        return tableNumber;
    }

    public String getClientName() {
        return clientName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getDate() {
        return date;
    }

    public String getSlotTimeStart() {
        return slotTimeStart;
    }

    public String getSlotTimeEnd() {
        return slotTimeEnd;
    }

    public boolean hasConflict(Reservation other) {
        if (!Objects.equals(this.getTableNumber(), other.getTableNumber())) {
            return false;
        }

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        LocalDateTime thisSlotStart = LocalDateTime.parse(date + " " + slotTimeStart, dateFormatter.withZone(ZoneOffset.UTC));
        LocalDateTime thisSlotEnd = LocalDateTime.parse(date + " " + slotTimeEnd, dateFormatter.withZone(ZoneOffset.UTC));
        LocalDateTime otherSlotStart = LocalDateTime.parse(other.date + " " + other.slotTimeStart, dateFormatter.withZone(ZoneOffset.UTC));
        LocalDateTime otherSlotEnd = LocalDateTime.parse(other.date + " " + other.slotTimeEnd, dateFormatter.withZone(ZoneOffset.UTC));

        return !thisSlotStart.isAfter(otherSlotEnd) && !otherSlotStart.isAfter(thisSlotEnd);

    }

}
