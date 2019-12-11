package com.example.msiproject.utils;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class Request extends AsyncTask<Object, Integer, Object> {

    private IRequestResult requestListener = null;
    private Signal signal;
    private URL url;
    private String method;
    private String params;
    private int responseCode = Constants.RESULT_CONNECTION_ERROR;
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
        Log.d("Request", "Starting request to "+url.toString());
        try {
            HttpsTrustManager.allowAllSSL();
            HttpURLConnection conn = (HttpsURLConnection)url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setRequestMethod(method);

            if(headers != null)
                for(String k : headers.keySet()) {
                    conn.setRequestProperty(k, headers.get(k));
                }

            conn.setDoInput(true);
            conn.setDoOutput(!method.equalsIgnoreCase("GET") && !method.equalsIgnoreCase("DELETE"));

            Log.d("Request", "Output stream");

            if(!method.equalsIgnoreCase("GET") && !method.equalsIgnoreCase("DELETE")) {
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(params);
                writer.write("\n\n");
                writer.flush();
                writer.close();
                os.close();
            }

            responseCode = conn.getResponseCode();
            Log.d("Request", "Response: "+responseCode);


            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuffer buff = new StringBuffer("");
            String l = "";
            while( (l = reader.readLine()) != null ) {
                if(l.length()<3) break;
                buff.append(l);
            }

            return new RequestResult(responseCode, buff.toString());
        } catch (IOException e) {
            Log.e("Request", e.getMessage());
            if(requestListener != null) requestListener.publishProblem(e);
            return new RequestResult(responseCode, "{}");
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
        void publishProblem(Exception e);
    }

    public enum Signal {
        Add,
        Edit,
        Fetch,
        Delete,
        Quantity,
        Authorize,
        Synchronize
    }

}