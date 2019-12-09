package com.example.msiproject.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UserLevel {
    @JsonProperty("level")
    public int level;

    private UserLevel(int level) {
        this.level = level;
    }

    public int getLevel() { return level; }

    public static UserLevel getInstance(String json) {
        ObjectMapper om = new ObjectMapper();
        try {
            return om.readValue(json, UserLevel.class);
        } catch (Exception e) {
            return new UserLevel(0);
        }
    }
}
