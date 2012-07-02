package com.goal98.flipdroid2.util;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.goal98.flipdroid2.R;

import java.util.Calendar;

public class AlarmSender {

    private Context activity;
    private Toast toast;

    public AlarmSender(Context activity) {
        this.activity = activity;
    }

    public void sendInstantMessage(int msgId) {
        String msg = activity.getString(msgId);
        sendInstantMessage(msg);
    }

    public void sendInstantMessage(String msg) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        View layout = inflater.inflate(R.layout.toast,
                null);
        layout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
        TextView title = (TextView) layout.findViewById(R.id.text);

        title.setText(msg);
        if (toast == null)
            toast = new Toast(activity);
        toast.setGravity(Gravity.CENTER, 0, 200);
        toast.setDuration(2000);
        toast.setView(layout);

        toast.show();
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
        if (toast == null)
            toast = Toast.makeText(activity, msg,
                    Toast.LENGTH_LONG);
        else
            toast.setText(msg);
        toast.show();
    }
}
