package com.task10.handler;

import com.task10.entity.SignUpRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminInitiateAuthRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminInitiateAuthResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminRespondToAuthChallengeRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminRespondToAuthChallengeResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthFlowType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ChallengeNameType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.DeliveryMediumType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListUserPoolClientsRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListUserPoolsRequest;

import java.util.HashMap;
import java.util.Map;

abstract class CognitoSupport extends CommonTools {
    protected final CognitoIdentityProviderClient cognitoClient;
    private final String userPoolId;
    private final String clientId;
    private final String userPoolName;

    protected CognitoSupport(CognitoIdentityProviderClient cognitoClient, String userPoolName) {
        this.cognitoClient = cognitoClient;
        this.userPoolName = userPoolName;
        this.userPoolId = getUserPoolId();
        this.clientId = getClientId();
    }

    private String getUserPoolId() throws RuntimeException {
        return cognitoClient.listUserPools(ListUserPoolsRequest.builder()
                        .maxResults(10)
                        .build())
                .userPools().stream()
                .filter(pool -> pool.name().contains(userPoolName))
                .findAny()
                .orElseThrow(() -> new RuntimeException(String.format("user pool %s not found", userPoolName)))
                .id();
    }

    // we use client resolving because there is no possibility to transfer the client ID to the lambda by Syndicate at this moment yet
    private String getClientId() throws RuntimeException {
        return cognitoClient.listUserPoolClients(ListUserPoolClientsRequest.builder()
                        .userPoolId(userPoolId)
                        .maxResults(1)
                        .build())
                .userPoolClients().stream()
                .filter(client -> client.clientName().contains("client-app"))
                .findAny()
                .orElseThrow(() -> new RuntimeException("client 'client-app' not found"))
                .clientId();
    }

    protected AdminInitiateAuthResponse signIn(String email, String password) {
        Map<String, String> authParams = new HashMap<>();
        authParams.put("USERNAME", email);
        authParams.put("PASSWORD", password);
        return cognitoClient.adminInitiateAuth(AdminInitiateAuthRequest.builder()
                .authFlow(AuthFlowType.ADMIN_NO_SRP_AUTH)
                .authParameters(authParams)
                .userPoolId(userPoolId)
                .clientId(clientId)
                .build());
    }

    protected AdminCreateUserResponse signUp(SignUpRequest signUpRequest) {
        return cognitoClient.adminCreateUser(AdminCreateUserRequest.builder()
                .userPoolId(userPoolId)
                .username(signUpRequest.getEmail())
                .temporaryPassword(signUpRequest.getPassword())
                .userAttributes(
                        AttributeType.builder()
                                .name("given_name")
                                .value(signUpRequest.getFirstName())
                                .build(),
                        AttributeType.builder()
                                .name("family_name")
                                .value(signUpRequest.getLastName())
                                .build(),
                        AttributeType.builder()
                                .name("email")
                                .value(signUpRequest.getEmail())
                                .build(),
                        AttributeType.builder()
                                .name("email_verified")
                                .value("true")
                                .build())
                .desiredDeliveryMediums(DeliveryMediumType.EMAIL)
                .messageAction("SUPPRESS")
                .forceAliasCreation(Boolean.FALSE)
                .build());
    }

    protected AdminRespondToAuthChallengeResponse confirmSignUp(SignUpRequest signUpRequest) {
        AdminInitiateAuthResponse adminInitiateAuthResponse = signIn(signUpRequest.getEmail(), signUpRequest.getPassword());

        if (!ChallengeNameType.NEW_PASSWORD_REQUIRED.name().equals(adminInitiateAuthResponse.challengeNameAsString())) {
            throw new RuntimeException("unexpected challenge: " + adminInitiateAuthResponse.challengeNameAsString());
        }

        Map<String, String> challengeResponses = new HashMap<>();
        challengeResponses.put("USERNAME", signUpRequest.getEmail());
        challengeResponses.put("PASSWORD", signUpRequest.getPassword());
        challengeResponses.put("NEW_PASSWORD", signUpRequest.getPassword());

        return cognitoClient.adminRespondToAuthChallenge(AdminRespondToAuthChallengeRequest.builder()
                .challengeName(ChallengeNameType.NEW_PASSWORD_REQUIRED)
                .challengeResponses(challengeResponses)
                .userPoolId(userPoolId)
                .clientId(clientId)
                .session(adminInitiateAuthResponse.session())
                .build());
    }
}
