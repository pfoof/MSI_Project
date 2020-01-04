package com.example.msiprojectv2.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.msiprojectv2.utils.SetItem;
import com.example.msiprojectv2.utils.SetModel;

import java.util.List;

@Dao
public interface SetsDao {

    @Query("SELECT * FROM setmodels")
    List<SetModel> getItems();

    @Query("SELECT * FROM setmodels WHERE id = :id")
    SetModel getItem(int id);

    @Query("DELETE FROM setmodels")
    void deleteAll();

    @Query("DELETE FROM setitems")
    void deleteAllItems();

    @Insert
    void insertSet(SetModel model);
}
