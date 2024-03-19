package com.task10.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.task10.dao.ReservationDao;
import com.task10.dao.RestaurantTableDao;
import com.task10.entity.Reservation;
import com.task10.entity.RestaurantTable;
import org.apache.http.HttpStatus;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public class CreateReservationFunction extends CommonTools implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final ReservationDao reservationDao;
    private final RestaurantTableDao restaurantTableDao;

    protected CreateReservationFunction(ReservationDao reservationDao, RestaurantTableDao restaurantTableDao) {
        this.reservationDao = reservationDao;
        this.restaurantTableDao = restaurantTableDao;
    }

    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) {
        try {
            String jsonPayload = apiGatewayProxyRequestEvent.getBody();
            Reservation newReservation = objectMapper.readValue(jsonPayload, Reservation.class);

            List<RestaurantTable> allRestaurantTables = restaurantTableDao.getAllRestaurantTables();
            Integer reservationTableId = allRestaurantTables.stream()
                    .filter(restaurantTable -> Objects.equals(restaurantTable.getNumber(), newReservation.getTableNumber()))
                    .findFirst()
                    .orElseThrow(() -> new Exception(String.format("Table with number %s not found", newReservation.getTableNumber())))
                    .getId();

            List<Reservation> allReservations = reservationDao.getAllReservations();

            boolean hasConflict = allReservations.stream()
                    .filter(existingReservation -> existingReservation.getTableNumber().equals(newReservation.getTableNumber()))
                    .filter(existingReservation -> existingReservation.getDate().equals(newReservation.getDate()))
                    .anyMatch(existingReservation -> existingReservation.hasConflict(newReservation));
            if (hasConflict) {
                return new APIGatewayProxyResponseEvent()
                        .withStatusCode(HttpStatus.SC_BAD_REQUEST)
                        .withBody(String.format(SIMPLE_MESSAGE_JSON_TEMPLATE, "error", "There is time slot conflict."));
            }

            String reservationId = reservationDao.putReservation(newReservation);
            restaurantTableDao.updateLastBookingDate(
                    reservationTableId,
                    ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT));

            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(HttpStatus.SC_OK)
                    .withBody(String.format(SIMPLE_MESSAGE_JSON_TEMPLATE, "reservationId", reservationId));
        } catch (Exception e) {
            e.printStackTrace();
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(HttpStatus.SC_BAD_REQUEST)
                    .withBody(String.format(SIMPLE_MESSAGE_JSON_TEMPLATE, "error", "Internal server error :: " + e.getMessage()));
        }
    }
}
