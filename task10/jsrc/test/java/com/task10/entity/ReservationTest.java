package com.task10.entity;

import org.junit.Assert;
import org.junit.Test;


public class ReservationTest {

    @Test
    public void hasNoConflict() {
        Reservation reservationA = new Reservation()
                .withTableNumber("1")
                .withDate("2023-11-27")
                .withSlotTimeStart("10:00")
                .withSlotTimeEnd("13:00");

        Reservation reservationB = new Reservation()
                .withTableNumber("1")
                .withDate("2023-11-27")
                .withSlotTimeStart("14:00")
                .withSlotTimeEnd("17:00");

        Assert.assertFalse(reservationA.hasConflict(reservationB));
        Assert.assertFalse(reservationB.hasConflict(reservationA));
    }

    @Test
    public void hasTimeSlotIntersectionConflict() {
        Reservation reservationA = new Reservation()
                .withTableNumber("1")
                .withDate("2023-11-27")
                .withSlotTimeStart("10:00")
                .withSlotTimeEnd("13:00");

        Reservation reservationB = new Reservation()
                .withTableNumber("1")
                .withDate("2023-11-27")
                .withSlotTimeStart("12:00")
                .withSlotTimeEnd("15:00");

        Assert.assertTrue(reservationA.hasConflict(reservationB));
        Assert.assertTrue(reservationB.hasConflict(reservationA));
    }

    @Test
    public void hasTimeSlotIncludedConflict() {
        Reservation reservationA = new Reservation()
                .withTableNumber("1")
                .withDate("2023-11-27")
                .withSlotTimeStart("10:00")
                .withSlotTimeEnd("13:00");

        Reservation reservationB = new Reservation()
                .withTableNumber("1")
                .withDate("2023-11-27")
                .withSlotTimeStart("11:00")
                .withSlotTimeEnd("12:00");

        Assert.assertTrue(reservationA.hasConflict(reservationB));
        Assert.assertTrue(reservationB.hasConflict(reservationA));
    }

    @Test
    public void hasTimeSlotBorderConflict() {
        Reservation reservationA = new Reservation()
                .withTableNumber("1")
                .withDate("2023-11-27")
                .withSlotTimeStart("10:00")
                .withSlotTimeEnd("13:00");

        Reservation reservationB = new Reservation()
                .withTableNumber("1")
                .withDate("2023-11-27")
                .withSlotTimeStart("13:00")
                .withSlotTimeEnd("16:00");

        Assert.assertTrue(reservationA.hasConflict(reservationB));
        Assert.assertTrue(reservationB.hasConflict(reservationA));
    }

    @Test
    public void whenTimeSlotsIntersectButDifferentTableNumber_thenHasNoConflict() {
        Reservation reservationA = new Reservation()
                .withTableNumber("1")
                .withDate("2023-11-27")
                .withSlotTimeStart("10:00")
                .withSlotTimeEnd("13:00");

        Reservation reservationB = new Reservation()
                .withTableNumber("2")
                .withDate("2023-11-27")
                .withSlotTimeStart("12:00")
                .withSlotTimeEnd("15:00");

        Assert.assertFalse(reservationA.hasConflict(reservationB));
        Assert.assertFalse(reservationB.hasConflict(reservationA));
    }
}