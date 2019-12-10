package com.example.msiproject.local;

import android.content.Context;
import android.os.AsyncTask;

import com.example.msiproject.utils.Request;
import com.example.msiproject.utils.RequestResult;

public abstract class LocalActionTask extends AsyncTask<Void, Void, Void> {

    private Context context;
    protected Request.IRequestResult result;

    protected ActionDao getActionDb() {
        return Local.getActionsDatabase(context);
    }

    protected ItemsDao getItemsDb() {
        return Local.getItemsDatabase(context);
    }

    public LocalActionTask(Context context, Request.IRequestResult result) {
        this.context = context;
        this.result = result;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        action();
        result();
        return null;
    }

    public abstract void action();
    public abstract void result();

}
