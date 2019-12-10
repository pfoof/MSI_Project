package com.example.msiproject.utils;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Entity(tableName = "items")
public class ItemModel {

    @PrimaryKey
    @JsonProperty("item")
    @NonNull
    public int id = 0;
    @JsonProperty("name")
    public String name;
    @JsonProperty("prod")
    public String prod;
    @JsonProperty("quantity")
    public int quantity;
    @JsonProperty("price")
    public float price;

    public boolean fromServer = true;

    public Bundle asBundle() {
        Bundle bun = new Bundle();
        bun.putInt("item", id);
        bun.putString("name", this.name);
        bun.putString("prod", prod);
        bun.putInt("quantity", quantity);
        bun.putFloat("price", price);
        return bun;
    }

    public String toJson() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(this);
    }

    public ItemModel setFromServer(boolean fromServer) {
        this.fromServer = fromServer;
        return this;
    }

}
