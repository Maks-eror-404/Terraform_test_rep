package com.task10.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SignInRequest {

    @JsonProperty("email")
    private String email;
    @JsonProperty("password")
    private String password;

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
