package com.example.msiproject.utils;

import org.json.JSONException;
import org.json.JSONObject;

public class RequestResult {
    private String data = "";
    private int resultCode = -1;

    public RequestResult(int code, String data) {
        this.data = data;
        this.resultCode = code;
    }

    public int getResultCode() {return resultCode;}
    public String getData() {return data;}
    public JSONObject getJSON() throws JSONException {return new JSONObject(data);}
}
