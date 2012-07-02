package com.goal98.flipdroid2.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import com.actionbarsherlock.app.SherlockExpandableListActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.MenuItem;
import com.goal98.flipdroid2.R;
import com.goal98.flipdroid2.db.SourceDB;
import com.goal98.flipdroid2.model.GroupedSource;
import com.goal98.flipdroid2.model.Source;
import com.goal98.flipdroid2.model.SourceRepo;
import com.goal98.flipdroid2.util.Constants;
import com.goal98.flipdroid2.view.SourceExpandableListAdapter;
import com.goal98.tika.common.TikaConstants;
import com.mobclick.android.MobclickAgent;

import java.util.Map;

public class RSSSourceSelectionActivity extends SherlockExpandableListActivity {
    ActionMode mMode;

    protected SourceDB sourceDB;
    private GroupedSource groupedSource;
    private SourceExpandableListAdapter sourceExpandableListAdapter;

    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Sherlock);
        getSupportActionBar().setTitle(R.string.rssfeeds);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.source_expandable_list);
        sourceDB = new SourceDB(this);
        String type = getIntent().getExtras().getString("type");
        groupedSource = new SourceRepo(this).findGroupedSourceByType(type);
        addExtraItem(groupedSource);
        group();
    }


    private void group() {

    }

    @Override
    protected void onStop() {
        super.onStop();
        sourceDB.close();
    }

    @Override
    protected void onStart() {
        super.onStart();
        String[] from = new String[]{Source.KEY_SOURCE_NAME, Source.KEY_SOURCE_DESC, Source.KEY_IMAGE_URL, Source.KEY_SOURCE_TYPE};
        int[] to = new int[]{R.id.source_name, R.id.source_desc, R.id.source_image, R.id.source_type, R.id.group_desc};
        sourceExpandableListAdapter = new SourceExpandableListAdapter(this, groupedSource.getGroups(), R.layout.group, new String[]{SourceRepo.KEY_NAME_GROUP, SourceRepo.KEY_NAME_SAMPLES}, new int[]{R.id.txt_group, R.id.group_desc}, groupedSource.getChildren(), R.layout.source_item, from, to, sourceDB);
        setListAdapter(sourceExpandableListAdapter);
    }

    protected void addExtraItem(GroupedSource groupedSource) {
        Map<String, String> customeSection = SourceDB.buildSource(TikaConstants.TYPE_RSS,
                "Add Custom RSS Feed",
                Constants.ADD_CUSTOME_SOURCE,
                "Add any RSS URL here.", null, this.getString(R.string.custom));

        groupedSource.addGroup(SourceRepo.KEY_NAME_GROUP, this.getString(R.string.custom));
        groupedSource.addChild(this.getString(R.string.custom), customeSection);
    }

//    @Override
//    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
//        super.onChildClick(parent, v, groupPosition, childPosition, id);
////        if (mMode == null) {
////            mMode = startActionMode(new AnActionModeOfEpicProportions(this));
////            View closeButton = findViewById(R.id.abs__action_mode_close_button);
////            if (closeButton != null)
////                closeButton.setVisibility(View.GONE);
////        }
//
////        Map<String, String> source = (Map<String, String>) parent.getExpandableListAdapter().getChild(groupPosition, childPosition);
////        String sourceId = source.get(Source.KEY_SOURCE_ID);
////
////        if (Constants.ADD_CUSTOME_SOURCE.equals(sourceId)) {
////            doWithAddCustomerSouce();
////        } else {
////            if (sourceDB.findSourceByName(source.get(Source.KEY_SOURCE_NAME)).getCount() > 0) {
////                sourceDB.removeSourceByName(source.get(Source.KEY_SOURCE_NAME));
////            } else {
////                sourceDB.insert(source);
////            }
////            sourceExpandableListAdapter.notifyDataSetChanged();
////        }
//
//        return true;
//    }
//
//
//    public void doWithAddCustomerSouce() {
//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        final LinearLayout layout = new LinearLayout(this);
//
//        final EditText feedURL = new EditText(this);
//        feedURL.setSingleLine(true);
//        layout.setPadding(10, 5, 10, 5);
//        layout.addView(feedURL, 0, lp);
//
//        new AlertDialog.Builder(this)
//                .setTitle(R.string.pastersshere)
//                .setView(layout)
//                .setPositiveButton(android.R.string.ok,
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog,
//                                                int which) {
//                                String url = feedURL.getText().toString();
//                                if (!url.startsWith("http://"))
//                                    url = "http://" + url;
//
//                                RssParser rp = new RssParser(url);
//                                try {
//                                    rp.parse();
//                                    RssParser.RssFeed feed = rp.getFeed();
//                                    Map<String, String> customeRSSFeed = SourceDB.buildSource(TikaConstants.TYPE_RSS,
//                                            feed.title,
//                                            null,
//                                            feed.description, feed.imageUrl, url, feedURL.getContext().getString(R.string.custom));
//                                    sourceDB.insert(customeRSSFeed);
//
//                                    startActivity(new Intent(RSSSourceSelectionActivity.this, IndexActivity.class));
//
//                                    finish();
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                    new AlarmSender(RSSSourceSelectionActivity.this.getApplicationContext()).sendInstantMessage(R.string.rssinvalid);
//                                }
//                            }
//                        })
//                .setNegativeButton(android.R.string.cancel,
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog,
//                                                int which) {
//                                dialog.dismiss();
//                            }
//                        }).show();
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

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}

