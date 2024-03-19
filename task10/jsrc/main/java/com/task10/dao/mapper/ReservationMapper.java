package com.task10.dao.mapper;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemUtils;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.task10.entity.Reservation;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ReservationMapper {

    private static final String PRIMARY_KEY = "id";
    private static final String TABLE_NUMBER = "tableNumber";
    private static final String CLIENT_NAME = "clientName";
    private static final String PHONE_NUMBER = "phoneNumber";
    private static final String DATE = "date";
    private static final String SLOT_TIME_START = "slotTimeStart";
    private static final String SLOT_TIME_END = "slotTimeEnd";

    public static Item reservationToDynamoDbItem(Reservation reservation, String key){
        Map<String, AttributeValue> attributeValueMap = new HashMap<>();

        attributeValueMap.put(PRIMARY_KEY, new AttributeValue(key));
        Optional.ofNullable(reservation.getTableNumber()).ifPresent(tableNumber -> attributeValueMap.put(TABLE_NUMBER, new AttributeValue().withN(tableNumber.toString())));
        Optional.ofNullable(reservation.getClientName()).ifPresent(clientName -> attributeValueMap.put(CLIENT_NAME, new AttributeValue(clientName)));
        Optional.ofNullable(reservation.getPhoneNumber()).ifPresent(phoneNumber -> attributeValueMap.put(PHONE_NUMBER, new AttributeValue(phoneNumber)));
        Optional.ofNullable(reservation.getDate()).ifPresent(date -> attributeValueMap.put(DATE, new AttributeValue(date)));
        Optional.ofNullable(reservation.getSlotTimeStart()).ifPresent(slotTimeStart -> attributeValueMap.put(SLOT_TIME_START, new AttributeValue(slotTimeStart)));
        Optional.ofNullable(reservation.getSlotTimeEnd()).ifPresent(slotTimeEnd -> attributeValueMap.put(SLOT_TIME_END, new AttributeValue(slotTimeEnd)));

        return ItemUtils.toItem(attributeValueMap);
    }

    public static Reservation dynamoDbItemToReservation(Item reservationItem) {
        return new Reservation()
                .withTableNumber(reservationItem.getInt(TABLE_NUMBER))
                .withClientName(reservationItem.getString(CLIENT_NAME))
                .withPhoneNumber(reservationItem.getString(PHONE_NUMBER))
                .withDate(reservationItem.getString(DATE))
                .withSlotTimeStart(reservationItem.getString(SLOT_TIME_START))
                .withSlotTimeEnd(reservationItem.getString(SLOT_TIME_END));
    }
}
