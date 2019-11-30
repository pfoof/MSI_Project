package com.example.msiproject.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
interface ActionDao {

    @Query("SELECT * FROM actions")
    List<Action> getAll();

    @Insert
    void insertAll(Action... actions);

    @Query("DELETE FROM actions")
    void deleteAll();
}
