package com.example.msiproject.local;

import android.content.Context;

import androidx.room.*;

public class Local {

    private static AppDatabase getDatabase(Context context) {
        return Room.
                databaseBuilder(context, AppDatabase.class, "msidb")
                .build();
    }

    public static ActionDao getActionsDatabase(Context context) {
        return getDatabase(context).getActionsDao();
    }

    public static ItemsDao getItemsDatabase(Context context) {
        return getDatabase(context).getItemsDao();
    }

}
