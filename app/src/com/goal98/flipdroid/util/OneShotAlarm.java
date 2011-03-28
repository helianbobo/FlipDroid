package com.goal98.flipdroid.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class OneShotAlarm extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        String msg = intent.getExtras().getString("msg");
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
