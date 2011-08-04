package com.goal98.flipdroid.model;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import com.goal98.android.MyHandler;
import com.goal98.flipdroid.util.DeviceInfo;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 8/3/11
 * Time: 10:53 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class PreloadImageLoaderHandler implements MyHandler {
    protected Article article;
    protected String url;

    protected Bitmap scale(Bitmap bitmap) {
        if (bitmap != null) {


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
                    return resizeBitmap;
                } catch (Error e) {
                    e.printStackTrace();
                    return null;
                }

            }
            return null;
        } else return null;
    }

}
