package com.task10.dao;

import com.task10.entity.RestaurantTable;

import java.util.List;
import java.util.Optional;

public interface RestaurantTableDao {
    List<RestaurantTable> getAllRestaurantTables();
    Optional<RestaurantTable> getRestaurantTableById(Integer id);
    void updateLastBookingDate(Integer id, String Date);
    void putRestaurantTable(RestaurantTable restaurantTable);
}
