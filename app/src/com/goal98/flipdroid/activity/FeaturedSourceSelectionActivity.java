package com.goal98.flipdroid.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.BaseAdapter;
import android.widget.SimpleAdapter;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.MenuItem;
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.db.SourceDB;
import com.goal98.flipdroid.model.SourceRepo;
import com.goal98.tika.common.TikaConstants;
import com.srz.androidtools.util.DeviceInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 12-6-2
 * Time: 下午7:44
 * To change this template use File | Settings | File Templates.
 */
public class FeaturedSourceSelectionActivity extends SherlockListActivity {
    private DeviceInfo deviceInfo;
    private SourceDB sourceDB;
    private BaseAdapter adapter;

    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Sherlock);
        getSupportActionBar().setTitle(R.string.rssfeeds);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.featured_selection);
        sourceDB = new SourceDB(this.getApplication());
        List<Map<String, String>> maps = new SourceRepo(this).findSourceByType(TikaConstants.TYPE_FEATURED);
        bindAdapter(maps);
        getListView().setAdapter(adapter);
    }

    private void bindAdapter(List<Map<String, String>> maps) {
        if (deviceInfo == null)
            deviceInfo = DeviceInfo.getInstance(this);

        if (maps.size() == 0) {
            Map<String, String> noData = new HashMap<String, String>();
            noData.put("text", getString(R.string.nodata));
            List emptyBlock = new ArrayList();
            emptyBlock.add(noData);
            adapter = new SimpleAdapter(this, emptyBlock, R.layout.nodataitem,
                    new String[]{"text"},
                    new int[]{R.id.text});
            return;
        }

        adapter = new SourceItemArrayAdapter<SourceItem>(this, R.layout.source_item, maps, deviceInfo);

        setListAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        setResult(RESULT_OK);
        finish();
        return true;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
            setResult(RESULT_OK);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
