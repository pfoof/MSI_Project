package com.example.msiprojectv2.local;

import android.content.Context;

import com.example.msiprojectv2.utils.ItemModel;
import com.example.msiprojectv2.utils.Request;
import com.example.msiprojectv2.utils.RequestResult;

public class ChangeQuantityLocallyTask extends LocalActionTask {

    private int id;
    private int delta;

    public ChangeQuantityLocallyTask(Context context, Request.IRequestResult result, int id, int delta) {
        super(context, result);
        this.id = id;
        this.delta = delta;
    }

    @Override
    public void action() {
        ItemModel itemModel = getItemsDb().getItem(id);
        getItemsDb().quantity(id, delta);
        if(itemModel == null || itemModel.fromServer) {
            Action action = new Action().setAction(ActionTaken.QUANTITY);
            action.item = id;
            action.quantity = delta;
            getActionDb().insertAll(action);
        } else {
            getActionDb().quantity(id, delta);
        }
    }

    @Override
    public void result() {
        result.publishResult(new RequestResult(200, ""), Request.Signal.Quantity);
    }
}
