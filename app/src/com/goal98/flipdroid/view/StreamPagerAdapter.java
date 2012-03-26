package com.goal98.flipdroid.view;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jleo
 * Date: 12-3-12
 * Time: 下午9:34
 * To change this template use File | Settings | File Templates.
 */
public class StreamPagerAdapter extends PagerAdapter implements ViewPagerTabProvider{
    private List<PullToRefreshListView> ptrs;
    private List<String> titles;
    private List<String> icons;

    public StreamPagerAdapter(List<PullToRefreshListView> ptrs, List<String> titles, List<String> icons) {
        this.ptrs = ptrs;
        this.titles = titles;
        this.icons = icons;
    }

    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    public int getCount() {
        return ptrs.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ((ViewPager) container).addView(ptrs.get(position));
        return ptrs.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView(ptrs.get(position));
    }



    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }


    @Override
    public String getTitle(int position) {
        return titles.get(position);
    }

    public String getIcon(int position) {
        return icons.get(position);  //To change body of implemented methods use File | Settings | File Templates.
    }
}
