package com.example.msiprojectv2.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.msiprojectv2.utils.ItemModel;
import com.example.msiprojectv2.utils.SetItem;
import com.example.msiprojectv2.utils.SetModel;

@Database(entities = {Action.class, ItemModel.class, SetItem.class, SetModel.class}, version = 4)
abstract class AppDatabase extends RoomDatabase {
    public abstract ActionDao getActionsDao();
    public abstract ItemsDao getItemsDao();
    public abstract SetsDao getSetsDao();
    public abstract SetItemDao getSetItemDao();
}
