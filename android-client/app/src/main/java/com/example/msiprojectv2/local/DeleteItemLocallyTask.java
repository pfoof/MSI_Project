package com.example.msiprojectv2.local;

import android.content.Context;

import com.example.msiprojectv2.utils.ItemModel;
import com.example.msiprojectv2.utils.Request;
import com.example.msiprojectv2.utils.RequestResult;

public class DeleteItemLocallyTask extends LocalActionTask {

    private int id = 0;

    public DeleteItemLocallyTask(Context context, Request.IRequestResult result, int id) {
        super(context, result);
        this.id = id;
    }

    @Override
    public void action() {
        ItemModel itemModel = getItemsDb().getItem(id);
        if(itemModel == null || itemModel.fromServer) {
            ItemModel im = new ItemModel();
            im.id = id;
            getItemsDb().delete(id);
            getActionDb().insertAll(new Action().setAction(ActionTaken.DELETE).fillWith(im));
        } else {
            getItemsDb().delete(id);
            getActionDb().deleteAllOfItem(id);
        }
    }

    @Override
    public void result() {
        result.publishResult(new RequestResult(200, ""), Request.Signal.Delete);
    }
}
