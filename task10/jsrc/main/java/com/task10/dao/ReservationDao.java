package com.task10.dao;

import com.task10.entity.Reservation;

import java.util.List;

public interface ReservationDao {
    List<Reservation> getAllReservations();
    String putReservation(Reservation reservation);
}
