package com.example.msiproject.utils;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ItemModel {

    @JsonProperty("id")
    public int id;
    @JsonProperty("name")
    public String name;
    @JsonProperty("prod")
    public String prod;
    @JsonProperty("quantity")
    public int quantity;
    @JsonProperty("price")
    public float price;

}
