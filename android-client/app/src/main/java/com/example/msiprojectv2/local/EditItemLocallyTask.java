package com.example.msiprojectv2.local;

import android.content.Context;

import com.example.msiprojectv2.utils.ItemModel;
import com.example.msiprojectv2.utils.Request;
import com.example.msiprojectv2.utils.RequestResult;

public class EditItemLocallyTask extends LocalActionTask {

    private int id = 0;
    private ItemModel model = null;

    public EditItemLocallyTask(Context context, Request.IRequestResult result, int id, ItemModel model) {
        super(context, result);
        this.id = id;
        this.model = model;
    }

    private void addItem() {
        long lastid = getItemsDb().insertOne(model.setFromServer(false));
        model.id = (int)lastid;
        Action action = new Action().fillWith(model).setAction(ActionTaken.ADD);
        getActionDb().insertAll(action);
    }

    private void updateItem() {
        getItemsDb().edit(id, model.name, model.prod, model.price);
        getActionDb().insertAll(new Action().fillWith(model).setAction(ActionTaken.EDIT));
    }

    private void updateLocal() {
        getItemsDb().edit(id, model.name, model.prod, model.price);
        getActionDb().edit(id, model.name, model.prod, model.price);
    }

    @Override
    public void action() {
        if(id <= 0) addItem();
        else {
            ItemModel im = getItemsDb().getItem(id);
            if(im == null || im.fromServer)
                updateItem();
            else
                updateLocal();
        }
    }

    @Override
    public void result() {
        if(id <= 0)
            result.publishResult(new RequestResult(200, ""), Request.Signal.Add);
        else
            result.publishResult(new RequestResult(200, ""), Request.Signal.Edit);
    }
}
