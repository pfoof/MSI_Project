package com.example.msiprojectv2.utils;

import android.content.Context;

import androidx.core.content.res.TypedArrayUtils;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.example.msiprojectv2.local.Local;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity(tableName = "setmodels")
@JsonIgnoreProperties(ignoreUnknown = true)
public class SetModel {

    @PrimaryKey
    @JsonProperty("id")
    public int id;

    @JsonProperty("name")
    public String name;

    public float getPrice(Context context) {
        return Local.getCompletePrice(context, this);
    }

    public List<String> getItemCommonNames(Context context) {
        List<SetItem> items = Local.getSetItems(context, this);
        int[] ids = new int[items.size()];
        int[] quantities = new int[items.size()];
        for(int i = 0; i < items.size(); ++i) {
            ids[i] = items.get(i).item;
            quantities[i] = items.get(i).quantity;
        }

        return Local.getItemCommonNames(context, ids, quantities);
    }
}