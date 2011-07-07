package com.goal98.flipdroid.util;

import android.util.Log;
import android.view.MotionEvent;

public class GestureUtil {

    public static final int minDelta = 3;

    public static boolean flipRight(MotionEvent event){

        boolean result = false;

        if(event.getAction() == MotionEvent.ACTION_MOVE){

            float delta = event.getX() - event.getHistoricalX(event.getHistorySize()-1);
            Log.v(GestureUtil.class.getName(), "delta="+delta);
            result =  delta > minDelta;
        }

        return result;

    }

    public static boolean flipUp(MotionEvent event){

        boolean result = false;

        if(event.getAction() == MotionEvent.ACTION_MOVE){

            float delta = event.getY() - event.getHistoricalY(event.getHistorySize()-1);
            Log.v(GestureUtil.class.getName(), "delta="+delta);
            result =  delta > minDelta;
        }

        return result;

    }

    public static boolean flipLeft(MotionEvent event){

        boolean result = false;

        if(event.getAction() == MotionEvent.ACTION_MOVE){
            result =  event.getHistoricalX(event.getHistorySize()-1) - event.getX()  > minDelta;
        }

        return result;

    }

    public static boolean flipDown(MotionEvent event){

        boolean result = false;

        if(event.getAction() == MotionEvent.ACTION_MOVE){
            result =  event.getHistoricalY(event.getHistorySize()-1) - event.getY()  > minDelta;
        }

        return result;

    }

}
