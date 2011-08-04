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
public class PreloadPrimaryImageLoaderHandler extends PreloadImageLoaderHandler implements MyHandler {

    private Drawable errorDrawable;
    private int height = 0;
    private int width = 0;


    public PreloadPrimaryImageLoaderHandler(Article article, String url) {
        this.article = article;
        this.url = url;
    }

    public boolean handleImageLoaded(Bitmap bitmap) {
        article.setLoading(false);
        Bitmap scaledBitmap = scale(bitmap);
        article.setImageBitmap(scaledBitmap);
        article.getImagesMap().put(url, scaledBitmap);
        return scaledBitmap != null;
    }


    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
