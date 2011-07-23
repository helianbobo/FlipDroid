package com.goal98.flipdroid.model;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import com.goal98.android.MyHandler;

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
        if (bitmap != null) {

            if (bitmap == null)
                return false;

            int bmpWidth = bitmap.getWidth();

            int bmpHeight = bitmap.getHeight();

            //缩放图片的尺寸

            float scaleWidth = 0.3f;     //按固定大小缩放  sWidth 写多大就多大

            float scaleHeight = 0.3f;  //


            Matrix matrix = new Matrix();

            matrix.postScale(scaleWidth, scaleHeight);


            //产生缩放后的Bitmap对象

            Bitmap resizeBitmap = Bitmap.createBitmap(

                    bitmap, 0, 0, bmpWidth, bmpHeight, matrix, false);
            article.setImageBitmap(bitmap);
            return true;
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
