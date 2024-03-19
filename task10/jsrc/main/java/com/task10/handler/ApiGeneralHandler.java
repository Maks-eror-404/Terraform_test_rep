package com.task10.handler;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.syndicate.deployment.annotations.environment.EnvironmentVariable;
import com.syndicate.deployment.annotations.environment.EnvironmentVariables;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.task10.dao.ReservationDao;
import com.task10.dao.RestaurantTableDao;
import com.task10.dao.impl.DynamoReservationDao;
import com.task10.dao.impl.DynamoRestaurantTableDao;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import com.syndicate.deployment.model.DeploymentRuntime;

import java.util.HashMap;
import java.util.Map;

@EnvironmentVariables(value = {
        @EnvironmentVariable(key = "pool", value = "${booking_userpool}"),
        @EnvironmentVariable(key = "region", value = "${region}"),
        @EnvironmentVariable(key = "reservations", value = "${reservations_table}"),
        @EnvironmentVariable(key = "tables", value = "${tables_table}")})
@LambdaHandler(
        lambdaName = "api_handler",
        roleName = "lambda-role",
        runtime=DeploymentRuntime.JAVA11
)
public class ApiGeneralHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final Map<String, RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent>> handlersByMethod = initHandlers();
    private final Map<String, String> headers = initHeaders();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) {
        return getHandler(apiGatewayProxyRequestEvent).handleRequest(apiGatewayProxyRequestEvent, context).withHeaders(headers);
    }

    private RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> getHandler(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent) {
        return handlersByMethod.get(getMethodName(apiGatewayProxyRequestEvent));
    }

    private String getMethodName(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent) {
        String httpMethod = apiGatewayProxyRequestEvent.getRequestContext().getHttpMethod();
        String path = apiGatewayProxyRequestEvent.getPath().split("/")[1];
        Map<String, String> pathParameters = apiGatewayProxyRequestEvent.getPathParameters();
        return path + (pathParameters == null || pathParameters.isEmpty() ? "" : "/{variable}") + "::" + httpMethod;
    }

    private Map<String, RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent>> initHandlers() {

        final String userPoolName = System.getenv("pool");
        final DynamoDB dynamoDB = new DynamoDB(AmazonDynamoDBAsyncClientBuilder.standard().withRegion(System.getenv("region")).build());
        final ReservationDao reservationDao = new DynamoReservationDao(dynamoDB.getTable(System.getenv("reservations")));
        final RestaurantTableDao restaurantTableDao = new DynamoRestaurantTableDao(dynamoDB.getTable(System.getenv("tables")));
        final CognitoIdentityProviderClient cognitoClient = CognitoIdentityProviderClient.builder().region(Region.of(System.getenv("region"))).build();

        Map<String, RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent>> requestHandlerMap = new HashMap<>();
        requestHandlerMap.put("reservations::POST", new CreateReservationFunction(reservationDao, restaurantTableDao));
        requestHandlerMap.put("tables::POST", new CreateRestaurantTableFunction(restaurantTableDao));
        requestHandlerMap.put("reservations::GET", new GetAllReservationsFunction(reservationDao));
        requestHandlerMap.put("tables::GET", new GetAllRestaurantTablesFunction(restaurantTableDao));
        requestHandlerMap.put("tables/{variable}::GET", new GetRestaurantTableByIdFunction(restaurantTableDao));
        requestHandlerMap.put("signin::POST", new SignInFunction(cognitoClient, userPoolName));
        requestHandlerMap.put("signup::POST", new SignUpFunction(cognitoClient, userPoolName));

        return requestHandlerMap;
    }

    private Map<String, String> initHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Access-Control-Allow-Headers", "Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token");
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Access-Control-Allow-Methods", "*");
        headers.put("Accept-Version", "*");
        return headers;
    }
}
