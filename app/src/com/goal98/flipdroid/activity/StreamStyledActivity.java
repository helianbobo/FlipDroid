package com.goal98.flipdroid.activity;

import android.app.TabActivity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.*;
import android.widget.*;
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.db.RSSURLDB;
import com.goal98.flipdroid.db.RecommendSourceDB;
import com.goal98.flipdroid.db.SourceDB;
import com.goal98.flipdroid.model.SourceUpdateManager;
import com.goal98.flipdroid.model.cachesystem.CachedArticleSource;
import com.goal98.flipdroid.model.cachesystem.SourceCache;
import com.goal98.flipdroid.model.cachesystem.SourceUpdateable;
import com.goal98.flipdroid.multiscreen.MultiScreenSupport;
import com.goal98.flipdroid.util.AlarmSender;
import com.goal98.flipdroid.util.DeviceInfo;
import com.goal98.flipdroid.view.PopupWindowManager;
import com.goal98.flipdroid.view.TopBar;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

/**
 * Created by IntelliJ IDEA.
 * User: janexie
 * Date: 12-1-26
 * Time: 下午4:21
 * To change this template use File | Settings | File Templates.
 */
public class StreamStyledActivity extends TabActivity implements TabHost.TabContentFactory, SourceUpdateable {
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

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab);

        HostSetter hostSetter = new HostSetter(this);
        hostSetter.setHost();
        hostSetter = null;

        inflator = (LayoutInflater) getSystemService("layout_inflater");
        addSourcePopupViewBuilder = new AddSourcePopupViewBuilder(StreamStyledActivity.this);
        deviceInfo = DeviceInfo.getInstance(this);
        final MultiScreenSupport multiScreenSupport = MultiScreenSupport.getInstance(deviceInfo);
        bottomHeight = multiScreenSupport.getBottomRadioHeight();
        tabHost = getTabHost();
        tabcontent = (FrameLayout) findViewById(android.R.id.tabcontent);
        tabcontent.setPadding(0, 0, 0, bottomHeight);
        tabHost.setup(this.getLocalActivityManager());
        final int bottomBarIconHeight = multiScreenSupport.getBottomBarIconHeight();

        addTab(R.string.mystream, R.layout.tab_stream, bottomBarIconHeight, null);
        addTab(R.string.my_feed, R.layout.tab_feeds, bottomBarIconHeight, new Intent(this, IndexActivity.class));
        addTab(R.string.config, R.layout.tab_setting, bottomBarIconHeight, new Intent(this, ConfigActivity.class));

        for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
            tabHost.getTabWidget().getChildAt(i).getLayoutParams().height = bottomHeight;
        }


//        tabHost.setOnTabChangedListener(changeLis);

    }


    private void addTab(int strId, int layout, int bottomBarIconHeight, Intent intent) {
        String text = this.getString(strId);
        final View view = inflator.inflate(layout, null);
        view.findViewById(R.id.tab_item_iv_icon).getLayoutParams().height = bottomBarIconHeight;
        final TabHost.TabSpec tabSpec = tabHost.newTabSpec(text)
                .setIndicator(view);
        if (intent != null) {
            tabSpec.setContent(intent);
        } else
            tabSpec.setContent(this);
        tabHost.addTab(tabSpec);
    }


    public View createTabContent(String s) {
        if (s.equals(this.getString(R.string.mystream))) {
            final View wrapper = inflator.inflate(R.layout.stream, null);
            mPullRefreshListView = (PullToRefreshListView) (wrapper.findViewById(R.id.pull_refresh_list));
            // Set a listener to be invoked when the list should be refreshed.
            mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener() {
                public void onRefresh() {
                    // Do work to refresh the list here.
                    new GetDataTask(mPullRefreshListView).execute();
                }
            });
            final TopBar topbar = (TopBar) wrapper.findViewById(R.id.topbar);
            topbar.addButton(TopBar.IMAGE, R.drawable.refresh_black_48, new LinearLayout.OnClickListener() {
                public synchronized void onClick(View view) {
                    mPullRefreshListView.setRefreshing();
                }
            });
            adapter = new ArticleAdapter(this, mPullRefreshListView.getAdapterView(), R.layout.lvloading, R.layout.stream_styled_article_view, articleLoader, R.layout.add_more_source_view, new View.OnClickListener() {
                public void onClick(View view) {
                    int[] location = new int[2];
                    view.getLocationOnScreen(location);

                    View addSourcePopup = addSourcePopupViewBuilder.buildAddSourcePopupView(StreamStyledActivity.this);
                    if (mPopupWindow != null && mPopupWindow.isShowing()) {
                        mPopupWindow.dismiss();
                        return;
                    }

                    mPopupWindow = new PopupWindow(addSourcePopup, ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
                    mPopupWindow.setOutsideTouchable(true);
                    if(location[1]>deviceInfo.getHeight()/2){
                       mPopupWindow.showAsDropDown(view, 0, -view.getHeight());
                    }else{
                        mPopupWindow.showAsDropDown(view, 0, 0);
                    }

                    PopupWindowManager.getInstance().setWindow(mPopupWindow);
                }
            });

            adapter.forceLoad(true);
            return wrapper;
        }
        return null;
    }



    public void notifyUpdating(CachedArticleSource cachedArticleSource) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void notifyHasNew(CachedArticleSource cachedArticleSource) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void notifyNoNew(CachedArticleSource cachedArticleSource) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void notifyUpdateDone(CachedArticleSource cachedArticleSource) {
        //To change body of implemented methods use File | Settings | File Templates.
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

    private class GetDataTask extends AsyncTask<Void, Void, String[]> {
        private PullToRefreshListView mPullRefreshListView;
        private RSSURLDB rssurlDB;

        public GetDataTask(PullToRefreshListView mPullRefreshListView) {
            this.mPullRefreshListView = mPullRefreshListView;

        }
        
        int countBeforeUpdate = 0;

        @Override
        protected String[] doInBackground(Void... params) {
            adapter.reset();
            articleLoader.reset();
            SourceDB sourceDB = new SourceDB(getApplicationContext());
            rssurlDB = new RSSURLDB(getApplicationContext());
            countBeforeUpdate = rssurlDB.getCount();
            try{
                SourceUpdateManager updateManager = new SourceUpdateManager(rssurlDB, sourceDB, new SourceCache(StreamStyledActivity.this), StreamStyledActivity.this, RecommendSourceDB.getInstance(StreamStyledActivity.this));
                updateManager.updateContent(true);
            }finally {
                sourceDB.close();
                rssurlDB.close();
            }
//            mPullRefreshListView.
            adapter.forceLoad(false);
            return null;
        }


        @Override
        protected void onPostExecute(String[] result) {
            // Call onRefreshComplete when the list has been refreshed.
            int countAfterUpdate = rssurlDB.getCount();
            final int updatedCount = countAfterUpdate-countBeforeUpdate;

            mPullRefreshListView.onRefreshComplete();

            super.onPostExecute(result);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    String updated = StreamStyledActivity.this.getString(R.string.updated);
                    updated = updated.replaceAll("%", "" + updatedCount);
                    new AlarmSender(StreamStyledActivity.this.getApplicationContext()).sendInstantMessage(updated);
                }
            });
        }
    }

//    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//        if (isChecked) {
//            if (buttonView == mRadioButtons[0]) {
//                tabHost.setCurrentTabByTag(this.getString(R.string.mystream));
//            } else if (buttonView == mRadioButtons[1]) {
//                tabHost.setCurrentTabByTag(this.getString(R.string.my_feed));
//            }else if (buttonView == mRadioButtons[2]) {
//                tabHost.setCurrentTabByTag(this.getString(R.string.addfeeds));
//            }
//            for (int i = 0; i < mRadioButtons.length; i++) {
//            RadioButton mRadioButton = mRadioButtons[i];
//            if(buttonView== mRadioButton)
//                mRadioButton.setSelected(true);
//            else
//                mRadioButton.setSelected(false);
//        }
//        }

//    }

}


