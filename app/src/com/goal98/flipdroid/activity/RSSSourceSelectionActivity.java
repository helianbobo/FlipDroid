package com.goal98.flipdroid.activity;

import android.app.AlertDialog;
import android.app.ExpandableListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.db.SourceDB;
import com.goal98.flipdroid.model.GroupedSource;
import com.goal98.flipdroid.model.Source;
import com.goal98.flipdroid.model.SourceRepo;
import com.goal98.flipdroid.model.rss.RssParser;
import com.goal98.flipdroid.util.AlarmSender;
import com.goal98.flipdroid.util.Constants;
import com.goal98.flipdroid.view.SourceExpandableListAdapter;

import java.util.Map;

public class RSSSourceSelectionActivity extends ExpandableListActivity {

    protected SourceDB sourceDB;
    private GroupedSource groupedSource;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.source_expandable_list);
        sourceDB = new SourceDB(this);
        String type = getIntent().getExtras().getString("type");
        groupedSource = new SourceRepo(this).findGroupedSourceByType(type);
        //////System.out.println("on create");
        addExtraItem(groupedSource);
        group();
    }

    private void group() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        String[] from = new String[]{Source.KEY_SOURCE_NAME, Source.KEY_SOURCE_DESC, Source.KEY_IMAGE_URL, Source.KEY_ACCOUNT_TYPE};
        int[] to = new int[]{R.id.source_name, R.id.source_desc, R.id.source_image, R.id.source_type, R.id.group_desc};
        ExpandableListAdapter adapter = new SourceExpandableListAdapter(this, groupedSource.getGroups(), R.layout.group, new String[]{SourceRepo.KEY_NAME_GROUP, SourceRepo.KEY_NAME_SAMPLES}, new int[]{R.id.txt_group, R.id.group_desc}, groupedSource.getChildren(), R.layout.source_item, from, to);
        setListAdapter(adapter);
    }

    protected void addExtraItem(GroupedSource groupedSource) {
        Map<String, String> customeSection = SourceDB.buildSource(Constants.TYPE_RSS,
                "Add Custom RSS Feed",
                Constants.ADD_CUSTOME_SOURCE,
                "Add any RSS URL here.", null, this.getString(R.string.custom));

        groupedSource.addGroup(SourceRepo.KEY_NAME_GROUP,this.getString(R.string.custom));
        groupedSource.addChild(this.getString(R.string.custom), customeSection);
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        super.onChildClick(parent, v, groupPosition, childPosition, id);
        Map<String, String> source = (Map<String, String>) parent.getExpandableListAdapter().getChild(groupPosition, childPosition);
        String sourceId = source.get(Source.KEY_SOURCE_ID);

        if (Constants.ADD_CUSTOME_SOURCE.equals(sourceId)) {
            doWithAddCustomerSouce();
        } else {
            sourceDB.insert(source);
            startActivity(new Intent(this, IndexActivity.class));
            finish();
        }
        return true;
    }

    public void doWithAddCustomerSouce() {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final LinearLayout layout = new LinearLayout(this);

        final EditText feedURL = new EditText(this);
        feedURL.setSingleLine(true);
        layout.setPadding(10, 5, 10, 5);
        layout.addView(feedURL, 0, lp);

        new AlertDialog.Builder(this)
                .setTitle(R.string.pastersshere)
                .setView(layout)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                String url = feedURL.getText().toString();
                                if (!url.startsWith("http://"))
                                    url = "http://" + url;

                                RssParser rp = new RssParser(url);
                                try {
                                    rp.parse();
                                    RssParser.RssFeed feed = rp.getFeed();
                                    Map<String, String> customeRSSFeed = SourceDB.buildSource(Constants.TYPE_RSS,
                                            feed.title,
                                            null,
                                            feed.description, feed.imageUrl, url);
                                    sourceDB.insert(customeRSSFeed);

                                    startActivity(new Intent(RSSSourceSelectionActivity.this, IndexActivity.class));

                                    finish();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    AlarmSender.sendInstantMessage(R.string.rssinvalid, RSSSourceSelectionActivity.this);
                                }
                            }
                        })
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        }).show();
    }
}