package com.buenatech.staytune.activities;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefHelper {
    public static SharedPreferences prefs;
    private static final String SHARE_PREF_NAME = "MySharedPref";


    public void setFirstStart(Context context, String value) {
        prefs = context.getSharedPreferences(SHARE_PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor myEdit = prefs.edit();
        myEdit.putString("firststart", value);
        myEdit.apply();
    }


    public String getFirstStart(Context context) {
        prefs = context.getSharedPreferences(SHARE_PREF_NAME, MODE_PRIVATE);
        return prefs.getString("firststart", "");
    }

}

