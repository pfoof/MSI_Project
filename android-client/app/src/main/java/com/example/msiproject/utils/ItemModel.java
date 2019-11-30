package com.example.msiproject.utils;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity(tableName = "items")
public class ItemModel {

    @PrimaryKey
    @JsonProperty("item")
    @NonNull
    public String id = "0";
    @JsonProperty("name")
    public String name;
    @JsonProperty("prod")
    public String prod;
    @JsonProperty("quantity")
    public String quantity;
    @JsonProperty("price")
    public String price;

    public Bundle asBundle() {
        Bundle bun = new Bundle();
        bun.putString("item", id);
        bun.putString("name", this.name);
        bun.putString("prod", prod);
        bun.putString("quantity", quantity);
        bun.putString("price", price);
        return bun;
    }

}
