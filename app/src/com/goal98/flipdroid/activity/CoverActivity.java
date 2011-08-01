package com.goal98.flipdroid.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Window;
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.db.AccountDB;
import com.goal98.flipdroid.util.DeviceInfo;
import com.goal98.flipdroid.util.GestureUtil;
import com.goal98.flipdroid.util.NetworkUtil;


public class CoverActivity extends Activity {

    private boolean goingToSleep;

    private String deviceId;
    public static final int WIRELESS_SETTING = 1;
     public static int statusBarHeight;
    public static int titleBarHeight;

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
        this.findViewById(R.id.flipbar).post(new Runnable() {
            public void run() {
                Rect rect = new Rect();
                Window window = getWindow();
                window.getDecorView().getWindowVisibleDisplayFrame(rect);
                statusBarHeight = rect.top;
                int contentViewTop =
                        window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
                titleBarHeight = contentViewTop - statusBarHeight;
                DeviceInfo.displayHeight = (int) ((int) (CoverActivity.this.getWindowManager().getDefaultDisplay().getHeight()) - statusBarHeight - titleBarHeight * 2.2);
                DeviceInfo.displayWidth = (int) (CoverActivity.this.getWindowManager().getDefaultDisplay().getWidth()) - 20;
                DeviceInfo.width = CoverActivity.this.getWindowManager().getDefaultDisplay().getWidth();
                DeviceInfo.height = CoverActivity.this.getWindowManager().getDefaultDisplay().getHeight();
            }
        });
//        if (!NetworkUtil.isNetworkAvailable(CoverActivity.this)) {
//            showDialog(WIRELESS_SETTING);
//            return;
//        }
        new AccountDB(getApplicationContext());

        TelephonyManager tManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        deviceId = tManager.getDeviceId();
        Log.v(this.getClass().getName(), "deviceId="+deviceId);

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
}