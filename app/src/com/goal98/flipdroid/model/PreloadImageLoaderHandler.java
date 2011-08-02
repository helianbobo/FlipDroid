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
public class PreloadImageLoaderHandler implements MyHandler {
    private Article article;
    private Drawable errorDrawable;
    private int height = 0;
    private int width = 0;


    public PreloadImageLoaderHandler(Article article) {
        this.article = article;
    }

    public boolean handleImageLoaded(Bitmap bitmap) {
        article.setLoading(false);
        if (bitmap != null) {

            if (bitmap == null)
                return false;

            int bmpWidth = bitmap.getWidth();

            int bmpHeight = bitmap.getHeight();

            //缩放图片的尺寸


            float scale = 0.0f;
            boolean largeScreen = false;
            if (DeviceInfo.height == 800) {
                largeScreen = true;
                scale = 0.6f;
            } else {
                scale = 0.4f;
            }

            Matrix matrix = new Matrix();

            matrix.postScale(scale, scale);


            //产生缩放后的Bitmap对象
            if (bmpWidth > 0 && bmpHeight > 0) {
                try {
                    Bitmap resizeBitmap = Bitmap.createBitmap(
                            bitmap, 0, 0, bmpWidth, bmpHeight, matrix, false);
                    bitmap.recycle();
                    article.setImageBitmap(resizeBitmap);
                } catch (Error e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            }
        }

        return false;
    }


    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
