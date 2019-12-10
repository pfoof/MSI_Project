package com.example.msiproject.utils;

import android.widget.EditText;

public class Constants {

    private static String lastDest = "https://172.20.10.2:8000";
    public static final String SERVER_DEST(EditText editText) { if(editText != null) {lastDest = editText.getText().toString();} return lastDest; }
    public static final String TOKEN_HEADER = "X-Auth-Token";
    public static final String TEST_TOKEN = "D2096F0CAAF5E7C425CBCBF967DDB2619A29C0530662801D95B063B3B6EE2759EA3A46D4F3274BEFAB36B5AD417A129C1E0C3DD7FCBF56702EB3B39EA5102FE8";
    public static final String USER_PREFS = "userPrefs";
    public static final String USER_LEVEL = "userLevel";

    public static final int RESULT_CONNECTION_ERROR = -101;

    public static final int ACTIVITY_REQUEST_ADDEDIT = 0x1001;
    public static final int ACTIVITY_RESULT_FAIL = 0x400;
    public static final int ACTIVITY_RESULT_OK = 0x200;
    public static final int ACTIVITY_RESULT_CANCEL = 0x300;


}
