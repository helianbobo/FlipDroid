package com.goal98.flipdroid.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.*;
import android.widget.*;
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.db.RSSURLDB;
import com.goal98.flipdroid.db.RecommendSourceDB;
import com.goal98.flipdroid.db.SourceContentDB;
import com.goal98.flipdroid.db.SourceDB;
import com.goal98.flipdroid.model.SourceUpdateManager;
import com.goal98.flipdroid.model.cachesystem.SourceCache;
import com.goal98.flipdroid.multiscreen.MultiScreenSupport;
import com.goal98.flipdroid.util.AlarmSender;
import com.goal98.flipdroid.util.DeviceInfo;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

/**
 * Created by IntelliJ IDEA.
 * User: janexie
 * Date: 12-1-26
 * Time: 下午4:21
 * To change this template use File | Settings | File Templates.
 */
public class TabActivity extends android.app.TabActivity {
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

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab);

        HostSetter hostSetter = new HostSetter(this);
        hostSetter.setHost();
        hostSetter = null;

        inflator = (LayoutInflater) getSystemService("layout_inflater");
        addSourcePopupViewBuilder = new AddSourcePopupViewBuilder(TabActivity.this);
        deviceInfo = DeviceInfo.getInstance(this);
        final MultiScreenSupport multiScreenSupport = MultiScreenSupport.getInstance(deviceInfo);
        bottomHeight = multiScreenSupport.getBottomRadioHeight();
        tabHost = getTabHost();
        tabcontent = (FrameLayout) findViewById(android.R.id.tabcontent);
        tabcontent.setPadding(0, 0, 0, bottomHeight);
        tabHost.setup(this.getLocalActivityManager());
        final int bottomBarIconHeight = multiScreenSupport.getBottomBarIconHeight();

        addTab(R.string.mystream, R.layout.tab_stream, bottomBarIconHeight, new Intent(this, StreamActivity.class));
        addTab(R.string.my_feed, R.layout.tab_feeds, bottomBarIconHeight, new Intent(this, IndexActivity.class));
        addTab(R.string.config, R.layout.tab_setting, bottomBarIconHeight, new Intent(this, ConfigActivity.class));

        for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
            tabHost.getTabWidget().getChildAt(i).getLayoutParams().height = bottomHeight;
        }
    }


    private void addTab(int strId, int layout, int bottomBarIconHeight, Intent intent) {
        String text = this.getString(strId);
        final View view = inflator.inflate(layout, null);
        view.findViewById(R.id.tab_item_iv_icon).getLayoutParams().height = bottomBarIconHeight;
        final TabHost.TabSpec tabSpec = tabHost.newTabSpec(text)
                .setIndicator(view);
        tabSpec.setContent(intent);
        tabHost.addTab(tabSpec);
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


