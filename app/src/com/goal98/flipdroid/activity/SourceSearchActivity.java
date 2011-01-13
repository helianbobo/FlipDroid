package com.goal98.flipdroid.activity;

import android.app.Activity;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SimpleAdapter;
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.db.SourceDB;
import com.goal98.flipdroid.model.SourceRepo;
import com.goal98.flipdroid.util.Constants;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SourceSearchActivity extends ListActivity {

    private String type = Constants.TYPE_SINA_WEIBO;

    List<Map<String, String>> sourceList;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.source_search);

        Bundle extras = getIntent().getExtras();
        if(extras != null)
            type = extras.getString("type");
        Log.v(this.getClass().getName(), "Account type:" + type);

        sourceList = new LinkedList<Map<String, String>>();
        String[] from = new String[]{SourceDB.KEY_SOURCE_NAME, SourceDB.KEY_SOURCE_DESC};
        int[] to = new int[]{R.id.source_name, R.id.source_desc};
        SimpleAdapter adapter = new SimpleAdapter(this, sourceList, R.layout.source_item, from, to);
        setListAdapter(adapter);

        Button searchButton = (Button) findViewById(R.id.source_search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Map<String, String> customeSection = SourceRepo.buildSource("Velour", "111", "Velour News");
                sourceList.add(customeSection);

            }
        });
    }


}