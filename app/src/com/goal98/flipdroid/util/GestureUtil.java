package com.goal98.flipdroid.util;

import android.view.MotionEvent;

public class GestureUtil {

    public static boolean flipRight(MotionEvent event){

        boolean result = false;

        if(event.getAction() == MotionEvent.ACTION_MOVE){
            result =  event.getX() - event.getHistoricalX(0) > 5;
        }

        return result;

    }

    public static boolean flipLeft(MotionEvent event){

        boolean result = false;

        if(event.getAction() == MotionEvent.ACTION_MOVE){
            result =  event.getHistoricalX(0) - event.getX()  > 5;
        }

        return result;

    }

}
