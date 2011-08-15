package com.goal98.flipdroid.activity;

import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.db.AccountDB;
import com.goal98.flipdroid.db.SourceDB;
import com.goal98.flipdroid.model.Account;
import com.goal98.flipdroid.model.Source;
import com.goal98.flipdroid.model.SourceUpdateManager;
import com.goal98.flipdroid.model.cachesystem.CacheToken;
import com.goal98.flipdroid.model.cachesystem.CachedArticleSource;
import com.goal98.flipdroid.model.cachesystem.SourceCache;
import com.goal98.flipdroid.model.cachesystem.SourceUpdateable;
import com.goal98.flipdroid.model.rss.RSSArticleSource;
import com.goal98.flipdroid.util.Constants;
import com.goal98.flipdroid.util.DeviceInfo;
import com.goal98.flipdroid.util.EachCursor;
import com.goal98.flipdroid.util.ManagedCursor;
import com.goal98.flipdroid.view.SourceItemViewBinder;

import java.util.*;

public class IndexActivity extends ListActivity implements SourceUpdateable {

    static final private int CONFIG_ID = Menu.FIRST;
    static final private int CLEAR_ID = Menu.FIRST + 1;
    static final private int ACCOUNT_LIST_ID = Menu.FIRST + 2;

    private AccountDB accountDB;
    private SourceDB sourceDB;
    private Cursor sourceCursor;
    private DeviceInfo deviceInfo;
    //    private SourceCache sourceCache;
    private BaseAdapter adapter;
    private boolean updated;


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }


    public DeviceInfo getDeviceInfoFromApplicationContext() {
        return DeviceInfo.getInstance(this);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceInfo = getDeviceInfoFromApplicationContext();

        sourceDB = new SourceDB(getApplicationContext());

        setContentView(R.layout.index);

        Button addSourceButton = (Button) findViewById(R.id.btn_add_source);
        addSourceButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(IndexActivity.this, SiteActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left, R.anim.fade);
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
            SourceCache sourceCache = new SourceCache(this);
            try {
                sourceCache.clear(sourceType, sourceUrl);
            } finally {
                sourceCache.close();
            }
            if (sourceType.equals(Constants.TYPE_MY_SINA_WEIBO)) {
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
//            adapter = new SimpleCursorAdapter(this, R.layout.source_item, sourceCursor,
//                    new String[]{Source.KEY_SOURCE_NAME, Source.KEY_SOURCE_DESC, Source.KEY_IMAGE_URL, Source.KEY_SOURCE_TYPE, Source.KEY_CONTENT_URL},
//                    new int[]{R.id.source_name, R.id.source_desc, R.id.source_image, R.id.source_type, R.id.source_url});
//            ((SimpleCursorAdapter) adapter).setViewBinder(new SourceItemViewBinder(deviceInfo));
            buildAdapter();
        }

        setListAdapter(adapter);

    }

    private void buildAdapter() {
        sourceCursor = sourceDB.findAll();
        final List<SourceItem> items = new ArrayList<SourceItem>();
        new ManagedCursor(sourceCursor).each(new EachCursor() {
            public void call(Cursor cursor, int index) {
                String sourceType = cursor.getString(cursor.getColumnIndex(Source.KEY_SOURCE_TYPE));
                String sourceContentUrl = cursor.getString(cursor.getColumnIndex(Source.KEY_CONTENT_URL));
                String sourceName = cursor.getString(cursor.getColumnIndex(Source.KEY_SOURCE_NAME));
                String sourceImage = cursor.getString(cursor.getColumnIndex(Source.KEY_IMAGE_URL));
                String sourceDesc = cursor.getString(cursor.getColumnIndex(Source.KEY_SOURCE_DESC));
                String sourceID = cursor.getString(cursor.getColumnIndex(Source.KEY_SOURCE_ID));
                long sourceUpdateTime = cursor.getLong(cursor.getColumnIndex(Source.KEY_UPDATE_TIME));

                SourceItem item = new SourceItem();
                item.setSourceType(sourceType);
                item.setSourceName(sourceName);
                item.setSourceImage(sourceImage);
                item.setSourceURL(sourceContentUrl);
                item.setSourceDesc(sourceDesc);
                item.setSourceId(sourceID);
                final Date date = new Date();
                date.setTime(sourceUpdateTime);
                item.setSourceUpdateTime(date);

                items.add(item);
            }
        });

        adapter = new SourceItemArrayAdapter<SourceItem>(this, R.layout.source_item, items, deviceInfo);
    }

    @Override
    protected void onStart() {
        super.onStart();

//        startManagingCursor(sourceCursor);


    }

    private void openDatabase() {
        sourceDB = new SourceDB(getApplicationContext());
        accountDB = new AccountDB(this);
//        sourceCursor = sourceDB.findAll();
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
        openDatabase();
        bindAdapter();

        adapter.notifyDataSetChanged();
        new Thread(new Runnable() {

            public void run() {
                if (!updated) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {

                    }
                    SourceUpdateManager updateManager = new SourceUpdateManager(IndexActivity.this, adapter);
                    updateManager.updateAll();
                    updated = true;
                }
            }
        }).start();

    }

    @Override
    protected void onPause() {
        super.onPause();
        closeDB();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent = new Intent(this, PageActivity.class);
        if (l.getItemAtPosition(position) instanceof Map) {
            return;
        }
        SourceItem item = (SourceItem) l.getItemAtPosition(position);
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
        menu.add(0, ACCOUNT_LIST_ID, 0, "Accounts");

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


                buildAdapter();
                return true;
            case ACCOUNT_LIST_ID:
                startActivity(new Intent(this, AccountListActivity.class));
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
                        item.getSourceItemView().findViewById(R.id.loadingbar).setVisibility(View.VISIBLE);
                    }
                }
            }
        }
        );
    }

    public void notifyHasNew
            (CachedArticleSource
                     cachedArticleSource) {
        final CacheToken token = cachedArticleSource.getToken();

        final Cursor c = sourceDB.findAll();
        hander.post(new Runnable() {
            public void run() {
                ManagedCursor mc = new ManagedCursor(c);
                mc.each(new EachCursor() {
                    public void call(Cursor cursor, int index) {
                        SourceItem item = (SourceItem) adapter.getItem(index);
                        if (token.match(item)) {
                            IndexActivity.this.getListView().getChildAt(index).findViewById(R.id.loadingbar).setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
    }

    Handler hander = new Handler();

    public void notifyNoNew
            (CachedArticleSource
                     cachedArticleSource) {
        final CacheToken token = cachedArticleSource.getToken();

        final Cursor c = sourceDB.findAll();
        hander.post(new Runnable() {
            public void run() {
                ManagedCursor mc = new ManagedCursor(c);
                mc.each(new EachCursor() {
                    public void call(Cursor cursor, int index) {
                        SourceItem item = (SourceItem) adapter.getItem(index);
                        if (token.match(item)) {
                            IndexActivity.this.getListView().getChildAt(index).findViewById(R.id.loadingbar).setVisibility(View.GONE);
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