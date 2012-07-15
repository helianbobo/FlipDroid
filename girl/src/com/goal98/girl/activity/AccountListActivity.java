package com.goal98.girl.activity;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SimpleCursorAdapter;
import com.goal98.girl.R;
import com.goal98.girl.db.AccountDB;
import com.goal98.girl.model.Account;
import com.mobclick.android.MobclickAgent;

public class AccountListActivity extends ListActivity {

    private AccountDB accountDB;
    private Cursor accountCursor;

    static final private int NEW_ACCOUNT_ID = Menu.FIRST;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        accountDB = new AccountDB(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        accountCursor.close();
        accountDB.close();
    }

    @Override
    protected void onStart() {
        super.onStart();
        accountCursor = accountDB.findAll();
        startManagingCursor(accountCursor);

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.account_item, accountCursor,
                new String[]{Account.KEY_USERNAME, Account.KEY_ACCOUNT_TYPE},
                new int[]{R.id.accoount_title, R.id.accoount_type});
        setListAdapter(adapter);

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

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}