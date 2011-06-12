package com.goal98.flipdroid.activity;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.db.SourceDB;
import com.goal98.flipdroid.model.Source;
import com.goal98.flipdroid.model.SourceRepo;
import com.goal98.flipdroid.model.rss.RssParser;
import com.goal98.flipdroid.util.AlarmSender;
import com.goal98.flipdroid.util.Constants;
import com.goal98.flipdroid.view.SourceItemViewBinder;

import java.util.List;
import java.util.Map;

public class RSSSourceSelectionActivity extends ListActivity {
    protected SourceDB sourceDB;
    private List<Map<String, String>> sourceList;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.source_list);
        sourceDB = new SourceDB(this);
        String type = getIntent().getExtras().getString("type");
        System.out.println("on create");
        sourceList = new SourceRepo(this).findSourceByType(type);
        addExtraItem(sourceList);
    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("on start");
        String[] from = new String[]{Source.KEY_SOURCE_NAME, Source.KEY_SOURCE_DESC, Source.KEY_IMAGE_URL};
        int[] to = new int[]{R.id.source_name, R.id.source_desc, R.id.source_image};
        SimpleAdapter adapter = new SimpleAdapter(this, sourceList, R.layout.source_item, from, to);
        adapter.setViewBinder(new SourceItemViewBinder());

        setListAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    protected void addExtraItem(List<Map<String, String>> sourceList) {
        Map<String, String> customeSection = SourceDB.buildSource(Constants.TYPE_RSS,
                "Add Custom RSS Feed",
                Constants.ADD_CUSTOME_SOURCE,
                "Add any RSS URL here.", null, null);

        sourceList.add(customeSection);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Map<String, String> source = (Map<String, String>) l.getItemAtPosition(position);
        String sourceId = source.get(Source.KEY_SOURCE_ID);
        Log.v(this.getClass().getName(), sourceId);

        if (Constants.ADD_CUSTOME_SOURCE.equals(sourceId)) {
            doWithAddCustomerSouce();
        } else {
            sourceDB.insert(source);
            startActivity(new Intent(this, IndexActivity.class));
            finish();
        }


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
                                            feed.description, null, url);
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