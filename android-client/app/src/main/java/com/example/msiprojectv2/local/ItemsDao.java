package com.example.msiprojectv2.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.msiprojectv2.utils.ItemModel;

import java.util.List;

@Dao
interface ItemsDao {

    @Query("SELECT * FROM items")
    List<ItemModel> getItems();

    @Query("DELETE FROM items")
    void deleteAll();

    @Insert
    void insertAll(ItemModel... items);

    @Insert
    long insertOne(ItemModel item);

    @Query("SELECT * FROM items WHERE id = :id")
    ItemModel getItem(int id);

    @Query("UPDATE items SET quantity = (quantity + :delta) WHERE id = :id")
    void quantity(int id, int delta);

    @Query("UPDATE items SET name = :name, prod = :prod, price = :price WHERE id = :id")
    void edit(int id, String name, String prod, float price);

    @Query("DELETE FROM items WHERE id = :id")
    void delete(int id);

    @Query("SELECT * FROM items WHERE id = :id")
    ItemModel getNameAndProd(int id);

    @Query("SELECT price FROM items WHERE id = :id")
    float getPrice(int id);

}
