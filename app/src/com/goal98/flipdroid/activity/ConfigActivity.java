package com.goal98.flipdroid.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import com.goal98.flipdroid.R;
import com.mobclick.android.MobclickAgent;

public class ConfigActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    SharedPreferences settings;
//    private ListPreference browseModePreference;
    private CheckBoxPreference loadImagePreference;
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
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        settings.registerOnSharedPreferenceChangeListener(this);
//        browseModeKey = getString(R.string.key_browse_mode_preference);
        animationModeKey = getString(R.string.key_animation_mode_preference);
//        browseModePreference = (ListPreference) this.findPreference(getString(R.string.key_browse_mode_preference));
        loadImagePreference = (CheckBoxPreference) this.findPreference(getString(R.string.key_load_image_preference));
        autoUpdateNonWIFIPreference = (CheckBoxPreference) this.findPreference(getString(R.string.key_auto_check_update_nonwifi_preference));

//        animationModePreference = (ListPreference) this.findPreference(getString(R.string.key_animation_mode_preference));
//        int browseModeIndex = findEntryIndex(settings, browseModeKey, browseModePreference);
//        int animationModeIndex = findEntryIndex(settings, animationModeKey, animationModePreference);

//        browseModePreference.setSummary(browseModePreference.getEntries()[browseModeIndex]);
//        animationModePreference.setSummary(animationModePreference.getEntries()[animationModeIndex]);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
//        if (key.equals(browseModeKey)) {
//            setSummary(sharedPreferences, key, browseModePreference);
//        }
//        if (key.equals(animationModeKey)) {
//            setSummary(sharedPreferences, key, animationModePreference);
//        }
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