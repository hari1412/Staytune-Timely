package com.buenatech.staytune.utils;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefHelper {

    public static SharedPreferences prefs;
    private static final String SHARE_PREF_NAME = "MySharedPref";
    private Context context;
    private SharedPreferences.Editor editor;

    public PrefHelper(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

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

    public Integer getInAppReviewToken() {
        return prefs.getInt("in_app_review_token", 0);
    }


    public void updateInAppReviewToken(int value) {
        editor.putInt("in_app_review_token", value);
        editor.apply();
    }

}


