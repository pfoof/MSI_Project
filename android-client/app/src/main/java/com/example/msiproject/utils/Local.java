package com.example.msiproject.utils;

import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.RoomDatabase;

import java.util.List;

public class Local {

    private static void getDatabase() {

    }

    @Database(entities = Action.class, version = 1)
    abstract class AppDatabase extends RoomDatabase {
        public abstract ActionDao getDao();
    }

    @Dao
    interface ActionDao {

        @Query("SELECT * FROM actions")
        public List<Action> getAll();

        @Insert
        void insertAll(Action... actions);

        @Delete
        void delete();
    }

    class Action {

    }
}
