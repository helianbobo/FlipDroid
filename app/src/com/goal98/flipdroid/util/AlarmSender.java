package com.goal98.flipdroid.util;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.widget.Toast;
import com.goal98.flipdroid.exception.NoNetworkException;

import java.util.Calendar;

public class AlarmSender {

    private Toast mToast;

    private Activity activity;

    public AlarmSender(Activity activity) {
        this.activity = activity;
    }

    public void sendAlarm(String msg) {
        Intent intent = new Intent(activity, OneShotAlarm.class);
        intent.putExtra("msg", msg);
        PendingIntent sender = PendingIntent.getBroadcast(activity,
                0, intent, 0);

        // We want the alarm to go off 30 seconds from now.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, 30);

        // Schedule the alarm!
        AlarmManager am = (AlarmManager) activity.getSystemService(Activity.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);

        // Tell the user about what we did.
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(activity, msg,
                Toast.LENGTH_LONG);
        mToast.show();
    }
}
