package com.goal98.flipdroid.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MotionEvent;
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.db.AccountDB;
import com.goal98.flipdroid.util.GestureUtil;


public class CoverActivity extends Activity {

    private boolean goingToSleep;

    private String deviceId;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cover);

        new AccountDB(getApplicationContext());

        TelephonyManager tManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        deviceId = tManager.getDeviceId();
        Log.v(this.getClass().getName(), "deviceId="+deviceId);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (event.getHistorySize() > 0 && !goingToSleep) {
                    if (GestureUtil.flipRight(event))
                        goToNextActivity();
                }
                break;
            case MotionEvent.ACTION_UP:
                boolean emulator = deviceId != null && deviceId.startsWith("0000");
                if (!goingToSleep && emulator) {
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