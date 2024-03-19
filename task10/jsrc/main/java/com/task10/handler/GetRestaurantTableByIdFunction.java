package com.task10.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.task10.dao.RestaurantTableDao;
import com.task10.entity.RestaurantTable;
import org.apache.http.HttpStatus;

import java.util.Map;
import java.util.Optional;

public class GetRestaurantTableByIdFunction extends CommonTools implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final RestaurantTableDao restaurantTableDao;

    protected GetRestaurantTableByIdFunction(RestaurantTableDao restaurantTableDao) {
        this.restaurantTableDao = restaurantTableDao;
    }

    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) {
        context.getLogger().log("apiGatewayProxyRequestEvent = " + apiGatewayProxyRequestEvent);

        try {
            Map<String, String> pathParameters = apiGatewayProxyRequestEvent.getPathParameters();
            String tableId = Optional.ofNullable(pathParameters)
                    .map(parameters -> parameters.get("tableId")).orElse("");
            context.getLogger().log("tableId = " + tableId);

            RestaurantTable restaurantTable = restaurantTableDao.getRestaurantTableById(Integer.parseInt(tableId))
                    .orElseThrow(() -> new Exception(String.format("Table with id=%s not found.", tableId)));
            context.getLogger().log("restaurantTable = " + restaurantTable);

            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(HttpStatus.SC_OK)
                    .withBody(String.format(objectMapper.writeValueAsString(restaurantTable)));
        } catch (Exception e) {
            e.printStackTrace();
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(HttpStatus.SC_BAD_REQUEST)
                    .withBody(String.format(SIMPLE_MESSAGE_JSON_TEMPLATE, "error", e.getMessage()));
        }
    }
}
