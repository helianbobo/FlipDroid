package com.goal98.flipdroid.activity;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.model.SourceRepo;

import java.util.List;

public class SourceSelectionActivity extends ListActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.source);

        String type = getIntent().getExtras().getString("type");
        Log.v(this.getClass().getName(), "Account type:"+type);

        List<String> sourceList = new SourceRepo().findSourceByType(type);
        sourceList.add("Search Source");

        setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, sourceList.toArray(new String[0])) );

    }
}