package com.ab.hicarerun.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtility {

    public static final String IS_USER_LOGIN = "IS_USER_LOGIN";
    public static final String PREF_STATUS = "PREF_STATUS";
    public static final String PREF_MODE = "PREF_MODE";
    public static final String PREF_AMOUNT_TO_COLLECTED = "PREF_AMOUNT_TO_COLLECTED";
    public static final String PREF_AMOUNT_COLLECTED = "PREF_AMOUNT_COLLECTED";
    public static final String IS_PROPERTY_CHECKED = "IS_PROPERTY_CHECKED";
    public static final String PREF_ACTUAL_SIZE = "PREF_ACTUAL_SIZE";
    public static final String PREF_FEEDBACK_CODE = "PREF_FEEDBACK_CODE";
    public static final String PREF_SIGNATORY = "PREF_SIGNATORY";
    public static final String PREF_SIGNATURE = "SIGNATURE";
    public static final String PREF_TIME = "1";
    public static final String PREF_USERNAME = "PREF_USERNAME";
    public static final String PREF_USERID = "PREF_USERID";
    public static final String PREF_ATTACHMENT = "PREF_ATTACHMENT";
    public static final String PREF_REFRESH = "PREF_REFRESH";

    public static boolean getPrefBoolean(Context context, String key) {
        SharedPreferences prefs =
                context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        return prefs.getBoolean(key, false);
    }

    public static void savePrefBoolean(Context context, String key, boolean value) {
        SharedPreferences prefs =
                context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        prefs.edit().putBoolean(key, value).apply();
    }

    public static String getPrefString(Context context, String key) {
        SharedPreferences prefs =
                context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        return prefs.getString(key, "");
    }
    public static void savePrefString(Context context, String key, String value) {
        SharedPreferences prefs =
                context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        prefs.edit().putString(key, value).apply();
    }

}
