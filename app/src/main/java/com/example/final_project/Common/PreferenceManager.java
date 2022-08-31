package com.example.final_project.Common;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {

    private static  PreferenceManager INSTANCE;
    private  static SharedPreferences preferences;

    synchronized public static PreferenceManager getInstance(Context context){
        if(INSTANCE==null){
            INSTANCE=new PreferenceManager();
            preferences=context.getSharedPreferences("ImageDataFile",Context.MODE_PRIVATE);
        }
        return INSTANCE;
    }

    public void setString(String key,String value){
        preferences.edit().putString(key, value).apply();
    }

    public String getString(String key){
        return preferences.getString(key,"");
    }

    public void delObj(String key){
        preferences.edit().remove(key).commit();

    }

}