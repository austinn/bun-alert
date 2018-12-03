package com.refect.bunalert.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

/**
 * Created by austinn on 8/4/2017.
 */

public class PrefUtils {

    final public static String PREFS_KEY = "com.refect.bunalert";

    // Store string setting
    public static void storeSetting(String settingKey, String settingValue, Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(settingKey, settingValue);
        // Commit the edits!
        editor.apply();
    }

    // Get string setting
    public static String getSetting(String settingName, String defaultValue, Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
        settings = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
        return settings.getString(settingName, defaultValue);
    }

    // Store string setting
    public static void storeSetting(String settingKey, boolean settingValue, Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(settingKey, settingValue);
        // Commit the edits!
        editor.apply();
    }

    // Get string setting
    public static boolean getSetting(String settingName, boolean defaultValue, Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
        settings = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
        return settings.getBoolean(settingName, defaultValue);
    }

    // Store string setting
    public static void storeSetting(String settingKey, int settingValue, Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(settingKey, settingValue);
        // Commit the edits!
        editor.apply();
    }

    // Get string setting
    public static int getSetting(String settingName, int defaultValue, Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
        settings = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
        return settings.getInt(settingName, defaultValue);
    }

    // Store string setting
    public static void storeSetting(String settingKey, Set<String> settingValue, Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putStringSet(settingKey, settingValue);
        // Commit the edits!
        editor.apply();
    }

    // Get string setting
    public static Set<String> getSetting(String settingName, Set<String> defaultValue, Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
        settings = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
        return settings.getStringSet(settingName, defaultValue);
    }

}
