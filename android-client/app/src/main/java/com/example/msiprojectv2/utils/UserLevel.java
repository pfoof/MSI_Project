package com.example.msiprojectv2.utils;

import android.util.Log;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UserLevel {
    @JsonProperty("level")
    public String level;

    @JsonCreator
    private UserLevel(@JsonProperty("level") String level) {
        this.level = level;
    }

    public int getLevel() { return Integer.parseInt(level); }

    public static UserLevel getInstance(String json) {
        ObjectMapper om = new ObjectMapper();
        try {
            return om.readValue(json, UserLevel.class);
        } catch (Exception e) {
            Log.e("User_Token", "Parsing exception: "+e.getMessage());
            Log.e("User_Token", Log.getStackTraceString(e));
            return new UserLevel("0");
        }
    }
}
