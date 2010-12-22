package com.goal98.flipdroid.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.util.GestureUtil;


public class CoverActivity extends Activity {

    private boolean goingToSleep;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cover);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_MOVE:
                if(event.getHistorySize() > 0 && !goingToSleep){
                    if (GestureUtil.flipRight(event))
                        goToNextActivity();
                }
                break;
            case MotionEvent.ACTION_UP:
                if(!goingToSleep){
                    goToNextActivity();
                }
                break;

        }
        return true;
    }

    private void goToNextActivity() {
        startActivity(new Intent(this, IndexActivity.class));
        overridePendingTransition(android.R.anim.slide_in_left, R.anim.fade);
        goingToSleep = true;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        goingToSleep = false;
    }
}