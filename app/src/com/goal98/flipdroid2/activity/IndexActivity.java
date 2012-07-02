package com.goal98.flipdroid2.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.actionbarsherlock.app.SherlockActivity;
import com.goal98.flipdroid2.R;
import com.goal98.flipdroid2.db.AccountDB;
import com.goal98.flipdroid2.db.RSSURLDB;
import com.goal98.flipdroid2.db.SourceContentDB;
import com.goal98.flipdroid2.db.SourceDB;
import com.goal98.flipdroid2.model.cachesystem.SourceCache;
import com.goal98.flipdroid2.util.NetworkUtil;
import com.goal98.tika.common.TikaConstants;
import com.mobclick.android.MobclickAgent;
import com.mobclick.android.UmengUpdateListener;
import com.srz.androidtools.util.DeviceInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IndexActivity extends SherlockActivity{

    static final private int CONFIG_ID = Menu.FIRST;
    static final private int CLEAR_ID = Menu.FIRST + 1;
    static final private int ACCOUNT_LIST_ID = Menu.FIRST + 2;
    static final private int TIPS_ID = Menu.FIRST + 3;
    private View addSourcePopup;
    private PopupWindow mPopupWindow;
    private AddSourcePopupViewBuilder addSourcePopupViewBuilder;

    private AccountDB accountDB;
    private SourceDB sourceDB;
    private RSSURLDB rssurlDB;
    private Cursor sourceCursor;
    private DeviceInfo deviceInfo;
    //    private SourceCache sourceCache;
    private BaseAdapter adapter;
    private boolean updated;
//    private PullToRefreshListView mPullRefreshListView;

    final private Map indicatorMap = new HashMap();

    private String TAG = this.getClass().getName();
    private GridView gridview;


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }


    public DeviceInfo getDeviceInfoFromApplicationContext() {
        return DeviceInfo.getInstance(this);
    }


    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Sherlock);
        super.onCreate(savedInstanceState);

        if (NetworkUtil.isNetworkAvailable(this)) {
            MobclickAgent.update(this);
            MobclickAgent.updateAutoPopup = false;
            MobclickAgent.setUpdateListener(new UmengUpdateListener() {
                public void onUpdateReturned(int status) {
                    Activity parent = getParent();
                    if (parent == null)
                        return;

                    switch (status) {
                        case 0: //has update
                            MobclickAgent.showUpdateDialog(IndexActivity.this);
                            Log.i(TAG, "show dialog");
                            break;
//                        case 1: //has no update
//                            Toast.makeText(parent, "has no update", Toast.LENGTH_SHORT).show();
//                            break;
//                        case 2: //none wifi
//                            Toast.makeText(parent, "has no update", Toast.LENGTH_SHORT).show();
//                            break;
//                        case 3: //time out
//                            Toast.makeText(parent, "time out", Toast.LENGTH_SHORT).show();
//                            break;
                    }
                }
            });
        }

        deviceInfo = getDeviceInfoFromApplicationContext();

        sourceDB = new SourceDB(this);
        rssurlDB = new RSSURLDB(this);
        setContentView(R.layout.index);
         gridview = (GridView) findViewById(R.id.gridview);
        addSourcePopupViewBuilder = new AddSourcePopupViewBuilder(IndexActivity.this);
        addSourcePopup = addSourcePopupViewBuilder.buildAddSourcePopupView(IndexActivity.this);

//        mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
//
//        // Set a listener to be invoked when the list should be refreshed.
//        mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener() {
//            public void onRefresh() {
//                // Do work to refresh the list here.
//                new GetDataTask().execute();
//            }
//        });
        gridview.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            public void onCreateContextMenu(ContextMenu menu, View v,
                                            ContextMenu.ContextMenuInfo menuInfo) {
                if (v.findViewById(R.id.text) != null)
                    return;

                menu.setHeaderTitle(R.string.deletesource);
                menu.setHeaderIcon(R.drawable.btndelete);
                menu.add(0, 0, 0, R.string.yes);
                menu.add(0, 1, 0, R.string.no);
            }
        });

        openDatabase();
        bindAdapter();

        adapter.notifyDataSetChanged();

//        final Cursor c = sourceDB.findAll();
//        hander.post(new Runnable() {
//            public void run() {
//                ManagedCursor mc = new ManagedCursor(c);
//                mc.each(new EachCursor() {
//                    public void call(Cursor cursor, int index) {
//                        if (!(adapter.getItem(index) instanceof SourceItem))
//                            return;
//                        SourceItem item = (SourceItem) adapter.getItem(index);
//                        if (indicatorMap.get(item.getSourceType() + "_" + item.getSourceURL()) != null) {
//                            View childAt = IndexActivity.this.getListView().getChildAt(index);
//                            if (childAt != null) {
//                                childAt.findViewById(R.id.loadingbar).setVisibility(View.GONE);
//                                TextView indicator = (TextView) childAt.findViewById(R.id.indicator);
//                                indicator.setVisibility(View.VISIBLE);
//                            }
//                        }
//                    }
//                });
//            }
//        });
    }


//    private void updateSource() {
//        SourceUpdateManager updateManager = new SourceUpdateManager(rssurlDB, sourceDB,new SourceCache(new SourceContentDB(IndexActivity.this)), IndexActivity.this, RecommendSourceDB.getInstance(IndexActivity.this));
//        updateManager.updateAll(false);
//    }




//    private class GetDataTask extends AsyncTask<Void, Void, String[]> {
//
//        @Override
//        protected String[] doInBackground(Void... params) {
//            // Simulates a background job.
//            updateSource();
//            return null;
//        }
//
//
//        @Override
//        protected void onPostExecute(String[] result) {
//            // Call onRefreshComplete when the list has been refreshed.
////            mPullRefreshListView.onRefreshComplete();
//
//            super.onPostExecute(result);
//        }
//    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        TextView sourceNameTextView = (TextView) info.targetView
                .findViewById(R.id.source_name);
        TextView sourceTypeTextView = (TextView) info.targetView
                .findViewById(R.id.source_type);
        TextView sourceUrlTextView = (TextView) info.targetView
                .findViewById(R.id.source_url);
        String sourceUrl = sourceUrlTextView.getText().toString();
        String sourceName = sourceNameTextView.getText().toString();
        String sourceType = sourceTypeTextView.getText().toString();
        if (item.getItemId() == 0) {//delete
            sourceDB.removeSourceByName(sourceName);
            rssurlDB.deleteFromFrom(sourceUrl);
            SourceCache sourceCache = new SourceCache(new SourceContentDB(this));
            try {
                sourceCache.clear(sourceType, sourceUrl);
            } finally {
                sourceCache.close();
            }
            if (sourceType.equals(TikaConstants.TYPE_MY_SINA_WEIBO)) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                String sinaAccountId = preferences.getString(WeiPaiWebViewClient.SINA_ACCOUNT_PREF_KEY, null);
                preferences.edit().putString(WeiPaiWebViewClient.SINA_ACCOUNT_PREF_KEY, null).commit();
                preferences.edit().putString(WeiPaiWebViewClient.PREVIOUS_SINA_ACCOUNT_PREF_KEY, sinaAccountId).commit();
            }

            bindAdapter();
            adapter.notifyDataSetChanged();
            return true;
        }
        return super.onContextItemSelected(item);
    }

    private void bindAdapter() {
        if (deviceInfo == null)
            deviceInfo = getDeviceInfoFromApplicationContext();

        sourceCursor = sourceDB.findAll();
        if (sourceCursor.getCount() == 0) {
            sourceCursor.close();
            Map<String, String> noData = new HashMap<String, String>();
            noData.put("text", getString(R.string.nodata));
            List emptyBlock = new ArrayList();
            emptyBlock.add(noData);
            adapter = new SimpleAdapter(this, emptyBlock, R.layout.nodataitem,
                    new String[]{"text"},
                    new int[]{R.id.text});
        } else {
            adapter = new SourceGridArrayAdapter<SourceItem>(this, R.layout.source_grid, sourceDB, deviceInfo,rssurlDB );
        }
        gridview.setAdapter(adapter);
//        setListAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void openDatabase() {
        sourceDB = new SourceDB(getApplicationContext());
        accountDB = new AccountDB(this);
    }

    private void closeDB() {
        if (sourceCursor != null)
            sourceCursor.close();
        if (accountDB != null)
            accountDB.close();
        if (sourceDB != null)
            sourceDB.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);


//        boolean shallUpdate = NetworkUtil.toUpdateSource(this);
//        if (shallUpdate) {
//            new Thread(new Runnable() {
//
//                public void run() {
//                    if (!updated) {
//                        try {
//                            Thread.sleep(2000);
//                        } catch (InterruptedException e) {
//
//                        }
//                        updateSource();
//                        updated = true;
//                    }
//                }
//            }).start();
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        closeDB();
    }

//    @Override
//    protected void onListItemClick(ListView l, View v, int position, long id) {
//        Intent intent;
//        if (l.getItemAtPosition(position) instanceof Map) {
//            return;
//        }
//
//        SourceItem item = (SourceItem) l.getItemAtPosition(position);
//
//        if (TikaConstants.TYPE_MY_SINA_WEIBO.endsWith(item.getSourceType()) && !SinaAccountUtil.alreadyBinded(this)) {
//
//            intent = new Intent(this, SinaAccountActivity.class);
//            intent.putExtra("PROMPTTEXT", this.getString(R.string.gotosinaoauth));
//
//        } else {
//
//            intent = new Intent(this, PageActivity.class);
//            indicatorMap.remove(item.getSourceType() + "_" + item.getSourceURL());
//
//            intent.putExtra("type", item.getSourceType());
//            intent.putExtra("sourceId", item.getSourceId());
//            intent.putExtra("sourceImage", item.getSourceImage());
//            intent.putExtra("sourceName", item.getSourceName());
//            intent.putExtra("contentUrl", item.getSourceURL());//for rss
//
//        }
//
//
//        startActivity(intent);
//        overridePendingTransition(android.R.anim.slide_in_left, R.anim.fade);
//    }

//    public void notifyUpdating(final CachedArticleSource cachedArticleSource) {
//        final CacheToken token = cachedArticleSource.getToken();
//        hander.post(new Runnable() {
//            public void run() {
//                for (int i = 0; i < adapter.getCount(); i++) {
//                    SourceItem item = (SourceItem) adapter.getItem(i);
//                    if (token.match(item)) {
//                        if (item.getSourceItemView() != null) {
//                            item.getSourceItemView().findViewById(R.id.loadingbar).setVisibility(View.VISIBLE);
//                            item.getSourceItemView().findViewById(R.id.indicator).setVisibility(View.GONE);
//                        }
//                    }
//                }
//            }
//        }
//        );
//    }

//    public void notifyHasNew
//            (final CachedArticleSource
//                     cachedArticleSource) {
//        final CacheToken token = cachedArticleSource.getToken();
//
//        final Cursor c = sourceDB.findAll();
//        hander.post(new Runnable() {
//            public void run() {
//                ManagedCursor mc = new ManagedCursor(c);
//                mc.each(new EachCursor() {
//                    public void call(Cursor cursor, int index) {
//                        if (adapter.getCount() <= index)
//                            return;
//                        if (!(adapter.getItem(index) instanceof SourceItem))
//                            return;
//                        SourceItem item = (SourceItem) adapter.getItem(index);
//                        if (token.match(item)) {
//                            View childAt = IndexActivity.this.getListView().getChildAt(index);
//                            if (childAt != null) {
//                                childAt.findViewById(R.id.loadingbar).setVisibility(View.GONE);
//                                TextView indicator = (TextView) childAt.findViewById(R.id.indicator);
//                                indicator.setVisibility(View.VISIBLE);
//                                indicatorMap.put(token.getType() + "_" + token.getToken(), new Object());
//                            }
//                        }
//                    }
//                });
//            }
//        });
//    }
//
//    Handler hander = new Handler();
//
//    public void notifyNoNew
//            (final CachedArticleSource
//                     cachedArticleSource) {
//        final CacheToken token = cachedArticleSource.getToken();
//
//        final Cursor c = sourceDB.findAll();
//        hander.post(new Runnable() {
//            public void run() {
//                ManagedCursor mc = new ManagedCursor(c);
//                mc.each(new EachCursor() {
//                    public void call(Cursor cursor, int index) {
//                        if (adapter.getCount() <= index)
//                            return;
//                        if (!(adapter.getItem(index) instanceof SourceItem))
//                            return;
//                        SourceItem item = (SourceItem) adapter.getItem(index);
//                        if (token.match(item)) {
//                            View childAt = IndexActivity.this.getListView().getChildAt(index);
//                            if (childAt != null) {
//                                childAt.findViewById(R.id.loadingbar).setVisibility(View.GONE);
//                                if (indicatorMap.get(item.getSourceType() + "_" + item.getSourceURL()) != null) {
//                                    TextView indicator = (TextView) childAt.findViewById(R.id.indicator);
//                                    indicator.setVisibility(View.VISIBLE);
//                                }
//
//                            }
//                        }
//                    }
//                });
//            }
//        });
//    }
//
//    public void notifyUpdateDone(CachedArticleSource cachedArticleSource) {
//        ContentValues values = new ContentValues();
//        values.put(Source.KEY_UPDATE_TIME, new Date().getTime());
//        sourceDB.update(values, Source.KEY_SOURCE_TYPE + " = ? and " + Source.KEY_CONTENT_URL + " = ?", new String[]{cachedArticleSource.getToken().getType(), cachedArticleSource.getToken().getToken()});
//    }
}