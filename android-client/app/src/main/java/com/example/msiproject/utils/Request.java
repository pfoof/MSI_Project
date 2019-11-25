package com.example.msiproject.utils;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class Request extends AsyncTask<Object, Integer, Object> {

    private IRequestResult requestListener = null;
    private Signal signal;
    private URL url;
    private String method;
    private String params;
    private int responseCode = -1;
    private Map<String, String> headers = null;

    public Request(String url, String method, String params, Map<String, String> headers, IRequestResult result, Signal signal) throws MalformedURLException {
        this.requestListener = result;
        this.signal = signal;
        this.url = new URL(url);
        this.method = method;
        this.params = params;
        this.headers = headers;
    }

    public RequestResult doRequestSync() {
        try {
            HttpURLConnection conn = (HttpsURLConnection)url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setRequestMethod(method);

            if(headers != null)
                for(String k : headers.keySet()) {
                    conn.setRequestProperty(k, headers.get(k));
                }

            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(params);
            writer.flush();
            writer.close();
            os.close();

            responseCode = conn.getResponseCode();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuffer buff = new StringBuffer("");
            String l = "";
            while( (l = reader.readLine()) != null ) {
                if(l.length()<3) break;
                buff.append(l);
            }

            return new RequestResult(responseCode, buff.toString());
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    protected Object doInBackground(Object[] objects) {

        RequestResult res = doRequestSync();

        if (requestListener != null)
            requestListener.publishResult(res, signal);

        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    public interface IRequestResult {
        void publishResult(RequestResult data, Signal sig);
    }

    public enum Signal {
        Add,
        Edit,
        Fetch,
        Delete
    }

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

}