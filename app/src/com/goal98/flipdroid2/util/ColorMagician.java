package com.goal98.flipdroid2.util;

import android.app.Activity;
import android.view.View;

/**
 * Created by IntelliJ IDEA.
 * User: jleo
 * Date: 12-3-18
 * Time: 下午7:58
 * To change this template use File | Settings | File Templates.
 */
public class ColorMagician {
    private Activity activity;
    private int[] resources;
    private String[] rgbStrings;
    private View[] views;

    public ColorMagician(Activity activity, int[] resources, String[] rgbstrings){
        this.activity = activity;
        this.resources = resources;
        this.rgbStrings = rgbstrings;
        views = new View[resources.length];
    }
    public void changeColor(){

    }
}
