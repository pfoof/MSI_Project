package com.example.msiprojectv2.utils;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity(tableName = "setitems")
public class SetItem {

    @PrimaryKey
    @JsonProperty("id")
    public int id;

    @JsonProperty("set")
    public int set;

    @JsonProperty("item")
    public int item;

    @JsonProperty("quantity")
    public int quantity;

}
