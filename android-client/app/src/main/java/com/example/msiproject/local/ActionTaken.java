package com.example.msiproject.local;

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
            if(at.code == val)
                return at;
        return null;
    }

    @TypeConverter
    public static String getActionTakenString(ActionTaken at) {
        if(at != null)
            return at.code;
        return null;
    }
}
