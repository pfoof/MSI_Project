package com.example.msiprojectv2.local;

import android.content.Context;

import androidx.room.*;

import com.example.msiprojectv2.utils.ItemModel;

import java.util.List;

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

    public static List<ItemModel> getItems(Context context) {
        List<ItemModel> items = getItemsDatabase(context).getItems();
        return items;
    }

    public static List<Action> getActions(Context context) {
        return getActionsDatabase(context).getAll();
    }

    public static void deleteAllActions(Context context) {
        getActionsDatabase(context).deleteAll();
    }

}
