package com.wwq.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by 魏文强 on 2016/6/9.
 */
public class SharedPreferencesUtils {

    public static final String SP_NAME = "config";

    public static void saveBoolean(Context context, String key, boolean value) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, 0);
        sp.edit().putBoolean(key, value).commit();
    }

    public static boolean getBoolean(Context context, String key, boolean defValue) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, 0);
        return sp.getBoolean(key, defValue);
    }
}
