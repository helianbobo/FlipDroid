package com.goal98.flipdroid.util;

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
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.exception.NoNetworkException;

import java.util.Calendar;

public class AlarmSender {

    private Toast mToast;

    private Activity activity;

    public AlarmSender(Activity activity) {
        this.activity = activity;
    }

    public static void sendInstantMessage(int msgId, Context context){
        String msg = context.getString(msgId);
        sendInstantMessage(msg,context);
    }

    public static void sendInstantMessage(String msg, Context context){
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.toast,
                null);
        layout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
        TextView title = (TextView) layout.findViewById(R.id.text);

        title.setText(msg);
        Toast toast = new Toast(context);
        toast.setGravity(Gravity.CENTER,0,200);
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
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(activity, msg,
                Toast.LENGTH_LONG);
        mToast.show();
    }
}
