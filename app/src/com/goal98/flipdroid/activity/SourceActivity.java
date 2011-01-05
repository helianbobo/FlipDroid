package com.goal98.flipdroid.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import com.goal98.flipdroid.R;

public class SourceActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.source);

        String type = getIntent().getExtras().getString("type");
        Log.v(this.getClass().getName(), "Account type:"+type);

    }
}