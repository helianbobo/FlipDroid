package com.goal98.flipdroid2.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;

/**
 * Created by IntelliJ IDEA.
 * User: jleo
 * Date: 12-3-12
 * Time: 下午3:57
 * To change this template use File | Settings | File Templates.
 */
public class ImageUtil {
    public static void recycleBmp(Bitmap bmp){
        if(null != bmp && !bmp.isRecycled()){
            bmp.recycle();
        }
    }

    public static Bitmap getBitmapFromView(View view,int width,int height) {
        int widthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
        view.measure(widthSpec, heightSpec);
        view.layout(0, 0, width, height);
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }
}
