package com.example.msiproject.local;

import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.msiproject.utils.ItemModel;

@Entity(tableName = "actions")
class Action {
    @PrimaryKey(autoGenerate = true) public int id;
    public String name;
    public String prod;
    public String price;
    public String quantity;
    public String item;

    @Nullable
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
