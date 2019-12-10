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

    @Query("UPDATE items SET quantity = CAST( (CAST(quantity AS INTEGER) + :delta) AS VARCHAR) WHERE id = :id")
    void quantity(int id, int delta);

    @Query("UPDATE items SET name = :name, prod = :prod, price = :price WHERE id = :id")
    void edit(int id, String name, String prod, float price);

    @Query("DELETE FROM items WHERE id = :id")
    void delete(int id);

}
