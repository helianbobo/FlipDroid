package com.goal98.flipdroid.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import com.goal98.flipdroid.R;

public class ConfigActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    SharedPreferences settings;
    private ListPreference browseModePreference;
    private ListPreference animationModePreference;
    private String browseModeKey;
    private String animationModeKey;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        settings.registerOnSharedPreferenceChangeListener(this);
        browseModeKey = getString(R.string.key_browse_mode_preference);
        animationModeKey = getString(R.string.key_animation_mode_preference);
        browseModePreference = (ListPreference) this.findPreference(getString(R.string.key_browse_mode_preference));
        animationModePreference = (ListPreference) this.findPreference(getString(R.string.key_animation_mode_preference));
        int browseModeIndex = findEntryIndex(settings, browseModeKey, browseModePreference);
        int animationModeIndex = findEntryIndex(settings, animationModeKey, animationModePreference);

        browseModePreference.setSummary(browseModePreference.getEntries()[browseModeIndex]);
        animationModePreference.setSummary(animationModePreference.getEntries()[animationModeIndex]);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if (key.equals(browseModeKey)) {
            setSummary(sharedPreferences, key, browseModePreference);
        }
        if (key.equals(animationModeKey)) {
            setSummary(sharedPreferences, key, animationModePreference);
        }
    }

    private void setSummary(SharedPreferences sharedPreferences, String key, ListPreference preference) {
        int index = findEntryIndex(sharedPreferences, key, preference);
        preference.setSummary(preference.getEntries()[index]);
    }

    private int findEntryIndex(SharedPreferences sharedPreferences, String key, ListPreference preference) {
        int index = 0;

        while (preference.getEntryValues()[index] != sharedPreferences.getString(key, "0")) {
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