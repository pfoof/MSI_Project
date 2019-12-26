package com.example.msiprojectv2.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class Tokens {

    public static boolean hasToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.USER_PREFS, Context.MODE_PRIVATE);
        return prefs.contains(Constants.TOKEN_HEADER);
    }

    public static String getToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.USER_PREFS, Context.MODE_PRIVATE);
        return prefs.getString(Constants.TOKEN_HEADER, "xxx");
    }



}
