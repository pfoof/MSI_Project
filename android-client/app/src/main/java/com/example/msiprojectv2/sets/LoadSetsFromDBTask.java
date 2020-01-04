package com.example.msiprojectv2.sets;

import android.content.Context;
import android.os.AsyncTask;

import com.example.msiprojectv2.local.Local;
import com.example.msiprojectv2.utils.Request;
import com.example.msiprojectv2.utils.RequestResult;
import com.fasterxml.jackson.databind.ObjectMapper;

public class LoadSetsFromDBTask extends AsyncTask<Void, Void, Void> {

    private Context context;
    private Request.IRequestResult result;

    public LoadSetsFromDBTask(Context context, Request.IRequestResult result) {
        this.context = context;
        this.result = result;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            String mappedObject = new ObjectMapper().writeValueAsString(Local.getSets(context));

            result.publishResult(new RequestResult(200, mappedObject), Request.Signal.FetchSet);
        } catch(Exception e) {
            result.publishResult(new RequestResult(500, "Error serializing to JSON"), Request.Signal.FetchSet);
        } finally {
            return null;
        }
    }
}
