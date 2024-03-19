package com.task08;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.lambda.layer.exchange.OpenMeteoSimpleApi;
import com.syndicate.deployment.annotations.LambdaUrlConfig;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.lambda.LambdaLayer;
import com.syndicate.deployment.model.ArtifactExtension;
import com.syndicate.deployment.model.DeploymentRuntime;
import com.syndicate.deployment.model.lambda.url.AuthType;
import com.syndicate.deployment.model.lambda.url.InvokeMode;

import java.util.HashMap;
import java.util.Map;

@LambdaHandler(
        lambdaName = "api_handler",
        roleName = "task-08-lambda-01-role",
        layers = {"sdk_layer"},
        runtime=DeploymentRuntime.JAVA11
)
@LambdaUrlConfig(
        authType = AuthType.NONE,
        invokeMode = InvokeMode.BUFFERED
)
@LambdaLayer(
        layerName = "sdk_layer",
        libraries = {"lib/open-meteo-sdk-1.0.0.jar"},
        runtime = DeploymentRuntime.JAVA11,
        artifactExtension = ArtifactExtension.ZIP
)
public class Task08Lambda01 implements RequestHandler<Object, Map<String, Object>> {

    private final OpenMeteoSimpleApi openMeteoSimpleApi = new OpenMeteoSimpleApi();

    public Map<String, Object> handleRequest(Object request, Context context) {

        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
            resultMap.put("body", openMeteoSimpleApi.getForecast());
        } catch (Exception ex) {
            resultMap.put("body", String.format("{\"error\": \"%s\"}", ex.getMessage()));
        }
        resultMap.put("statusCode", 200);
        return resultMap;
    }
}
