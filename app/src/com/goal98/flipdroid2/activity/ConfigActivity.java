package com.goal98.flipdroid2.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.*;
import android.widget.ListView;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.goal98.flipdroid2.R;
import com.mobclick.android.MobclickAgent;

public class ConfigActivity extends SherlockPreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    SharedPreferences settings;
//    private ListPreference browseModePreference;
    private CheckBoxPreference saveWifi;
    private CheckBoxPreference autoUpdateNonWIFIPreference;
    //    private ListPreference animationModePreference;
//    private String browseModeKey;
    private String animationModeKey;

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }


    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Sherlock);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config);

        addPreferencesFromResource(R.xml.preferences);
        PreferenceScreen screen = getPreferenceScreen();
        ListView listView = (ListView) findViewById(android.R.id.list);
        screen.bind(listView);
        listView.setAdapter(screen.getRootAdapter());

        settings = PreferenceManager.getDefaultSharedPreferences(this);
        settings.registerOnSharedPreferenceChangeListener(this);
        animationModeKey = getString(R.string.key_animation_mode_preference);
        saveWifi = (CheckBoxPreference) this.findPreference(getString(R.string.key_save_wifi));
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    }

    private void setSummary(SharedPreferences sharedPreferences, String key, ListPreference preference) {
        int index = findEntryIndex(sharedPreferences, key, preference);
        preference.setSummary(preference.getEntries()[index]);
    }

    private int findEntryIndex(SharedPreferences sharedPreferences, String key, ListPreference preference) {
        int index = 0;

        while (!preference.getEntryValues()[index].equals(sharedPreferences.getString(key, "0"))) {
            index++;
            if (index >= preference.getEntryValues().length) {
                return 0;
            }
        }
        if (index >= preference.getEntryValues().length) {
            return 0;
        }
        return index;
    }
}