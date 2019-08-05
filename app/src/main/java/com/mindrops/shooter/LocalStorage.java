package com.mindrops.shooter;

import android.content.Context;
import android.content.SharedPreferences;

public class LocalStorage {
    private Context context;
    public static final String TAG = LocalStorage.class.getSimpleName();
    public static final String localstorage = "localstorage";
    public LocalStorage(Context context) {
        this.context = context;
    }

    public void setTime(String time) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(localstorage, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("time", time);
        editor.apply();
    }

    public String getTime() {
        SharedPreferences sharedpreferences = context.getSharedPreferences(localstorage, Context.MODE_PRIVATE);
        return sharedpreferences.getString("time", null);


    }
}
