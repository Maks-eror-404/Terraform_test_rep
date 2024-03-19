package com.task07;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class FileContent {

    @JsonProperty("ids")
    private final List<String> ids;

    public FileContent(List<String> ids) {
        this.ids = ids;
    }

    public List<String> getIds() {
        return ids;
    }

    @Override
    public String toString() {
        return "FileContent{" +
                "ids=" + ids +
                '}';
    }
}
