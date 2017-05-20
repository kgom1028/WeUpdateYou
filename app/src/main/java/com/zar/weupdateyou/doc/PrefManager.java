package com.zar.weupdateyou.doc;

import android.content.Context;
import android.content.SharedPreferences;
/**
 * Created by KJS on 11/15/2016.
 */
public class PrefManager {
    public static final int PREF_MODE 				= Context.MODE_PRIVATE;
    public static final String PREF_NAME 			= "WUY";
    public static void savePrefString(Context context, String key, String val)
    {
        int mode = PREF_MODE;
        String name = PREF_NAME;
        SharedPreferences mySharedPreferences = context.getSharedPreferences(name, mode);
        if(mySharedPreferences != null){
            SharedPreferences.Editor editor = mySharedPreferences.edit();
            editor.putString(key, val);
            editor.commit();
        }
    }

    public static void savePrefBoolean(Context context, String key, boolean val)
    {
        int mode = PREF_MODE;
        String name = PREF_NAME;
        SharedPreferences mySharedPreferences = context.getSharedPreferences(name, mode);
        if(mySharedPreferences != null){
            SharedPreferences.Editor editor = mySharedPreferences.edit();
            editor.putBoolean(key, val);
            editor.commit();
        }
    }

    public static void savePrefInt(Context context, String key, int val)
    {
        int mode = PREF_MODE;
        String name = PREF_NAME;
        SharedPreferences mySharedPreferences = context.getSharedPreferences(name, mode);
        if(mySharedPreferences != null){
            SharedPreferences.Editor editor = mySharedPreferences.edit();
            editor.putInt(key, val);
            editor.commit();
        }
    }

    public static String readPrefString(Context context,String key)
    {
        int mode = PREF_MODE;
        String name = PREF_NAME;
        String result = "";
        SharedPreferences mySharedPreferences =context.getSharedPreferences(name, mode);
        if(mySharedPreferences != null){
            result = mySharedPreferences.getString(key, "");
        }
        return result;
    }

    public static void savePrefFloat(Context context, String key, float val)
    {
        int mode = PREF_MODE;
        String name = PREF_NAME;
        SharedPreferences mySharedPreferences = context.getSharedPreferences(name, mode);
        if(mySharedPreferences != null){
            SharedPreferences.Editor editor = mySharedPreferences.edit();
            editor.putFloat(key, val);
            editor.commit();
        }
    }
    public static float readPrefFloat(Context context, String key)
    {
        int mode = PREF_MODE;
        String name=PREF_NAME;
        float result= 0;
        SharedPreferences mySharedPreferences = context.getSharedPreferences(name,mode);
        if(mySharedPreferences != null)
            result = mySharedPreferences.getFloat(key, 0);
        return result;
    }

    public static SharedPreferences getPreference(Context context)
    {
        int mode = PREF_MODE;
        String name = PREF_NAME;
        SharedPreferences mySharedPreferences = context.getSharedPreferences(name,mode);
        return mySharedPreferences;
    }
    public static Boolean readPrefBoolean(Context context, String key)
    {
        int mode = PREF_MODE;
        String name = PREF_NAME;
        Boolean result = false;
        SharedPreferences mySharedPreferences =context.getSharedPreferences(name, mode);
        if(mySharedPreferences != null){
            result = mySharedPreferences.getBoolean(key, false);
        }
        return result;
    }

    public static Boolean readPrefBoolean(Context context, String key, boolean defaultValue)
    {
        int mode = PREF_MODE;
        String name = PREF_NAME;
        Boolean result = false;
        SharedPreferences mySharedPreferences =context.getSharedPreferences(name, mode);
        if(mySharedPreferences != null){
            result = mySharedPreferences.getBoolean(key, defaultValue);
        }
        return result;
    }

    public static int readPrefInt(Context context, String key)
    {
        int mode = PREF_MODE;
        String name = PREF_NAME;
        int result = 0;
        SharedPreferences mySharedPreferences =context.getSharedPreferences(name, mode);
        if(mySharedPreferences != null){
            result = mySharedPreferences.getInt(key, 0);
        }
        return result;
    }

}
