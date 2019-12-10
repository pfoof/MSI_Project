package com.example.msiproject.local;

import android.content.Context;
import android.os.AsyncTask;

import com.example.msiproject.utils.Request;
import com.example.msiproject.utils.RequestResult;
import com.fasterxml.jackson.databind.ObjectMapper;

public class LoadItemsFromDBTask extends AsyncTask<Void, Void, Void> {

    private Context context;
    private Request.IRequestResult result;

    public LoadItemsFromDBTask(Context context, Request.IRequestResult result) {
        this.context = context;
        this.result = result;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            String mappedObject = new ObjectMapper().writeValueAsString(Local.getItems(context));

            result.publishResult(new RequestResult(200, mappedObject), Request.Signal.Fetch);
        } catch(Exception e) {
            result.publishResult(new RequestResult(500, "Error serializing to JSON"), Request.Signal.Fetch);
        } finally {
            return null;
        }
    }
}
