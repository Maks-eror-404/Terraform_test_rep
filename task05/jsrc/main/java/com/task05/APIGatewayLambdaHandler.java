package com.task05;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.syndicate.deployment.annotations.environment.EnvironmentVariable;
import com.syndicate.deployment.annotations.environment.EnvironmentVariables;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.model.DeploymentRuntime;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@EnvironmentVariables(value = {
        @EnvironmentVariable(key = "region", value = "${region}"),
        @EnvironmentVariable(key = "table", value = "${target_table}")})
@LambdaHandler(
        lambdaName = "api_handler",
        roleName = "task-05-lambda-role",
        runtime=DeploymentRuntime.JAVA11
)
public class APIGatewayLambdaHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final String ERROR_JSON_TEMPLATE = "{\"statusCode\": %d, \"errorMessage\": \"%s\"}";
    private final DynamoDB DYNAMO_DB = new DynamoDB(
            AmazonDynamoDBAsyncClientBuilder.standard().withRegion(System.getenv("region")).build());

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) {
        APIGatewayProxyResponseEvent apiResponse = new APIGatewayProxyResponseEvent();
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            EventRequest eventRequest = objectMapper.readValue(apiGatewayProxyRequestEvent.getBody(), EventRequest.class);

            Event event = new Event()
                    .withId(UUID.randomUUID().toString())
                    .withPrincipalId(eventRequest.getPrincipalId())
                    .withCreatedAt(ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT))
                    .withBody(eventRequest.getContent());

            persistEvent(event);

            EventResponse eventResponse = new EventResponse()
                    .withStatusCode(201)
                    .withEvent(event);

            apiResponse.withBody(objectMapper.writeValueAsString(eventResponse));
        } catch (Exception e) {
            apiResponse.setBody(String.format(ERROR_JSON_TEMPLATE, 400, e.getMessage()));
        }
        return apiResponse;
    }

    private void persistEvent(Event event) {
        getTargetTable().putItem(
                new Item()
                        .withPrimaryKey("id", event.getId())
                        .with("principalId", event.getPrincipalId())
                        .with("createdAt", event.getCreatedAt())
                        .with("body", event.getBody()));
    }

    private Table getTargetTable() {
        return DYNAMO_DB.getTable(System.getenv("table"));
    }
}
