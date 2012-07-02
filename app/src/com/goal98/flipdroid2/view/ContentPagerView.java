package com.goal98.flipdroid2.view;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import com.goal98.flipdroid2.model.Article;

/**
 * Created by IntelliJ IDEA.
 * User: jleo
 * Date: 12-3-25
 * Time: 下午7:21
 * To change this template use File | Settings | File Templates.
 */
public class ContentPagerView extends ViewPager {

    private Article article;
    private PagerAdapter mPagerAdapter;

    public ContentPagerView(Context context, Article article) {
        super(context);


    }

}
