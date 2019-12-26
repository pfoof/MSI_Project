package com.example.msiprojectv2.utils;

import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class Remote {

    private static List<Request.IRequestResult> requestResults = new ArrayList<Request.IRequestResult>();

    public static boolean ping() {
        try {
            RequestResult res = new Request(Constants.SERVER_DEST(null) + "/", "GET", "", null, null, Request.Signal.Fetch).doRequestSync();
            return res != null && res.getResultCode() < 400 && res.getResultCode() >= 200;
        } catch(Exception e) {
            Log.e("ping()", e.getMessage());
            return false;
        }
    }

    public static void registerReceiver(Request.IRequestResult requestResult) {
        requestResults.add(requestResult);
    }

    private static Request.IRequestResult triggerReceivers = new Request.IRequestResult() {
        @Override
        public void publishResult(RequestResult data, Request.Signal sig) {
            for(Request.IRequestResult r : requestResults)
                if( r != null )
                    r.publishResult(data, sig);
        }

        @Override
        public void publishProblem(Exception e) {
            for(Request.IRequestResult r : requestResults)
                if( r != null )
                    r.publishProblem(e);
        }
    };



    public static void getItems() {
        try {
            new Request(Constants.SERVER_DEST(null) +"/", "GET", "", null, triggerReceivers, Request.Signal.Fetch).execute();
        } catch (MalformedURLException e) {
            Log.e("getItems()", e.getMessage());
        }
    }



    public static String getUserLevel(String token) {
        try {
            URL url = new URL(Constants.SERVER_DEST(null)+"/authorize");
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            if(conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                conn.getInputStream();
            }
        } catch (MalformedURLException e) {
            return "";
        } catch (IOException e) {
            return "";
        }
        return "";
    }
}
