package com.task10.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties
public class RestaurantTableList {
    @JsonProperty("tables")
    private final List<RestaurantTable> tables;

    public RestaurantTableList(List<RestaurantTable> tables) {
        this.tables = tables;
    }
}
