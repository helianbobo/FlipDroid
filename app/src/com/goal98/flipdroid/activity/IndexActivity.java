package com.goal98.flipdroid.activity;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Rect;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.db.AccountDB;
import com.goal98.flipdroid.db.SourceDB;
import com.goal98.flipdroid.model.Source;
import com.goal98.flipdroid.util.Constants;
import com.goal98.flipdroid.util.DeviceInfo;
import com.goal98.flipdroid.view.SourceItemViewBinder;

public class IndexActivity extends ListActivity {

    static final private int CONFIG_ID = Menu.FIRST;
    static final private int CLEAR_ID = Menu.FIRST + 1;
    static final private int ACCOUNT_LIST_ID = Menu.FIRST + 2;

    private ArrayAdapter<String> mAdapter;

    private AccountDB accountDB;
    private SourceDB sourceDB;
    private Cursor sourceCursor;

    private SimpleCursorAdapter adapter;


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        accountDB = new AccountDB(getApplicationContext());
        sourceDB = new SourceDB(getApplicationContext());

        setContentView(R.layout.index);

        Button button = (Button) findViewById(R.id.btn_add_source);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(IndexActivity.this, SiteActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left, R.anim.fade);
            }
        });
        this.getListView().setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            public void onCreateContextMenu(ContextMenu menu, View v,
                                            ContextMenu.ContextMenuInfo menuInfo) {

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
        String sourceName = sourceNameTextView.getText().toString();
        String sourceType = sourceTypeTextView.getText().toString();
        if (item.getItemId() == 0) {//delete
            sourceDB.removeSourceByName(sourceName);
            if (sourceType.equals(Constants.TYPE_MY_SINA_WEIBO)) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                String sinaAccountId = preferences.getString(WeiPaiWebViewClient.SINA_ACCOUNT_PREF_KEY, null);
                preferences.edit().putString(WeiPaiWebViewClient.SINA_ACCOUNT_PREF_KEY, null).commit();
                preferences.edit().putString(WeiPaiWebViewClient.PREVIOUS_SINA_ACCOUNT_PREF_KEY, sinaAccountId).commit();
            }
            sourceCursor.close();
            sourceCursor = sourceDB.findAll();
            adapter.changeCursor(sourceCursor);
            return true;
        }
        return super.onContextItemSelected(item);
    }

    private void bindAdapter() {
        adapter = new SimpleCursorAdapter(this, R.layout.source_item, sourceCursor,
                new String[]{Source.KEY_SOURCE_NAME, Source.KEY_SOURCE_DESC, Source.KEY_IMAGE_URL, Source.KEY_ACCOUNT_TYPE},
                new int[]{R.id.source_name, R.id.source_desc, R.id.source_image, R.id.source_type});
        adapter.setViewBinder(new SourceItemViewBinder());
        setListAdapter(adapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        sourceCursor = sourceDB.findAll();
        startManagingCursor(sourceCursor);

        bindAdapter();

        adapter.notifyDataSetChanged();
    }

    private void openDatabase() {
        sourceDB = new SourceDB(getApplicationContext());
        accountDB = new AccountDB(this);
        sourceCursor = sourceDB.findAll();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeDB();
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
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeDB();
    }

    @Override
    protected void onStop() {
        super.onStop();
        closeDB();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent = new Intent(this, PageActivity.class);
        Cursor cursor = (Cursor) l.getItemAtPosition(position);
        intent.putExtra("type", cursor.getString(cursor.getColumnIndex(Source.KEY_ACCOUNT_TYPE)));
        intent.putExtra("sourceId", cursor.getString(cursor.getColumnIndex(Source.KEY_SOURCE_ID)));
        intent.putExtra("sourceImage", cursor.getString(cursor.getColumnIndex(Source.KEY_IMAGE_URL)));
        intent.putExtra("sourceName", cursor.getString(cursor.getColumnIndex(Source.KEY_SOURCE_NAME)));
        intent.putExtra("contentUrl", cursor.getString(cursor.getColumnIndex(Source.KEY_CONTENT_URL)));//for rss
        closeDB();
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

                accountDB = new AccountDB(this);
                int count = accountDB.deleteAll();
                Log.e(this.getClass().getName(), count + " accounts are deleted.");

                count = sourceDB.deleteAll();
                Log.e(this.getClass().getName(), count + " sources are deleted.");

                sourceCursor.close();
                sourceCursor = sourceDB.findAll();
                adapter.changeCursor(sourceCursor);
                sourceCursor.close();
                return true;
            case ACCOUNT_LIST_ID:
                startActivity(new Intent(this, AccountListActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}