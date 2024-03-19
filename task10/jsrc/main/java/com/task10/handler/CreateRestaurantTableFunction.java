package com.task10.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.task10.dao.RestaurantTableDao;
import com.task10.entity.RestaurantTable;
import org.apache.http.HttpStatus;

public class CreateRestaurantTableFunction extends CommonTools implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final RestaurantTableDao restaurantTableDao;

    protected CreateRestaurantTableFunction(RestaurantTableDao restaurantTableDao) {
        this.restaurantTableDao = restaurantTableDao;
    }

    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) {
        context.getLogger().log("apiGatewayProxyRequestEvent = " + apiGatewayProxyRequestEvent);

        try {
            RestaurantTable restaurantTable = getRestaurantTable(apiGatewayProxyRequestEvent);

            restaurantTableDao.putRestaurantTable(restaurantTable);

            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(HttpStatus.SC_OK)
                    .withBody(String.format("{\"%s\": %d}", "id", restaurantTable.getId()));
        } catch (Exception e) {
            e.printStackTrace();
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(HttpStatus.SC_BAD_REQUEST)
                    .withBody(String.format(SIMPLE_MESSAGE_JSON_TEMPLATE, "error", "Internal server error :: " + e.getMessage()));
        }
    }

    private RestaurantTable getRestaurantTable(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent) throws Exception {
        String jsonPayload = apiGatewayProxyRequestEvent.getBody();
        return objectMapper.readValue(jsonPayload, RestaurantTable.class);
    }
}
