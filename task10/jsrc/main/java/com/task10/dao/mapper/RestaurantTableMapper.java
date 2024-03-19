package com.task10.dao.mapper;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemUtils;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.task10.entity.Reservation;
import com.task10.entity.RestaurantTable;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class RestaurantTableMapper {

    private static final String PRIMARY_KEY = "id";
    private static final String TABLE_NUMBER = "number";
    private static final String PLACES_AMOUNT = "places";
    private static final String IS_VIP = "isVip";
    private static final String MIN_ORDER = "minOrder";
    private static final String LAST_BOOKING_DATE = "lastBookingDate";
    private static final String LAST_BOOKING_BILL = "lastBookingBill";

    public static Item restaurantTableToDynamoDbItem(RestaurantTable restaurantTable){
        Map<String, AttributeValue> attributeValueMap = new HashMap<>();

        attributeValueMap.put(PRIMARY_KEY, new AttributeValue().withN(restaurantTable.getId().toString()));
        attributeValueMap.put(TABLE_NUMBER, new AttributeValue().withN(restaurantTable.getNumber().toString()));

        Optional.ofNullable(restaurantTable.getPlaces()).ifPresent(places -> attributeValueMap.put(PLACES_AMOUNT, new AttributeValue().withN(places.toString())));
        Optional.ofNullable(restaurantTable.isVip()).ifPresent(isVip -> attributeValueMap.put(IS_VIP, new AttributeValue().withBOOL(isVip)));
        Optional.ofNullable(restaurantTable.getMinOrder()).ifPresent(minOrder -> attributeValueMap.put(MIN_ORDER, new AttributeValue().withN(minOrder.toString())));
        Optional.ofNullable(restaurantTable.getLastBookingDate()).ifPresent(lastBookingDate -> attributeValueMap.put(LAST_BOOKING_DATE, new AttributeValue(lastBookingDate)));
        Optional.ofNullable(restaurantTable.getLastBookingBill()).ifPresent(lastBookingBill -> attributeValueMap.put(LAST_BOOKING_BILL, new AttributeValue().withN(lastBookingBill.toString())));

        return ItemUtils.toItem(attributeValueMap);
    }

    public static RestaurantTable dynamoDbItemToRestaurantTable(Item restaurantTableItem) {
        return new RestaurantTable()
                .withId(restaurantTableItem.getInt(PRIMARY_KEY))
                .withNumber(restaurantTableItem.getInt(TABLE_NUMBER))
                .withPlaces(restaurantTableItem.getInt(PLACES_AMOUNT))
                .withVip(restaurantTableItem.getBOOL(IS_VIP))
                .withMinOrder(Optional.ofNullable(restaurantTableItem.getNumber(MIN_ORDER)).map(BigDecimal::intValue).orElse(null))
                .withLastBookingDate(restaurantTableItem.getString(LAST_BOOKING_DATE))
                .withLastBookingBill(Optional.ofNullable(restaurantTableItem.getNumber(LAST_BOOKING_BILL)).map(BigDecimal::intValue).orElse(null));
    }
}
