package com.task10.dao.impl;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.internal.IteratorSupport;
import com.task10.dao.ReservationDao;
import com.task10.dao.mapper.ReservationMapper;
import com.task10.entity.Reservation;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DynamoReservationDao implements ReservationDao {

    private final Table table;

    public DynamoReservationDao(Table table) {
        this.table = table;
    }

    @Override
    public List<Reservation> getAllReservations() {
        ItemCollection<ScanOutcome> scan = table.scan();
        IteratorSupport<Item, ScanOutcome> iterator = scan.iterator();
        ArrayList<Reservation> reservations = new ArrayList<>();
        while (iterator.hasNext()) {
            Item reservationItem = iterator.next();
            reservations.add(ReservationMapper.dynamoDbItemToReservation(reservationItem));
        }
        return reservations;
    }

    @Override
    public String putReservation(Reservation reservation) {
        String key = UUID.randomUUID().toString();
        table.putItem(ReservationMapper.reservationToDynamoDbItem(reservation, key));
        return key;
    }
}
