package com.goal98.flipdroid.model;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import com.goal98.android.MyHandler;
import com.goal98.flipdroid.util.DeviceInfo;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 6/10/11
 * Time: 5:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class PreloadSecondaryImageLoaderHandler extends PreloadImageLoaderHandler implements MyHandler {
    private Article article;
    private Drawable errorDrawable;
    private int height = 0;
    private int width = 0;
    private String url;


    public PreloadSecondaryImageLoaderHandler(Article article, String url) {
        this.article = article;
        this.url = url;
    }

    public boolean handleImageLoaded(Bitmap bitmap) {
        Bitmap scaledBitmap = scale(bitmap);
        article.onSecondaryImageLoaded(bitmap,url);
        return scaledBitmap != null;
    }


    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
