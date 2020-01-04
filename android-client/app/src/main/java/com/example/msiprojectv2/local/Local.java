package com.example.msiprojectv2.local;

import android.content.Context;

import androidx.room.*;

import com.example.msiprojectv2.utils.ItemModel;
import com.example.msiprojectv2.utils.SetItem;
import com.example.msiprojectv2.utils.SetModel;

import java.lang.reflect.Array;
import java.util.ArrayList;
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

    public static SetsDao getSetsDatabase(Context context) {
        return getDatabase(context).getSetsDao();
    }

    public static SetItemDao getSetItemDatabase(Context context) {
        return getDatabase(context).getSetItemDao();
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

    public static List<String> getItemCommonNames(Context context, int[] ids, int[] quantities) {
        ItemsDao dao = getItemsDatabase(context);
        ArrayList<String> names = new ArrayList<>();

        for(int i = 0; i < ids.length; ++i) {
            int id = ids[i];
            String[] n  = new String[]{dao.getNameAndProd(id).name, dao.getNameAndProd(id).prod};
            if(n != null && n.length > 1)
                names.add(""+ quantities[i]+ "x " + n[1]+" "+n[0]);
        }

        return names;
    }

    public static float getCompletePrice(Context context, SetModel model) {
        List<SetItem> items = getSetItems(context, model);
        ItemsDao dao = getItemsDatabase(context);
        float sum = 0f;

        for(SetItem i : items) {
            float price = dao.getPrice(i.item);
            sum += price * i.quantity;
        }

        return sum;
    }

    public static List<SetModel> getSets(Context context) {
        return getSetsDatabase(context).getItems();
    }

    public static List<SetItem> getSetItems(Context context, SetModel model) {
        return getSetItemDatabase(context).getItemsOf(model.id);
    }

    public static void deleteAllSets(Context context) {
        getSetsDatabase(context).deleteAll();
        getSetsDatabase(context).deleteAllItems();
        getSetItemDatabase(context).deleteAll();
    }

    public static void insertSetModels(Context context, List<SetModel> models) {
        SetsDao dao = getSetsDatabase(context);
        for(SetModel model :models)
            dao.insertSet(model);
    }

    public static void insertSetItems(Context context, List<SetItem> items) {
        SetItemDao dao = getSetItemDatabase(context);
        for(SetItem item: items)
            dao.insert(item);
    }

}
