package com.goal98.flipdroid.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.db.AccountDB;
import com.goal98.flipdroid.util.Constants;
import com.goal98.flipdroid.util.DeviceInfo;
import com.goal98.flipdroid.util.GestureUtil;


public class CoverActivity extends Activity {

    private boolean goingToSleep;

    private String deviceId;
    public static final int WIRELESS_SETTING = 1;

    protected Dialog onCreateDialog(int id) {
        Dialog dialog;
        switch (id) {
            case WIRELESS_SETTING:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.nonetwork)
                        .setCancelable(false)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                CoverActivity.this.finish();
                            }
                        });
                dialog = builder.create();
                break;

            default:
                dialog = null;
        }
        return dialog;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cover);

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Constants.TIKA_HOST = preferences.getString(getString(R.string.key_tika_host), Constants.TIKA_HOST);

        final View view = this.findViewById(R.id.flipbar);
        view.setVisibility(View.GONE);
        view.post(new Runnable() {
            public void run() {
                DeviceInfo.getInstance(CoverActivity.this);
            }
        });
        new AccountDB(getApplicationContext());

        TelephonyManager tManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        deviceId = tManager.getDeviceId();
        Log.v(this.getClass().getName(), "deviceId=" + deviceId);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
                    public void run() {
                        goToNextActivity();
                    }
                }, 2000);
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
        finish();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        goingToSleep = false;


    }

    @Override
    protected void onStart() {
        super.onStart();
        final View view = findViewById(R.id.flipbar);
        view.setVisibility(View.VISIBLE);
        final Animation animation = AnimationUtils.loadAnimation(this, R.anim.fadein);
        animation.setDuration(1700);
        view.startAnimation(animation);
    }
}