package com.goal98.flipdroid.model;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.DisplayMetrics;
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
    private DeviceInfo deviceInfo;

    public PreloadImageLoaderHandler(DeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    protected Bitmap scale(Bitmap bitmap) {
        if (bitmap == null)
            return null;

        int bmpWidth = bitmap.getWidth();
        int bmpHeight = bitmap.getHeight();
        int heightDip = (int) (160 * bmpHeight / deviceInfo.getDensity());
        int widthDip = (int) (160 * bmpWidth / deviceInfo.getDensity());

        float scale = 1;
//        if (heightDip >= widthDip) {
//            if (deviceInfo.getHeight() <= (4 * heightDip / 3f)) {
//                scale = deviceInfo.getHeight() / (4 * heightDip / 3f);
//            }
//        } else {
//            if (deviceInfo.getWidth() < widthDip) {
//                scale = deviceInfo.getWidth() / (float) widthDip;
//            }
//        }
        scale = (deviceInfo.getWidth()-60) / (float) widthDip;
        if (scale > 1) {
            scale = 1.0f;
        }
        System.out.println("scale"+scale);
        if (scale == 1.0)
            return bitmap;

        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        //产生缩放后的Bitmap对象
        if (bmpWidth > 0 && bmpHeight > 0) {
            try {
                Bitmap resizeBitmap = Bitmap.createBitmap(
                        bitmap, 0, 0, bmpWidth, bmpHeight, matrix, false);
//                    onImageResized(resizeBitmap,url);
                bitmap.recycle();

                return resizeBitmap;
            } catch (Error e) {
                e.printStackTrace();
                return null;
            }

        }
        return null;
    }

    public void onImageResized(Bitmap resizeBitmap, String imageUrl) {
        System.out.println("image " + url + " resized");
        article.getImagesMap().put(imageUrl, resizeBitmap);
    }
}
