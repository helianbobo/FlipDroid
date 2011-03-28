package com.goal98.flipdroid.activity;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.*;
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.db.AccountDB;
import com.goal98.flipdroid.db.SourceDB;
import com.goal98.flipdroid.model.Source;
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
    public static int statusBarHeight;
    public static int titleBarHeight;
    public static int maxHeight;
    public static int maxWidth;

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
        button.post(new Runnable() {
            public void run() {
                Rect rect = new Rect();
                Window window = getWindow();
                window.getDecorView().getWindowVisibleDisplayFrame(rect);
                statusBarHeight = rect.top;
                int contentViewTop =
                        window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
                titleBarHeight = contentViewTop - statusBarHeight;
                maxHeight = (int) ((int) (IndexActivity.this.getWindowManager().getDefaultDisplay().getHeight()) - statusBarHeight - titleBarHeight*2.2);
                maxWidth = (int) (IndexActivity.this.getWindowManager().getDefaultDisplay().getWidth())-20;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sourceCursor != null)
            sourceCursor.close();
        if (accountDB != null)
            accountDB.close();
        if (sourceDB != null)
            sourceDB.close();
    }

    @Override
    protected void onStart() {
        super.onStart();

        sourceCursor = sourceDB.findAll();
        startManagingCursor(sourceCursor);

        adapter = new SimpleCursorAdapter(this, R.layout.source_item, sourceCursor,
                new String[]{Source.KEY_SOURCE_NAME, Source.KEY_SOURCE_DESC, Source.KEY_IMAGE_URL},
                new int[]{R.id.source_name, R.id.source_desc, R.id.source_image});
        adapter.setViewBinder(new SourceItemViewBinder());
        setListAdapter(adapter);
    }


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent = new Intent(this, PageActivity.class);
        Cursor cursor = (Cursor) l.getItemAtPosition(position);
        intent.putExtra("type", cursor.getString(cursor.getColumnIndex(Source.KEY_ACCOUNT_TYPE)));
        intent.putExtra("sourceId", cursor.getString(cursor.getColumnIndex(Source.KEY_SOURCE_ID)));
        cursor.close();
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

                accountDB = new AccountDB(this);
                int count = accountDB.deleteAll();
                Log.e(this.getClass().getName(), count + " accounts are deleted.");

                count = sourceDB.deleteAll();
                Log.e(this.getClass().getName(), count + " sources are deleted.");

                adapter.notifyDataSetChanged();
                return true;
            case ACCOUNT_LIST_ID:
                startActivity(new Intent(this, AccountListActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}