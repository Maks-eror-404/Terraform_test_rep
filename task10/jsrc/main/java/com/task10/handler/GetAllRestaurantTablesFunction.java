package com.task10.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.task10.dao.RestaurantTableDao;
import com.task10.entity.RestaurantTable;
import com.task10.entity.RestaurantTableList;
import org.apache.http.HttpStatus;

import java.io.IOException;
import java.util.List;

public class GetAllRestaurantTablesFunction extends CommonTools implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final RestaurantTableDao restaurantTableDao;

    protected GetAllRestaurantTablesFunction(RestaurantTableDao restaurantTableDao) {
        this.restaurantTableDao = restaurantTableDao;
    }

    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) {
        context.getLogger().log("apiGatewayProxyRequestEvent = " + apiGatewayProxyRequestEvent);
        try {

            List<RestaurantTable> allRestaurantTables = restaurantTableDao.getAllRestaurantTables();

            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(HttpStatus.SC_OK)
                    .withBody(String.format(objectMapper.writeValueAsString(new RestaurantTableList(allRestaurantTables))));
        } catch (IOException e) {
            e.printStackTrace();
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                    .withBody(String.format(SIMPLE_MESSAGE_JSON_TEMPLATE, "error", "Internal server error :: " + e.getMessage()));
        }
    }
}
