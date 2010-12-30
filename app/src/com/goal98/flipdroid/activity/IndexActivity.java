package com.goal98.flipdroid.activity;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.goal98.flipdroid.R;

import java.util.ArrayList;

public class IndexActivity extends ListActivity {

    static final private int CONFIG_ID = Menu.FIRST;

    private ArrayAdapter<String> mAdapter;

    private ArrayList<String> mStrings = new ArrayList<String>();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mStrings.add(getString(R.string.button_add_new_account));
        mStrings.add("weibo");
        mStrings.add("helianbobo");
        mStrings.add("fake");

        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mStrings);
        setListAdapter(mAdapter);

        setContentView(R.layout.index);

        setTheme(android.R.style.Theme_Light);

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        Intent intent = new Intent(this, PageActivity.class);
        intent.putExtra("repo", (String)l.getItemAtPosition(position));
        startActivity(intent);
        overridePendingTransition(android.R.anim.slide_in_left, R.anim.fade);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(0, CONFIG_ID, 0, R.string.config);

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
        }

        return super.onOptionsItemSelected(item);
    }
}