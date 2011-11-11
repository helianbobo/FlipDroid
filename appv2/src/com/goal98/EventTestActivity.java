package com.goal98;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class EventTestActivity extends Activity {

    private String TAG = this.getClass().getName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate: savedInstanceState = " + savedInstanceState);
        // The activity is being created.
    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG, "onStart");
        // The activity is about to become visible.
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e(TAG, "onRestart");
        // The activity is about to become visible.
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
        // The activity has become visible (it is now "resumed").
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "onPause");
        // Another activity is taking focus (this activity is about to be "paused").
    }
    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "onStop");
        // The activity is no longer visible (it is now "stopped")
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");
        // The activity is about to be destroyed.
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.e(TAG, "onSaveInstanceState:" + outState);
    }
}
