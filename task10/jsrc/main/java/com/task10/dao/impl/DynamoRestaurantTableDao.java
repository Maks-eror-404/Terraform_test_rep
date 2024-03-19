package com.task10.dao.impl;

import com.amazonaws.services.dynamodbv2.document.AttributeUpdate;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.internal.IteratorSupport;
import com.task10.dao.RestaurantTableDao;
import com.task10.dao.mapper.RestaurantTableMapper;
import com.task10.entity.RestaurantTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DynamoRestaurantTableDao implements RestaurantTableDao {

    private final Table table;
    private static final String PRIMARY_KEY = "id";

    public DynamoRestaurantTableDao(Table table) {
        this.table = table;
    }

    @Override
    public List<RestaurantTable> getAllRestaurantTables() {
        ItemCollection<ScanOutcome> scan = table.scan();
        IteratorSupport<Item, ScanOutcome> iterator = scan.iterator();
        ArrayList<RestaurantTable> restaurantTables = new ArrayList<>();
        while (iterator.hasNext()) {
            Item reservationItem = iterator.next();
            restaurantTables.add(RestaurantTableMapper.dynamoDbItemToRestaurantTable(reservationItem));
        }
        return restaurantTables;
    }

    @Override
    public Optional<RestaurantTable> getRestaurantTableById(Integer id) {
        Item tableItem = table.getItem(PRIMARY_KEY, id);
        if (tableItem != null) {
            return Optional.of(RestaurantTableMapper.dynamoDbItemToRestaurantTable(tableItem));
        }
        return Optional.empty();
    }

    @Override
    public void updateLastBookingDate(Integer id, String lastBookingDate) {
        table.updateItem(PRIMARY_KEY, id, new AttributeUpdate("lastBookingDate").put(lastBookingDate));
    }

    @Override
    public void putRestaurantTable(RestaurantTable restaurantTable) {
        table.putItem(RestaurantTableMapper.restaurantTableToDynamoDbItem(restaurantTable));
    }
}
