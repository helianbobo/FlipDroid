package com.goal98.flipdroid.activity;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import com.goal98.flipdroid.R;

public class ConfigActivity extends PreferenceActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}