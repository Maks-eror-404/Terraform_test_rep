package com.task06;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemUtils;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.lambda.runtime.events.transformers.v2.DynamodbEventTransformer;
import com.syndicate.deployment.annotations.environment.EnvironmentVariable;
import com.syndicate.deployment.annotations.environment.EnvironmentVariables;
import com.syndicate.deployment.annotations.events.DynamoDbTriggerEventSource;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.resources.DependsOn;
import com.syndicate.deployment.model.ResourceType;
import software.amazon.awssdk.services.dynamodb.model.Record;
import com.syndicate.deployment.model.DeploymentRuntime;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@LambdaHandler(
        lambdaName = "audit_producer",
        roleName = "task-06-lambda-01-role",
        runtime=DeploymentRuntime.JAVA11
)
@EnvironmentVariables(value = {
        @EnvironmentVariable(key = "region", value = "${region}"),
        @EnvironmentVariable(key = "table", value = "${target_table}")})
@DynamoDbTriggerEventSource(targetTable = "Configuration", batchSize = 1)
@DependsOn(name = "Configuration", resourceType = ResourceType.DYNAMODB_TABLE)
public class Task06Lambda01 implements RequestHandler<DynamodbEvent, Void> {
    private final DynamoDB DYNAMO_DB = new DynamoDB(
            AmazonDynamoDBAsyncClientBuilder.standard().withRegion(System.getenv("region")).build());

    public Void handleRequest(DynamodbEvent dynamodbEvent, Context context) {
        System.out.println("dynamodbEvent = " + dynamodbEvent);
        List<Record> convertedRecords = DynamodbEventTransformer.toRecordsV2(dynamodbEvent);
        System.out.println("convertedRecords = " + convertedRecords);
        convertedRecords.forEach(this::processRecord);
        return null;
    }

    private void processRecord(Record record) {
        try {
            switch (record.eventName()) {
                case INSERT:
                    getTable().putItem(
                            new Item()
                                    .withPrimaryKey("id", generateUUID())
                                    .with("itemKey", getKey(record))
                                    .with("modificationTime", getDateTimeNowAsString())
                                    .with("newValue", getKeyValue(record)));
                    break;
                case MODIFY:
                    getTable().putItem(
                            new Item()
                                    .withPrimaryKey("id", generateUUID())
                                    .with("itemKey", getKey(record))
                                    .with("modificationTime", getDateTimeNowAsString())
                                    .with("updatedAttribute", "value")
                                    .with("oldValue", getValue(record, false))
                                    .with("newValue", getValue(record, true)));
                    break;
                default:
                    // do nothing
            }
        } catch (Exception e) {
            System.out.println("exception = " + e.getMessage() + System.lineSeparator() + Arrays.toString(e.getStackTrace()));
        }
    }

    private Map<String, Object> getKeyValue(Record record) throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("key", getKey(record));
        map.put("value", getValue(record, true));
        return map;
    }

    private String getKey(Record record) throws Exception {
        return record.dynamodb().newImage().get("key").getValueForField("S", String.class).orElseThrow(() -> new Exception("Required filed 'key' is absent or not a STRING"));
    }

    private Object getValue(Record record, boolean newValue) throws Exception {
        return newValue
                ? Integer.parseInt(record.dynamodb().newImage().get("value").getValueForField("N", String.class).orElseThrow(() -> new Exception("value not found")))
                : Integer.parseInt(record.dynamodb().oldImage().get("value").getValueForField("N", String.class).orElseThrow(() -> new Exception("value not found")));
    }

    private String generateUUID() {
        return UUID.randomUUID().toString();
    }

    private String getDateTimeNowAsString() {
        return ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);
    }

    private Table getTable() {
        return DYNAMO_DB.getTable(System.getenv("table"));
    }

    private static <T> Map<String, T> toSimpleMapValue(
            Map<String, AttributeValue> values) {
        return ItemUtils.toSimpleMapValue(values);
    }

}
