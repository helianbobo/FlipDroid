package com.goal98.flipdroid.util;

import android.util.Log;
import android.view.MotionEvent;

public class GestureUtil {

    public static final int minDelta = 1;

    public static boolean flipRight(MotionEvent event){

        boolean result = false;

        if(event.getAction() == MotionEvent.ACTION_MOVE){

            float delta = event.getX() - event.getHistoricalX(0);
            Log.v(GestureUtil.class.getName(), "delta="+delta);
            result =  delta > minDelta;
        }

        return result;

    }

    public static boolean flipLeft(MotionEvent event){

        boolean result = false;

        if(event.getAction() == MotionEvent.ACTION_MOVE){
            result =  event.getHistoricalX(0) - event.getX()  > minDelta;
        }

        return result;

    }

}
