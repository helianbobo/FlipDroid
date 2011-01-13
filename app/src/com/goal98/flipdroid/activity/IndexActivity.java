package com.goal98.flipdroid.activity;

import android.app.ListActivity;
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
import android.widget.ListView;
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.db.AccountDB;

import java.util.ArrayList;

public class IndexActivity extends ListActivity {

    static final private int CONFIG_ID = Menu.FIRST;
    static final private int CLEAR_ID = Menu.FIRST+1;
    static final private int ACCOUNT_LIST_ID = Menu.FIRST+2;

    private ArrayAdapter<String> mAdapter;

    private ArrayList<String> mStrings = new ArrayList<String>();

    private AccountDB accountDB;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        accountDB = new AccountDB(this);

        mStrings.add(getString(R.string.button_add_new_source));
        mStrings.add("weibo");
        mStrings.add("helianbobo");
        mStrings.add("fake");

        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mStrings);
        setListAdapter(mAdapter);

        setContentView(R.layout.index);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        if (position == 0) {
            Intent intent = new Intent(this, SiteActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.slide_in_left, R.anim.fade);

        } else {
            Intent intent = new Intent(this, PageActivity.class);
            intent.putExtra("repo", (String) l.getItemAtPosition(position));
            startActivity(intent);
            overridePendingTransition(android.R.anim.slide_in_left, R.anim.fade);
        }
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