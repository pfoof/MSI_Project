package com.example.msiprojectv2.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class UserData {

    public final String token;
    public final int level;

    private UserData(String token, int level) {
        this.token = token;
        this.level = level;
    }

    public boolean canQuantity() {
        return level >= 1;
    }

    public boolean canAddEdit() {
        return level >= 2;
    }

    public boolean canDelete() {
        return level >= 4;
    }

    public static UserData fetchData(boolean force, Context context) {
        int tLevel = 0;
        String tToken = "";

        SharedPreferences prefs = context.getSharedPreferences("user_data",Context.MODE_PRIVATE);
        if(prefs.contains("level"))
            tLevel = prefs.getInt("level", 0);

        if(prefs.contains("token"))
            tToken = prefs.getString("token", "");

        if(force && Remote.ping()) {
            SharedPreferences.Editor edit = prefs.edit();

            edit.commit();

        }

        return new UserData(tToken, tLevel);
    }

}
