package com.goal98.flipdroid.activity;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.db.SourceDB;
import com.goal98.flipdroid.model.SourceRepo;
import com.goal98.flipdroid.util.Constants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SourceSelectionActivity extends ListActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.source_list);

        String type = getIntent().getExtras().getString("type");
        Log.v(this.getClass().getName(), "Account type:"+type);

        List<Map<String, String>> sourceList = new SourceRepo().findSourceByType(type);
        Map<String, String> customeSection = SourceRepo.buildSource("Add Custom Source", Constants.ADD_CUSTOME_SOURCE, "Add any person.");

        sourceList.add(customeSection);

        String[] from = new String[]{SourceDB.KEY_SOURCE_NAME, SourceDB.KEY_SOURCE_DESC};
        int[] to = new int[]{R.id.source_name, R.id.source_desc};
        SimpleAdapter adapter = new SimpleAdapter(this, sourceList, R.layout.source_item, from, to);

        setListAdapter(adapter);

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Map<String, String> source = (Map<String, String>)l.getItemAtPosition(position);
        String sourceId = source.get(SourceDB.KEY_SOURCE_ID);
        Log.v(this.getClass().getName(), sourceId);

        if(Constants.ADD_CUSTOME_SOURCE.equals(sourceId)){
            startActivity(new Intent(this, SourceSearchActivity.class));
        }
    }
}