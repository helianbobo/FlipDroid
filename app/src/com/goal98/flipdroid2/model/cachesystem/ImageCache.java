package com.goal98.flipdroid2.model.cachesystem;

import android.graphics.drawable.Drawable;

import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 4/9/11
 * Time: 2:30 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ImageCache {
    public Drawable load(URL url);

    public void put(String key, Drawable drawable);
}
