package com.example.msiproject.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
interface ActionDao {

    @Query("SELECT * FROM actions ORDER BY timestamp ASC")
    List<Action> getAll();

    @Insert
    void insertAll(Action... actions);

    @Query("UPDATE actions SET name = :name, prod = :prod, price = :price WHERE item = :id")
    void edit(int id, String name, String prod, float price);

    @Query("UPDATE actions SET quantity = (quantity + :delta) WHERE item = :id")
    void quantity(int id, int delta);

    @Query("DELETE FROM actions")
    void deleteAll();

    @Query("DELETE FROM actions WHERE item = :id")
    void deleteAllOfItem(int id);
}
