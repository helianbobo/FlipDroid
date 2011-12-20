package com.goal98.flipdroid.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.goal98.flipdroid.R;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12/1/11
 * Time: 9:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class PreferenceUtil {
    public static boolean checkBooleanPreference(SharedPreferences preferences, Context c, int stringID){
        String key = c.getString(stringID);
        return preferences.getBoolean(key, false);
    }
}
