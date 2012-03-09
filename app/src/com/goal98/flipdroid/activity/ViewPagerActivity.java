package com.goal98.flipdroid.activity;

import android.app.Activity;
import android.app.ActivityGroup;
import android.app.TabActivity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TabHost;
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.db.RSSURLDB;
import com.goal98.flipdroid.db.RecommendSourceDB;
import com.goal98.flipdroid.db.SourceContentDB;
import com.goal98.flipdroid.db.SourceDB;
import com.goal98.flipdroid.model.SourceUpdateManager;
import com.goal98.flipdroid.model.cachesystem.CachedArticleSource;
import com.goal98.flipdroid.model.cachesystem.SourceCache;
import com.goal98.flipdroid.model.cachesystem.SourceUpdateable;
import com.goal98.flipdroid.util.AlarmSender;
import com.goal98.flipdroid.util.DeviceInfo;
import com.goal98.flipdroid.view.PopupWindowManager;
import com.goal98.flipdroid.view.TopBar;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: janexie
 * Date: 12-1-26
 * Time: 下午4:21
 * To change this template use File | Settings | File Templates.
 */
public class ViewPagerActivity extends ActivityGroup {
    private ArticleAdapter adapter;
    //    private RadioButton[] mRadioButtons;
    private TabHost tabHost;
    private DeviceInfo deviceInfo;
    private int bottomHeight;
    private LayoutInflater inflator;
    private final ArticleLoader articleLoader = new ArticleLoader(this, 20);
    private AddSourcePopupViewBuilder addSourcePopupViewBuilder;
    private PopupWindow mPopupWindow;
    private PullToRefreshListView mPullRefreshListView;
    private FrameLayout tabcontent;
    private Handler handler = new Handler();
    private ViewPager mViewPager;
    private PagerTitleStrip mPagerTitleStrip;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewpager);

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mPagerTitleStrip = (PagerTitleStrip) findViewById(R.id.pagertitle);

        HostSetter hostSetter = new HostSetter(this);
        hostSetter.setHost();
        hostSetter = null;

        Window streamWindow = getLocalActivityManager().startActivity("", new Intent(ViewPagerActivity.this,
                StreamActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        View streamView = streamWindow.getDecorView();

        Window indexWindow = getLocalActivityManager().startActivity("", new Intent(ViewPagerActivity.this,
                IndexActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        View indexView = indexWindow.getDecorView();

        Window configWindow = getLocalActivityManager().startActivity("", new Intent(ViewPagerActivity.this,
                StreamActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        View configView = configWindow.getDecorView();

        //每个页面的Title数据
        final ArrayList<View> views = new ArrayList<View>();
        views.add(streamView);
        views.add(indexView);
        views.add(configView);

        final ArrayList<String> titles = new ArrayList<String>();
        titles.add(this.getString(R.string.mystream));
        titles.add(this.getString(R.string.my_feed));
        titles.add(this.getString(R.string.config));

        //填充ViewPager的数据适配器
        PagerAdapter mPagerAdapter = new PagerAdapter() {
            public boolean isViewFromObject(View arg0, Object arg1) {
                return arg0 == arg1;
            }

            public int getCount() {
                return views.size();
            }

            public void destroyItem(View container, int position, Object object) {
                ((ViewPager) container).removeView(views.get(position));
            }

            public CharSequence getPageTitle(int position) {
                return titles.get(position);
            }

            @Override
            public Object instantiateItem(View container, int position) {
                ((ViewPager) container).addView(views.get(position));
                return views.get(position);
            }
        };

        mViewPager.setAdapter(mPagerAdapter);
    }


    @Override
    protected void onPause() {
        super.onPause();
        articleLoader.closeDB();
    }

    @Override
    protected void onResume() {
        super.onResume();
        articleLoader.reopen();
    }
}


