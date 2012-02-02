package com.goal98.flipdroid.activity;

import android.app.ActivityGroup;
import android.app.TabActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.multiscreen.MultiScreenSupport;
import com.goal98.flipdroid.util.DeviceInfo;
import com.goal98.flipdroid.view.PopupWindowManager;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

/**
 * Created by IntelliJ IDEA.
 * User: janexie
 * Date: 12-1-26
 * Time: 下午4:21
 * To change this template use File | Settings | File Templates.
 */
public class StreamStyledActivity extends TabActivity implements TabHost.TabContentFactory, View.OnTouchListener {
    private ArticleAdapter adapter;
    //    private RadioButton[] mRadioButtons;
    private TabHost tabHost;
    private DeviceInfo deviceInfo;
    private int bottomHeight;
    private LayoutInflater inflator;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab);
        inflator = (LayoutInflater) getSystemService("layout_inflater");
        deviceInfo = DeviceInfo.getInstance(this);
        final MultiScreenSupport multiScreenSupport = MultiScreenSupport.getInstance(deviceInfo);
        bottomHeight = multiScreenSupport.getBottomRadioHeight();
        tabHost = getTabHost();
        FrameLayout tabcontent = (FrameLayout) findViewById(android.R.id.tabcontent);
        tabcontent.setPadding(0, 0, 0, bottomHeight);
        tabHost.setup(this.getLocalActivityManager());
        final int bottomBarIconHeight = multiScreenSupport.getBottomBarIconHeight();

        addTab(R.string.mystream, R.layout.tab_stream, bottomBarIconHeight, null);
        addTab(R.string.my_feed, R.layout.tab_feeds, bottomBarIconHeight, new Intent(this, IndexActivity.class));

        for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
            tabHost.getTabWidget().getChildAt(i).getLayoutParams().height = bottomHeight;
        }
        TabHost.OnTabChangeListener changeLis=new TabHost.OnTabChangeListener(){
            public void onTabChanged(String tabId) {
                PopupWindowManager.getInstance().dismissIfShowing();
            }
        };
        tabHost.setOnTabChangedListener(changeLis);
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
            View wrapper = inflator.inflate(R.layout.stream, null);
            final PullToRefreshListView mPullRefreshListView = (PullToRefreshListView) (wrapper.findViewById(R.id.pull_refresh_list));
            // Set a listener to be invoked when the list should be refreshed.
            mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener() {
                public void onRefresh() {
                    // Do work to refresh the list here.
                    new GetDataTask(mPullRefreshListView).execute();
                }
            });
            adapter = new ArticleAdapter(this, mPullRefreshListView.getAdapterView(), R.layout.lvloading, R.layout.stream_styled_article_view, new ArticleLoader(this, 5), R.layout.nodataview);

            adapter.forceLoad();
            return wrapper;
        }
        return null;
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_UP)
            PopupWindowManager.getInstance().dismissIfShowing();
        return true;
    }



    private class GetDataTask extends AsyncTask<Void, Void, String[]> {
        private PullToRefreshListView mPullRefreshListView;

        public GetDataTask(PullToRefreshListView mPullRefreshListView) {
            this.mPullRefreshListView = mPullRefreshListView;
        }

        @Override
        protected String[] doInBackground(Void... params) {
            // Simulates a background job.
            return null;
        }


        @Override
        protected void onPostExecute(String[] result) {
            // Call onRefreshComplete when the list has been refreshed.
            mPullRefreshListView.onRefreshComplete();

            super.onPostExecute(result);
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


