package com.example.msiproject.utils;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ItemModel {

    @JsonProperty("item")
    public String id;
    @JsonProperty("name")
    public String name;
    @JsonProperty("prod")
    public String prod;
    @JsonProperty("quantity")
    public String quantity;
    @JsonProperty("price")
    public String price;

}
