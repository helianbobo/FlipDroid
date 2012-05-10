package com.goal98.flipdroid.activity;

import android.app.ExpandableListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.db.SourceDB;
import com.goal98.flipdroid.model.GroupedSource;
import com.goal98.flipdroid.model.SearchSource;
import com.goal98.flipdroid.model.Source;
import com.goal98.flipdroid.model.SourceRepo;
import com.goal98.flipdroid.model.sina.SearchSourceTask;
import com.goal98.flipdroid.util.SinaAccountUtil;
import com.goal98.flipdroid.view.SourceExpandableListAdapter;
import com.goal98.tika.common.TikaConstants;
import com.mobclick.android.MobclickAgent;

import java.util.Map;

abstract public class SourceSearchActivity extends ExpandableListActivity {

//    protected List<Map<String, String>> sourceList;

    private SourceExpandableListAdapter adapter;

    private EditText queryText;

    private SourceDB sourceDB;
    private GroupedSource groupedSource = new GroupedSource();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setProgressBarIndeterminateVisibility(false);

        sourceDB = new SourceDB(this);

        setContentView(R.layout.source_search);

        queryText = (EditText) findViewById(R.id.source_query);

//        sourceList = new LinkedList<Map<String, String>>();
//        String[] from = new String[]{Source.KEY_SOURCE_NAME, Source.KEY_SOURCE_DESC, Source.KEY_IMAGE_URL, Source.KEY_ACCOUNT_TYPE};
//        int[] to = new int[]{R.id.source_name, R.id.source_desc, R.id.source_image, R.id.source_type};
//        adapter = new SimpleAdapter(this, sourceList, R.layout.source_item, from, to);
//        adapter.setViewBinder(new SourceItemViewBinder());
//        setListAdapter(adapter);

        String[] from = new String[]{Source.KEY_SOURCE_NAME, Source.KEY_SOURCE_DESC, Source.KEY_IMAGE_URL, Source.KEY_SOURCE_TYPE};
        int[] to = new int[]{R.id.source_name, R.id.source_desc, R.id.source_image, R.id.source_type, R.id.group_desc};
        adapter = new SourceExpandableListAdapter(this, groupedSource.getGroups(), R.layout.group, new String[]{SourceRepo.KEY_NAME_GROUP, SourceRepo.KEY_NAME_SAMPLES}, new int[]{R.id.txt_group, R.id.group_desc}, groupedSource.getChildren(), R.layout.source_item, from, to,sourceDB);
        setListAdapter(adapter);

        Button searchButton = (Button) findViewById(R.id.source_search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String query = queryText.getText().toString();
                System.out.println("wawa"+groupedSource.getChildren().size());
//                sourceList.removeAll(sourceList);
                groupedSource.getGroups().clear();
                groupedSource.getChildren().clear();
                doSearch(query);
            }
        });
    }

    public abstract SearchSource getSearchSource();

    public void doSearch(String query) {
        new SearchSourceTask(this, adapter, groupedSource, getSearchSource()).execute(query);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        super.onChildClick(parent, v, groupPosition, childPosition, id);
        Map<String, String> source = (Map<String, String>) parent.getExpandableListAdapter().getChild(groupPosition, childPosition);

        if (source.get(Source.KEY_SOURCE_TYPE).equals(TikaConstants.TYPE_SINA_WEIBO)) {
            if (!SinaAccountUtil.alreadyBinded(this)) {
                final Intent intent = new Intent(this, SinaAccountActivity.class);
                intent.putExtra("PROMPTTEXT", this.getString(R.string.addsinamusthavesina));
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left, R.anim.fade);
            }
        }
        sourceDB.insert(source);

        startActivity(new Intent(this, IndexActivity.class));

        finish();
        return true;
    }


}