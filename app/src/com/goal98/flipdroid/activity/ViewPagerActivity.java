package com.goal98.flipdroid.activity;

import android.app.ActivityGroup;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.view.PopupWindowManager;
import com.goal98.flipdroid.view.TopBar;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: janexie
 * Date: 12-1-26
 * Time: 下午4:21
 * To change this template use File | Settings | File Templates.
 */
public class ViewPagerActivity extends ActivityGroup {
    private final ArticleLoader articleLoader = new ArticleLoader(this, 20);
    private ViewPager mViewPager;
    private PagerTitleStrip mPagerTitleStrip;
    private PopupWindow mPopupWindow;

    private View addSourcePopUp;
    private AddSourcePopupViewBuilder addSourcePopupViewBuilder;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewpager);

        addSourcePopupViewBuilder = new AddSourcePopupViewBuilder(this);
        addSourcePopUp = addSourcePopupViewBuilder.buildAddSourcePopupView(this);

        mViewPager = (ViewPager) findViewById(R.id.viewpager);

        final TopBar topbar = (TopBar) findViewById(R.id.topbar);

        topbar.setTitle(this.getString(R.string.app_name));
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void onPageSelected(int i) {

                topbar.reset();
                if (i == 0) {
                    topbar.addButton(TopBar.IMAGE, R.drawable.refresh_black_48, new LinearLayout.OnClickListener() {
                        public synchronized void onClick(View view) {
//                            ((StreamActivity)getCurrentActivity()).getmPullRefreshListView().setRefreshing();

                        }
                    });
                }
                if (i == 1) {
                    topbar.addButton(TopBar.IMAGE, R.drawable.ic_btn_add_source, new LinearLayout.OnClickListener() {
                        public synchronized void onClick(View view) {
                            if (mPopupWindow != null && mPopupWindow.isShowing()) {
                                mPopupWindow.dismiss();
                                return;
                            }


                            mPopupWindow = new PopupWindow(addSourcePopUp, ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT);
                            mPopupWindow.setOutsideTouchable(true);
                            mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
                            mPopupWindow.showAsDropDown(topbar.getTableRow());
                            PopupWindowManager.getInstance().setWindow(mPopupWindow);
                        }
                    });
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });
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
                ConfigActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        View configView = configWindow.getDecorView();

        final ArrayList<View> views = new ArrayList<View>();
        views.add(streamView);
        views.add(indexView);
        views.add(configView);

        final ArrayList<String> titles = new ArrayList<String>();
        titles.add(this.getString(R.string.mystream));
        titles.add(this.getString(R.string.my_feed));
        titles.add(this.getString(R.string.config));

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


