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

    protected Bitmap scale(Bitmap bitmap) {
        if (bitmap != null) {


            int bmpWidth = bitmap.getWidth();

            int bmpHeight = bitmap.getHeight();

            //缩放图片的尺寸


            int heightDip = 160 * bmpHeight / DisplayMetrics.DENSITY_DEFAULT;
            int widthDip = 160 * bmpWidth / DisplayMetrics.DENSITY_DEFAULT;

            float scale = 1;
            if (heightDip >= widthDip) {
                if (DeviceInfo.height < (4 * heightDip / 3f)) {
                    scale = DeviceInfo.height/(4 * heightDip / 3f)  ;
                }
            } else {
                if (DeviceInfo.width < widthDip) {
                    scale = DeviceInfo.width/(float) widthDip  ;
                }
            }
            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);

            //产生缩放后的Bitmap对象
            if (bmpWidth > 0 && bmpHeight > 0) {
                try {
                    Bitmap resizeBitmap = Bitmap.createBitmap(
                            bitmap, 0, 0, bmpWidth, bmpHeight, matrix, false);
                    onImageResized(resizeBitmap,url);
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

    public void onImageResized(Bitmap resizeBitmap, String imageUrl){
        System.out.println("image " + url + " resized");
        article.getImagesMap().put(imageUrl,resizeBitmap);
    }
}
