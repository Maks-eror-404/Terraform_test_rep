package com.task10.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties
public class RestaurantTable {
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("number")
    private Integer number;
    @JsonProperty("places")
    private Integer places;
    @JsonProperty("isVip")
    private Boolean isVip;
    @JsonProperty("minOrder")
    private Integer minOrder;
    @JsonProperty("lastBookingDate")
    private String lastBookingDate;
    @JsonProperty("lastBookingBill")
    private Integer lastBookingBill;


    public RestaurantTable withId(Integer id) {
        this.id = id;
        return this;
    }

    public RestaurantTable withNumber(Integer number) {
        this.number = number;
        return this;
    }

    public RestaurantTable withPlaces(Integer places) {
        this.places = places;
        return this;
    }

    public RestaurantTable withVip(Boolean vip) {
        isVip = vip;
        return this;
    }

    public RestaurantTable withMinOrder(Integer minOrder) {
        this.minOrder = minOrder;
        return this;
    }

    public RestaurantTable withLastBookingDate(String lastBookingDate) {
        this.lastBookingDate = lastBookingDate;
        return this;
    }

    public RestaurantTable withLastBookingBill(Integer lastBookingBill) {
        this.lastBookingBill = lastBookingBill;
        return this;
    }

    public Integer getId() {
        return id;
    }

    public Integer getNumber() {
        return number;
    }

    public Integer getPlaces() {
        return places;
    }

    @JsonProperty("isVip")
    public Boolean isVip() {
        return isVip;
    }

    public Integer getMinOrder() {
        return minOrder;
    }

    public String getLastBookingDate() {
        return lastBookingDate;
    }

    public Integer getLastBookingBill() {
        return lastBookingBill;
    }
}
