package com.goal98.flipdroid.view;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import com.goal98.flipdroid.model.Article;
import com.goal98.flipdroid.model.ContentPagerAdapter;import com.goal98.flipdroid.util.DeviceInfo;

/**
 * Created by IntelliJ IDEA.
 * User: jleo
 * Date: 12-3-25
 * Time: 下午7:21
 * To change this template use File | Settings | File Templates.
 */
public class ContentPagerView extends ViewPager {

    private final DeviceInfo deviceInfo;
    private Article article;
    private final PagerAdapter mPagerAdapter;

    public ContentPagerView(Context context, Article article) {
        super(context);
        deviceInfo = DeviceInfo.getInstance((Activity) context);
        this.article = article;
        mPagerAdapter = new ContentPagerAdapter(article,deviceInfo, (Activity) context);
        setAdapter(mPagerAdapter);
    }



   
    
    
}
