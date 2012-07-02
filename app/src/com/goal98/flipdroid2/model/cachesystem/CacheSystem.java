package com.goal98.flipdroid2.model.cachesystem;

import android.content.Context;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 4/9/11
 * Time: 2:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class CacheSystem {
    public static ImageCache getImageCache() {
        return new FakeImageCache();
    }

    public static TikaCache getTikaCache(Context context) {
        return SQLLiteTikaCache.getInstance(context);
    }
}
