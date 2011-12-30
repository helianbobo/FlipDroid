package com.goal98.flipdroid.activity;

import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.db.AccountDB;
import com.goal98.flipdroid.db.RecommendSourceDB;
import com.goal98.flipdroid.db.SourceDB;
import com.goal98.flipdroid.model.Source;
import com.goal98.flipdroid.model.SourceUpdateManager;
import com.goal98.flipdroid.model.cachesystem.CacheToken;
import com.goal98.flipdroid.model.cachesystem.CachedArticleSource;
import com.goal98.flipdroid.model.cachesystem.SourceCache;
import com.goal98.flipdroid.model.cachesystem.SourceUpdateable;
import com.goal98.flipdroid.util.*;
import com.goal98.tika.common.TikaConstants;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.mobclick.android.MobclickAgent;
import com.mobclick.android.UmengUpdateListener;

import java.util.*;

public class IndexActivity extends ListActivity implements SourceUpdateable {

    static final private int CONFIG_ID = Menu.FIRST;
    static final private int CLEAR_ID = Menu.FIRST + 1;
    static final private int ACCOUNT_LIST_ID = Menu.FIRST + 2;
    static final private int TIPS_ID = Menu.FIRST + 3;

    private AccountDB accountDB;
    private SourceDB sourceDB;
    private Cursor sourceCursor;
    private DeviceInfo deviceInfo;
    //    private SourceCache sourceCache;
    private BaseAdapter adapter;
    private boolean updated;
    private PullToRefreshListView mPullRefreshListView;

    final private Map indicatorMap = new HashMap();

    private String TAG = this.getClass().getName();

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }


    public DeviceInfo getDeviceInfoFromApplicationContext() {
        return DeviceInfo.getInstance(this);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (NetworkUtil.isNetworkAvailable(this)) {
            MobclickAgent.update(this);
            MobclickAgent.updateAutoPopup = false;
            MobclickAgent.setUpdateListener(new UmengUpdateListener() {
                public void onUpdateReturned(int status) {
                    switch (status) {
                        case 0: //has update
                            MobclickAgent.showUpdateDialog(IndexActivity.this);
                            Log.i(TAG, "show dialog");
                            break;
                        case 1: //has no update
                            Toast.makeText(getParent(), "has no update", Toast.LENGTH_SHORT).show();
                            break;
                        case 2: //none wifi
                            Toast.makeText(getParent(), "has no update", Toast.LENGTH_SHORT).show();
                            break;
                        case 3: //time out
                            Toast.makeText(getParent(), "time out", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            });
        }

        deviceInfo = getDeviceInfoFromApplicationContext();

        sourceDB = new SourceDB(getApplicationContext());

        setContentView(R.layout.index);

        Button addSourceButton = (Button) findViewById(R.id.btn_add_source);
//        addSourceButton.setVisibility(View.GONE);
        addSourceButton.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_UP:
                        Intent intent = new Intent(IndexActivity.this, SiteActivity.class);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.slide_in_left, R.anim.fade);
                        break;
                    default:
                        break;
                }

                return false;
            }

        });
        mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);

        // Set a listener to be invoked when the list should be refreshed.
        mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener() {
            public void onRefresh() {
                // Do work to refresh the list here.
                new GetDataTask().execute();
            }
        });
        this.getListView().setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
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
        SourceUpdateManager updateManager = new SourceUpdateManager(sourceDB, SourceCache.getInstance(IndexActivity.this), IndexActivity.this, RecommendSourceDB.getInstance(IndexActivity.this));
        updateManager.updateSourceList();

    }

    private void updateSource() {
        SourceUpdateManager updateManager = new SourceUpdateManager(sourceDB, SourceCache.getInstance(IndexActivity.this), IndexActivity.this, RecommendSourceDB.getInstance(IndexActivity.this));
        updateManager.updateAll();
    }

    private class GetDataTask extends AsyncTask<Void, Void, String[]> {

        @Override
        protected String[] doInBackground(Void... params) {
            // Simulates a background job.
            updateSource();
            return null;
        }


        @Override
        protected void onPostExecute(String[] result) {
            // Call onRefreshComplete when the list has been refreshed.
            mPullRefreshListView.onRefreshComplete();

            super.onPostExecute(result);
        }
    }

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
            SourceCache sourceCache = SourceCache.getInstance(this);
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
            adapter = new SourceItemArrayAdapter<SourceItem>(this, R.layout.source_item, sourceDB, deviceInfo);
        }

        setListAdapter(adapter);

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
        openDatabase();
        bindAdapter();

        adapter.notifyDataSetChanged();

        final Cursor c = sourceDB.findAll();
        hander.post(new Runnable() {
            public void run() {
                ManagedCursor mc = new ManagedCursor(c);
                mc.each(new EachCursor() {
                    public void call(Cursor cursor, int index) {
                        if (!(adapter.getItem(index) instanceof SourceItem))
                            return;
                        SourceItem item = (SourceItem) adapter.getItem(index);
                        if (indicatorMap.get(item.getSourceType() + "_" + item.getSourceURL()) != null) {
                            View childAt = IndexActivity.this.getListView().getChildAt(index);
                            if (childAt != null) {
                                childAt.findViewById(R.id.loadingbar).setVisibility(View.GONE);
                                TextView indicator = (TextView) childAt.findViewById(R.id.indicator);
                                indicator.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });
            }
        });

        boolean shallUpdate = NetworkUtil.toUpdateSource(this);
        if (shallUpdate) {
            new Thread(new Runnable() {

                public void run() {
                    if (!updated) {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {

                        }
                        updateSource();
                        updated = true;
                    }
                }
            }).start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        MobclickAgent.onPause(this);
        closeDB();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent = new Intent(this, PageActivity.class);
        if (l.getItemAtPosition(position) instanceof Map) {
            return;
        }

        SourceItem item = (SourceItem) l.getItemAtPosition(position);

        indicatorMap.remove(item.getSourceType() + "_" + item.getSourceURL());

        intent.putExtra("type", item.getSourceType());
        intent.putExtra("sourceId", item.getSourceId());
        intent.putExtra("sourceImage", item.getSourceImage());
        intent.putExtra("sourceName", item.getSourceName());
        intent.putExtra("contentUrl", item.getSourceURL());//for rss
        startActivity(intent);
        overridePendingTransition(android.R.anim.slide_in_left, R.anim.fade);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(0, CONFIG_ID, 0, R.string.config);
        menu.add(0, CLEAR_ID, 0, R.string.clear_all_account);
        menu.add(0, ACCOUNT_LIST_ID, 0, R.string.accounts);
        menu.add(0, TIPS_ID, 0, R.string.tips);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case CONFIG_ID:
                startActivity(new Intent(this, ConfigActivity.class));
                return true;
            case CLEAR_ID:
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                preferences.edit().putString(WeiPaiWebViewClient.SINA_ACCOUNT_PREF_KEY, null).commit();

                int count = accountDB.deleteAll();
                Log.e(this.getClass().getName(), count + " accounts are deleted.");

                count = sourceDB.deleteAll();
                Log.e(this.getClass().getName(), count + " sources are deleted.");


                adapter = new SourceItemArrayAdapter<SourceItem>(this, R.layout.source_item, sourceDB, deviceInfo);
                bindAdapter();
                adapter.notifyDataSetChanged();
                return true;
            case ACCOUNT_LIST_ID:
                startActivity(new Intent(this, AccountListActivity.class));
                return true;
            case TIPS_ID:
                startActivity(new Intent(this, TipsActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void notifyUpdating(final CachedArticleSource cachedArticleSource) {
        final CacheToken token = cachedArticleSource.getToken();
        hander.post(new Runnable() {
            public void run() {
                for (int i = 0; i < adapter.getCount(); i++) {
                    SourceItem item = (SourceItem) adapter.getItem(i);
                    if (token.match(item)) {
                        if (item.getSourceItemView() != null) {
                            item.getSourceItemView().findViewById(R.id.loadingbar).setVisibility(View.VISIBLE);
                            item.getSourceItemView().findViewById(R.id.indicator).setVisibility(View.GONE);
                        }
                    }
                }
            }
        }
        );
    }

    public void notifyHasNew
            (final CachedArticleSource
                     cachedArticleSource) {
        final CacheToken token = cachedArticleSource.getToken();

        final Cursor c = sourceDB.findAll();
        hander.post(new Runnable() {
            public void run() {
                ManagedCursor mc = new ManagedCursor(c);
                mc.each(new EachCursor() {
                    public void call(Cursor cursor, int index) {
                        if (!(adapter.getItem(index) instanceof SourceItem))
                            return;
                        SourceItem item = (SourceItem) adapter.getItem(index);
                        if (token.match(item)) {
                            View childAt = IndexActivity.this.getListView().getChildAt(index);
                            if (childAt != null) {
                                childAt.findViewById(R.id.loadingbar).setVisibility(View.GONE);
                                TextView indicator = (TextView) childAt.findViewById(R.id.indicator);
                                indicator.setVisibility(View.VISIBLE);
                                indicatorMap.put(token.getType() + "_" + token.getToken(), new Object());
                            }
                        }
                    }
                });
            }
        });
    }

    Handler hander = new Handler();

    public void notifyNoNew
            (final CachedArticleSource
                     cachedArticleSource) {
        final CacheToken token = cachedArticleSource.getToken();

        final Cursor c = sourceDB.findAll();
        hander.post(new Runnable() {
            public void run() {
                ManagedCursor mc = new ManagedCursor(c);
                mc.each(new EachCursor() {
                    public void call(Cursor cursor, int index) {
                        if (!(adapter.getItem(index) instanceof SourceItem))
                            return;
                        SourceItem item = (SourceItem) adapter.getItem(index);
                        if (token.match(item)) {
                            View childAt = IndexActivity.this.getListView().getChildAt(index);
                            if (childAt != null) {
                                childAt.findViewById(R.id.loadingbar).setVisibility(View.GONE);
                                if (indicatorMap.get(item.getSourceType() + "_" + item.getSourceURL()) != null) {
                                    TextView indicator = (TextView) childAt.findViewById(R.id.indicator);
                                    indicator.setVisibility(View.VISIBLE);
                                }

                            }
                        }
                    }
                });
            }
        });
    }

    public void notifyUpdateDone(CachedArticleSource cachedArticleSource) {
        ContentValues values = new ContentValues();
        values.put(Source.KEY_UPDATE_TIME, new Date().getTime());
        sourceDB.update(values, Source.KEY_SOURCE_TYPE + " = ? and " + Source.KEY_CONTENT_URL + " = ?", new String[]{cachedArticleSource.getToken().getType(), cachedArticleSource.getToken().getToken()});
    }
}