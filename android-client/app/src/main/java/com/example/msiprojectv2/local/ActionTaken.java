package com.example.msiprojectv2.local;

import androidx.annotation.NonNull;
import androidx.room.TypeConverter;

enum ActionTaken {
    ADD("ADD"),
    DELETE("DELETE"),
    EDIT("EDIT"),
    QUANTITY("QUANTITY");

    private final String code;

    ActionTaken(String q) {
        code = q;
    }

    public String getCode() {
        return code;
    }

    @TypeConverter
    public static ActionTaken getActionTaken(String val) {
        for(ActionTaken at : values())
            if(at.code.equals(val))
                return at;
        return null;
    }

    @TypeConverter
    public static String getActionTakenString(ActionTaken at) {
        if(at != null)
            return at.code;
        return null;
    }

    @NonNull
    @Override
    public String toString() {
        return code;
    }
}
