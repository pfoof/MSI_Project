package com.example.msiproject.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.msiproject.utils.ItemModel;

@Database(entities = {Action.class, ItemModel.class}, version = 1)
abstract class AppDatabase extends RoomDatabase {
    public abstract ActionDao getActionsDao();
    public abstract ItemsDao getItemsDao();
}
