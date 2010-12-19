package com.goal98.flipdroid.activity;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.goal98.flipdroid.R;

import java.util.ArrayList;

public class IndexActivity extends ListActivity {

    private ArrayAdapter<String> mAdapter;

    private ArrayList<String> mStrings = new ArrayList<String>();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mStrings.add("weibo");
        mStrings.add("helianbobo");

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
    }
}