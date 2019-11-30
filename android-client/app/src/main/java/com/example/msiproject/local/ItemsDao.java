package com.example.msiproject.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.msiproject.utils.ItemModel;

import java.util.List;

@Dao
interface ItemsDao {

    @Query("SELECT * FROM items")
    List<ItemModel> getItems();

    @Query("DELETE FROM items")
    void deleteAll();

    @Insert
    void insertAll(ItemModel... items);

}
