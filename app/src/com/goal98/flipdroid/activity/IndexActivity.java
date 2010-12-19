package com.goal98.flipdroid.activity;

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import com.goal98.flipdroid.R;

import java.util.ArrayList;

public class IndexActivity extends ListActivity {

    private ArrayAdapter<String> mAdapter;

    private ArrayList<String> mStrings = new ArrayList<String>();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mStrings.add("Weibo");
        mStrings.add("helianbobo");

        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mStrings);
        setListAdapter(mAdapter);

        setContentView(R.layout.index);

        setTheme(android.R.style.Theme_Light);

    }
}