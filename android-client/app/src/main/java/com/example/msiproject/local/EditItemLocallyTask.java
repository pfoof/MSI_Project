package com.example.msiproject.local;

import android.content.Context;

import com.example.msiproject.utils.ItemModel;
import com.example.msiproject.utils.Request;
import com.example.msiproject.utils.RequestResult;

public class EditItemLocallyTask extends LocalActionTask {

    private int id = 0;
    private ItemModel model = null;

    public EditItemLocallyTask(Context context, Request.IRequestResult result, int id, ItemModel model) {
        super(context, result);
        this.id = id;
        this.model = model;
    }

    private void addItem() {
        getItemsDb().insertAll(model);
        Action action = new Action().fillWith(model).setAction(ActionTaken.ADD);
        getActionDb().insertAll(action);
    }

    private void updateItem() {
        getItemsDb().edit(id, model.name, model.prod, model.price);
        getActionDb().insertAll(new Action().fillWith(model).setAction(ActionTaken.EDIT));
    }

    @Override
    public void action() {
        if(id <= 0) addItem();
        else updateItem();
    }

    @Override
    public void result() {
        if(id <= 0)
            result.publishResult(new RequestResult(200, ""), Request.Signal.Add);
        else
            result.publishResult(new RequestResult(200, ""), Request.Signal.Edit);
    }
}
