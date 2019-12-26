package com.example.msiprojectv2.utils;

import android.os.Bundle;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Entity(tableName = "items")
public class ItemModel {

    @PrimaryKey
    @JsonProperty("item")
    public Integer id = null;
    @JsonProperty("name")
    public String name;
    @JsonProperty("prod")
    public String prod;
    @JsonProperty("quantity")
    public int quantity;
    @JsonProperty("price")
    public float price;
    @JsonProperty
    public String color = "";

    public boolean fromServer = true;

    public Bundle asBundle() {
        Bundle bun = new Bundle();
        bun.putInt("item", id==null?0:id);
        bun.putString("name", this.name);
        bun.putString("prod", prod);
        bun.putInt("quantity", quantity);
        bun.putFloat("price", price);
        bun.putString("color", color);
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
