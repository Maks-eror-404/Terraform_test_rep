package com.task07;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.syndicate.deployment.annotations.environment.EnvironmentVariable;
import com.syndicate.deployment.annotations.environment.EnvironmentVariables;
import com.syndicate.deployment.annotations.events.RuleEventSource;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.resources.DependsOn;
import com.syndicate.deployment.model.ResourceType;
import com.syndicate.deployment.model.DeploymentRuntime;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@LambdaHandler(
        lambdaName = "uuid_generator",
        roleName = "task-07-lambda-01-role",
        runtime=DeploymentRuntime.JAVA11
)
@RuleEventSource(
        targetRule = "uuid_trigger"
)
@DependsOn(
        resourceType = ResourceType.CLOUDWATCH_RULE,
        name = "uuid_trigger")
@EnvironmentVariables(value = {
        @EnvironmentVariable(key = "region", value = "${region}"),
        @EnvironmentVariable(key = "storage", value = "${target_bucket}")
})
public class Task07Lambda01 implements RequestHandler<ScheduledEvent, Void> {

    private AmazonS3 s3Client;
    private ObjectMapper objectMapper;

    public Task07Lambda01() {
        this.s3Client = AmazonS3Client.builder().withRegion(System.getenv("region")).build();
        this.objectMapper = new ObjectMapper();
    }

    public Void handleRequest(ScheduledEvent event, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("event = " + event);

        String bucketName = System.getenv("storage");
        logger.log("bucketName = " + bucketName);

        List<String> uuids = IntStream.range(0, 10)
                .mapToObj(iteration -> UUID.randomUUID().toString())
                .collect(Collectors.toList());
        logger.log("uuids = " + uuids);

        FileContent fileContent = new FileContent(uuids);
        logger.log("fileContent = " + fileContent);

        String fileName = ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);
        logger.log("fileName = " + fileName);

        PutObjectResult putObjectResult = s3Client.putObject(bucketName, fileName, convertObjectToJson(fileContent));
        logger.log("putObjectResult = " + putObjectResult);
        return null;
    }

    private String convertObjectToJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Object cannot be converted to JSON: " + object);
        }
    }
}
