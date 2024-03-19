package com.task04;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.syndicate.deployment.annotations.events.SqsTriggerEventSource;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.resources.DependsOn;
import com.syndicate.deployment.model.ResourceType;
import com.syndicate.deployment.model.DeploymentRuntime;

import java.text.SimpleDateFormat;
import java.util.Calendar;

@LambdaHandler(
        lambdaName = "sqs_handler",
        roleName = "sqs-handler-role",
        timeout = 10,
        runtime=DeploymentRuntime.JAVA11

)
@SqsTriggerEventSource(
        targetQueue = "async_queue",
        batchSize = 10
)
@DependsOn(
        name = "async_queue",
        resourceType = ResourceType.SQS_QUEUE
)
public class SqsHandler implements RequestHandler<SQSEvent, Void> {

    @Override
    public Void handleRequest(SQSEvent event, Context context) {
        context.getLogger().log("Invocation started: " + getTimeStamp());

        try {
            event.getRecords().forEach(sqsMessage -> context.getLogger().log(sqsMessage.getBody()));
        } catch (Exception exception) {
            context.getLogger().log("Failed to log messages from SQS. Reason: " + exception.getMessage());
        }

        context.getLogger().log("Invocation completed: " + getTimeStamp());
        return null;
    }

    private String getTimeStamp() {
        return new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(Calendar.getInstance().getTime());
    }
}