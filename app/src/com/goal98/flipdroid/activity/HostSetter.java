package com.goal98.flipdroid.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.util.Constants;
import com.mobclick.android.MobclickAgent;

public class HostSetter {
    private final Activity activity;

    public HostSetter(Activity activity) {
        this.activity = activity;
    }

    public void setHost() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        String host = MobclickAgent.getConfigParams(activity, activity.getString(R.string.umeng_host_key));
        if (host != null && host.length() > 0) {
            preferences.edit().putString(activity.getString(R.string.key_tika_host), host).commit();
        } else {
            host = preferences.getString(activity.getString(R.string.key_tika_host), Constants.TIKA_HOST);
        }
        Constants.TIKA_HOST = host;//
    }
}