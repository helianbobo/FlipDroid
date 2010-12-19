package com.goal98.flipdroid.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import com.goal98.flipdroid.R;


public class CoverActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cover);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_UP:
                startActivity(new Intent(this, PageActivity.class));
                overridePendingTransition(android.R.anim.slide_out_right, R.anim.hold);
        }
        return true;
    }
}