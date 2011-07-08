package com.goal98.flipdroid.util;

import android.util.Log;
import android.view.MotionEvent;

public class GestureUtil {

    public static final int minDelta = 3;

    public static boolean flipRight(MotionEvent event){

        boolean result = false;

        if(event.getAction() == MotionEvent.ACTION_MOVE && !flipVertical(event)){

            float delta = deltaRight(event);
            Log.v(GestureUtil.class.getName(), "flipRight delta="+delta);
            result =  delta > minDelta;
        }

        return result;

    }

    private static float deltaRight(MotionEvent event) {
        return event.getX() - event.getHistoricalX(event.getHistorySize()-1);
    }

    private static float deltaHorizontal(MotionEvent event) {
        return Math.abs(event.getX() - event.getHistoricalX(event.getHistorySize()-1));
    }

    private static float deltaVertical(MotionEvent event) {
        return Math.abs(event.getY() - event.getHistoricalY(event.getHistorySize()-1));
    }

    private static boolean flipVertical(MotionEvent event){
        return deltaHorizontal(event) < deltaVertical(event);
    }

    public static boolean flipUp(MotionEvent event){

        boolean result = false;

        if(event.getAction() == MotionEvent.ACTION_MOVE && flipVertical(event)){

            float delta = deltaUp(event);
            Log.v(GestureUtil.class.getName(), "flipUp delta="+delta);
            result =  delta > minDelta;
        }

        return result;

    }

    private static float deltaUp(MotionEvent event) {
        return event.getY() - event.getHistoricalY(event.getHistorySize()-1);
    }

    public static boolean flipLeft(MotionEvent event){

        boolean result = false;

        if(event.getAction() == MotionEvent.ACTION_MOVE && !flipVertical(event)){
            float delta = deltaLeft(event);
            Log.v(GestureUtil.class.getName(), "flipLeft delta="+delta);
            result =  delta > minDelta;
        }

        return result;

    }

    private static float deltaLeft(MotionEvent event) {
        return event.getHistoricalX(event.getHistorySize() - 1) - event.getX();
    }

    public static boolean flipDown(MotionEvent event){

        boolean result = false;

        if(event.getAction() == MotionEvent.ACTION_MOVE && flipVertical(event)){
            float delta = deltaDown(event);
            Log.v(GestureUtil.class.getName(), "flipDown delta="+delta);
            result =  delta > minDelta;
        }

        return result;

    }

    private static float deltaDown(MotionEvent event) {
        return event.getHistoricalY(event.getHistorySize() - 1) - event.getY();
    }

}
