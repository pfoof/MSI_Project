package com.example.msiproject.local;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;

import com.example.msiproject.utils.ItemModel;

import java.util.List;

public class SaveItemsLocallyTask extends AsyncTask<Void, Void, Void> {

    private List<ItemModel> itemModels;
    private ItemsDao dao;

    public SaveItemsLocallyTask(@NonNull List<ItemModel> items, Context context) {
        this.itemModels = items;
        dao = Local.getItemsDatabase(context);
    }

    @Override
    protected Void doInBackground(Void... params) {
        dao.deleteAll();
        for(ItemModel i : itemModels)
            dao.insertAll(i);
        return null;
    }
}
