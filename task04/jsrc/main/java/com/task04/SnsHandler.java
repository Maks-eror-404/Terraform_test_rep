package com.task04;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.syndicate.deployment.annotations.events.SnsEventSource;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.resources.DependsOn;
import com.syndicate.deployment.model.ResourceType;
import com.syndicate.deployment.model.DeploymentRuntime;

import java.text.SimpleDateFormat;
import java.util.Calendar;

@LambdaHandler(
        lambdaName = "sns_handler",
        roleName = "sns-handler-role",
        runtime=DeploymentRuntime.JAVA11
)
@SnsEventSource(
        targetTopic = "lambda_topic"
)
@DependsOn(
        name = "lambda_topic",
        resourceType = ResourceType.SNS_TOPIC
)
public class SnsHandler implements RequestHandler<SNSEvent, Object> {

    @Override
    public Object handleRequest(SNSEvent request, Context context) {
        context.getLogger().log("Invocation started: " + getTimeStamp());

        try {
            request.getRecords().forEach(snsMessage -> context.getLogger().log(snsMessage.getSNS().getMessage()));
        } catch (Exception exception) {
            context.getLogger().log("Failed to log messages from SNS. Reason: " + exception.getMessage());
        }

        context.getLogger().log("Invocation completed: " + getTimeStamp());
        return null;
    }

    private String getTimeStamp() {
        return new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(Calendar.getInstance().getTime());
    }
}
