//package com.goal98.girl.model;
//
//import android.graphics.Bitmap;
//import android.graphics.Matrix;
//import android.graphics.drawable.Drawable;
//import com.goal98.android.MyHandler;
//import com.srz.androidtools.util.DeviceInfo;
//
//import java.net.MalformedURLException;
//import java.net.URL;
//
///**
// * Created by IntelliJ IDEA.
// * User: Administrator
// * Date: 6/10/11
// * Time: 5:06 PM
// * To change this template use File | Settings | File Templates.
// */
//public class PreloadPrimaryImageLoaderHandler extends PreloadImageLoaderHandler implements MyHandler {
//
//    private Drawable errorDrawable;
//    private int height = 0;
//    private int width = 0;
//
//
//    public PreloadPrimaryImageLoaderHandler(Article article, String url,DeviceInfo deviceInfo) {
//        super(deviceInfo);
//        this.article = article;
//        this.url = url;
//    }
//
//    public boolean handleImageLoaded(Bitmap bitmap) {
//        try {
//            Bitmap scaledBitmap = scale(bitmap);
////            article.getImagesMap().put(url, scaledBitmap);
//            article.setImageUrl(new URL(url));
//            return scaledBitmap != null;
//        } catch (MalformedURLException e) {
//            return false;
//        } finally {
//            article.setLoading(false);
//        }
//    }
//
//
//    public void setWidth(int width) {
//        this.width = width;
//    }
//
//    public void setHeight(int height) {
//        this.height = height;
//    }
//}
