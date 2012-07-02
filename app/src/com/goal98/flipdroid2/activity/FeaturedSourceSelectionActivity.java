package com.goal98.flipdroid2.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import com.actionbarsherlock.app.SherlockExpandableListActivity;
import com.actionbarsherlock.view.MenuItem;
import com.goal98.flipdroid2.R;
import com.goal98.flipdroid2.db.SourceDB;
import com.goal98.flipdroid2.model.GroupedSource;
import com.goal98.flipdroid2.model.Source;
import com.goal98.flipdroid2.model.SourceRepo;
import com.goal98.flipdroid2.view.SourceExpandableListAdapter;
import com.goal98.tika.common.TikaConstants;
import com.srz.androidtools.util.DeviceInfo;

/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 12-6-2
 * Time: 下午7:44
 * To change this template use File | Settings | File Templates.
 */
public class FeaturedSourceSelectionActivity extends SherlockExpandableListActivity {
    private DeviceInfo deviceInfo;
    private SourceDB sourceDB;
    private SourceExpandableListAdapter sourceExpandableListAdapter;
    private GroupedSource groupedSource;

    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Sherlock);
        getSupportActionBar().setTitle(R.string.featured);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.featured_selection);
        sourceDB = new SourceDB(this.getApplication());
        groupedSource = new SourceRepo(this).findGroupedSourceByType(TikaConstants.TYPE_FEATURED);
    }

    @Override
    protected void onStart() {
        super.onStart();
        String[] from = new String[]{Source.KEY_SOURCE_NAME, Source.KEY_SOURCE_DESC, Source.KEY_IMAGE_URL, Source.KEY_SOURCE_TYPE};
        int[] to = new int[]{R.id.source_name, R.id.source_desc, R.id.source_image, R.id.source_type, R.id.group_desc};
        sourceExpandableListAdapter = new SourceExpandableListAdapter(this, groupedSource.getGroups(), R.layout.group, new String[]{SourceRepo.KEY_NAME_GROUP, SourceRepo.KEY_NAME_SAMPLES}, new int[]{R.id.txt_group, R.id.group_desc}, groupedSource.getChildren(), R.layout.source_item, from, to, sourceDB);
        setListAdapter(sourceExpandableListAdapter);
    }

//    private void bindAdapter(List<Map<String, String>> maps) {
//        if (deviceInfo == null)
//            deviceInfo = DeviceInfo.getInstance(this);
//
//        if (maps.size() == 0) {
//            Map<String, String> noData = new HashMap<String, String>();
//            noData.put("text", getString(R.string.nodata));
//            List emptyBlock = new ArrayList();
//            emptyBlock.add(noData);
//            adapter = new SimpleAdapter(this, emptyBlock, R.layout.nodataitem,
//                    new String[]{"text"},
//                    new int[]{R.id.text});
//            return;
//        }
//
//        adapter = new SourceItemArrayAdapter<SourceItem>(this, R.layout.source_item, maps, deviceInfo);
//
//        setListAdapter(adapter);
//    }

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
