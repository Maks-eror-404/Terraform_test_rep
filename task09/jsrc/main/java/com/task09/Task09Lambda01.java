package com.task09;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lambda.layer.exchange.OpenMeteoSimpleApi;
import com.syndicate.deployment.annotations.LambdaUrlConfig;
import com.syndicate.deployment.annotations.environment.EnvironmentVariable;
import com.syndicate.deployment.annotations.environment.EnvironmentVariables;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.lambda.LambdaLayer;
import com.syndicate.deployment.model.ArtifactExtension;
import com.syndicate.deployment.model.DeploymentRuntime;
import com.syndicate.deployment.model.TracingMode;
import com.syndicate.deployment.model.lambda.url.AuthType;
import com.syndicate.deployment.model.lambda.url.InvokeMode;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@LambdaHandler(
        tracingMode = TracingMode.Active,
        lambdaName = "processor",
        roleName = "task-09-lambda-01-role",
        layers = {"sdk_layer"},
        runtime=DeploymentRuntime.JAVA11
)
@LambdaUrlConfig(
        authType = AuthType.NONE,
        invokeMode = InvokeMode.BUFFERED
)
@EnvironmentVariables(value = {
        @EnvironmentVariable(key = "region", value = "${region}"),
        @EnvironmentVariable(key = "table", value = "${target_table}")}
)
@LambdaLayer(
        layerName = "sdk_layer",
        libraries = {"lib/open-meteo-sdk-1.0.0.jar"},
        runtime = DeploymentRuntime.JAVA11,
        artifactExtension = ArtifactExtension.ZIP
)
public class Task09Lambda01 implements RequestHandler<Object, Map<String, Object>> {

    private final DynamoDB DYNAMO_DB = new DynamoDB(
            AmazonDynamoDBAsyncClientBuilder.standard().withRegion(System.getenv("region")).build());
    private final OpenMeteoSimpleApi openMeteoSimpleApi = new OpenMeteoSimpleApi();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, Object> handleRequest(Object request, Context context) {

        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
            String rawJsonForecast = openMeteoSimpleApi.getForecast();
            TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {
            };

            Object forecastMap = objectMapper.readValue(rawJsonForecast, typeRef);
            getTargetTable().putItem(
                    new Item()
                            .withPrimaryKey("id", UUID.randomUUID().toString())
                            .with("forecast", forecastMap));

            resultMap.put("body", "{\"message\": \"Rate stored in DynamoDB\"}");
        } catch (Exception ex) {
            resultMap.put("body", String.format("{\"error\": \"%s\"}", ex.getMessage()));
        }
        resultMap.put("statusCode", 200);
        return resultMap;
    }

    private Table getTargetTable() {
        return DYNAMO_DB.getTable(System.getenv("table"));
    }
}
