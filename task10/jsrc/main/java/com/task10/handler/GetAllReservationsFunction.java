package com.task10.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.task10.dao.ReservationDao;
import com.task10.entity.Reservation;
import com.task10.entity.ReservationList;
import org.apache.http.HttpStatus;

import java.io.IOException;
import java.util.List;

public class GetAllReservationsFunction extends CommonTools implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final ReservationDao reservationDao;

    protected GetAllReservationsFunction(ReservationDao reservationDao){
        this.reservationDao = reservationDao;
    }

    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) {
        context.getLogger().log("apiGatewayProxyRequestEvent = " + apiGatewayProxyRequestEvent);
        try {

            List<Reservation> allReservations = reservationDao.getAllReservations();

            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(HttpStatus.SC_OK)
                    .withBody(String.format(objectMapper.writeValueAsString(new ReservationList(allReservations))));
        } catch (IOException e) {
            e.printStackTrace();
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                    .withBody(String.format(SIMPLE_MESSAGE_JSON_TEMPLATE, "error", "Internal server error :: " + e.getMessage()));
        }
    }
}
