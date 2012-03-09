package com.goal98.flipdroid.activity;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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

/**
 * Created by IntelliJ IDEA.
 * User: jleo
 * Date: 12-3-9
 * Time: 下午3:28
 * To change this template use File | Settings | File Templates.
 */
public class StreamActivity extends Activity implements SourceUpdateable {
    private PullToRefreshListView mPullRefreshListView;
    private Handler handler = new Handler();
    private ArticleAdapter adapter;
    private AddSourcePopupViewBuilder addSourcePopupViewBuilder;
    private PopupWindow mPopupWindow;
    private DeviceInfo deviceInfo;
    private final ArticleLoader articleLoader = new ArticleLoader(this, 20);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.
        setContentView(R.layout.stream_single);
        deviceInfo = DeviceInfo.getInstance(this);
        mPullRefreshListView = (PullToRefreshListView) (findViewById(R.id.pull_refresh_list));
        // Set a listener to be invoked when the list should be refreshed.
        mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener() {
            public void onRefresh() {
                // Do work to refresh the list here.
                new GetDataTask(mPullRefreshListView).execute();
            }
        });
        addSourcePopupViewBuilder = new AddSourcePopupViewBuilder(StreamActivity.this);
        adapter = new ArticleAdapter(this, mPullRefreshListView.getAdapterView(), R.layout.lvloading, R.layout.stream_styled_article_view, articleLoader, R.layout.add_more_source_view, new View.OnClickListener() {
            public void onClick(View view) {
                int[] location = new int[2];
                view.getLocationOnScreen(location);

                View addSourcePopup = addSourcePopupViewBuilder.buildAddSourcePopupView(StreamActivity.this);
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
                SourceUpdateManager updateManager = new SourceUpdateManager(rssurlDB, sourceDB, new SourceCache(new SourceContentDB(StreamActivity.this)), StreamActivity.this, RecommendSourceDB.getInstance(StreamActivity.this));
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
                    String updated = StreamActivity.this.getString(R.string.updated);
                    updated = updated.replaceAll("%", "" + updatedCount);
                    new AlarmSender(StreamActivity.this.getApplicationContext()).sendInstantMessage(updated);
                }
            });
        }
    }
}
