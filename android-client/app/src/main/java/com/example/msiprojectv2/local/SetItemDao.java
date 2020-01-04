package com.example.msiprojectv2.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.msiprojectv2.utils.SetItem;

import java.util.List;

@Dao
public interface SetItemDao {

    @Query("DELETE FROM setitems")
    void deleteAll();

    @Query("DELETE FROM setitems WHERE item = :item")
    void deleteAll(int item);

    @Query("SELECT * FROM setitems WHERE `set` = :set")
    List<SetItem> getItemsOf(int set);

    @Insert
    void insert(SetItem item);

}
