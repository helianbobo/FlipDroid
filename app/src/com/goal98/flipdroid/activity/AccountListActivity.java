package com.goal98.flipdroid.activity;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SimpleCursorAdapter;
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.db.AccountDB;

public class AccountListActivity extends ListActivity {

    private AccountDB accountDB;

    static final private int NEW_ACCOUNT_ID = Menu.FIRST;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        accountDB = new AccountDB(this);

        Cursor cursor = accountDB.findAll();
        startManagingCursor(cursor);

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.account_item, cursor,
                new String[] { AccountDB.KEY_USERNAME, AccountDB.KEY_ACCOUNT_TYPE },
                new int[] { R.id.accoount_title, R.id.accoount_type });
        setListAdapter(adapter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        accountDB.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(0, NEW_ACCOUNT_ID, 0, getString(R.string.button_add_new_account));

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
            case NEW_ACCOUNT_ID:
                startActivity(new Intent(this, SinaAccountActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}