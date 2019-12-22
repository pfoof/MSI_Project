package com.example.msiproject.local;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class SyncPack {

    @JsonProperty("uuid")
    public String uuid;

    @JsonProperty("actions")
    public List<Action> actions;

    public SyncPack(@NonNull String uuid, List<Action> actions) {
        this.actions = actions;
        this.uuid = uuid;
    }

    @Override
    public String toString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return "";
        }
    }

}
