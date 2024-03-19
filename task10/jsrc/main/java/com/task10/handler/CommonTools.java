package com.task10.handler;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

abstract class CommonTools {
    protected final String SIMPLE_MESSAGE_JSON_TEMPLATE = "{\"%s\": \"%s\"}";
    protected final ObjectMapper objectMapper;

    protected CommonTools(){
        this.objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }
}
