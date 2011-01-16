package com.goal98.flipdroid.activity;

import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.db.AccountDB;
import com.goal98.flipdroid.db.SourceDB;

import java.util.ArrayList;

public class IndexActivity extends ListActivity {

    static final private int CONFIG_ID = Menu.FIRST;
    static final private int CLEAR_ID = Menu.FIRST + 1;
    static final private int ACCOUNT_LIST_ID = Menu.FIRST + 2;

    private ArrayAdapter<String> mAdapter;

    private AccountDB accountDB;
    private SourceDB sourceDB;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        accountDB = new AccountDB(this);
        sourceDB = new SourceDB(this);

        Cursor cursor = sourceDB.findAll();
        startManagingCursor(cursor);

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.source_item, cursor,
                new String[]{SourceDB.KEY_SOURCE_NAME, SourceDB.KEY_SOURCE_DESC},
                new int[]{R.id.source_name, R.id.source_desc});
        setListAdapter(adapter);

        setContentView(R.layout.index);

        Button button = (Button) findViewById(R.id.btn_add_source);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(IndexActivity.this, SiteActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left, R.anim.fade);
            }
        });

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        accountDB.close();
        sourceDB.close();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent = new Intent(this, PageActivity.class);
        Cursor cursor = (Cursor) l.getItemAtPosition(position);
        intent.putExtra("type", cursor.getString(cursor.getColumnIndex(SourceDB.KEY_ACCOUNT_TYPE)));
        intent.putExtra("sourceId", cursor.getString(cursor.getColumnIndex(SourceDB.KEY_SOURCE_ID)));
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
                int count = accountDB.deleteAll();
                Log.e(this.getClass().getName(), count + " accounts are deleted.");
                return true;
            case ACCOUNT_LIST_ID:
                startActivity(new Intent(this, AccountListActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}