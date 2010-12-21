package com.goal98.flipdroid.util;

import android.view.MotionEvent;

public class GestureUtil {

    public static boolean flipRight(MotionEvent event){

        boolean result = false;

        if(event.getAction() == MotionEvent.ACTION_MOVE){
            result =  event.getX() > event.getHistoricalX(0);
        }

        return result;

    }

}
