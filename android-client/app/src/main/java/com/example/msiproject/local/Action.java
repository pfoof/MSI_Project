package com.example.msiproject.local;

import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.msiproject.utils.ItemModel;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

@Entity(tableName = "actions")
class Action {
    @PrimaryKey(autoGenerate = true) public int id;
    @JsonProperty("name")
    public String name;
    @JsonProperty("prod")
    public String prod;
    @JsonProperty("price")
    public float price;
    @JsonProperty("quantity")
    public int quantity;
    @JsonProperty("item")
    public int item;
    @JsonProperty("timestamp")
    public long timestamp = new Date().getTime();

    @Nullable
    @JsonProperty("action")
    @TypeConverters(ActionTaken.class)
    ActionTaken actionTaken;

    public Action fillWith(ItemModel model) {
        this.name = model.name;
        this.price = model.price;
        this.prod = model.prod;
        this.quantity = model.quantity;
        this.item = model.id;
        return this;
    }

    public Action setAction(ActionTaken at) {
        actionTaken = at;
        return this;
    }

}
