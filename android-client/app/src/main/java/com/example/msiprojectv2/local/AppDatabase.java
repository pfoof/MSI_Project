package com.example.msiprojectv2.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.msiprojectv2.utils.ItemModel;

@Database(entities = {Action.class, ItemModel.class}, version = 4)
abstract class AppDatabase extends RoomDatabase {
    public abstract ActionDao getActionsDao();
    public abstract ItemsDao getItemsDao();
}
