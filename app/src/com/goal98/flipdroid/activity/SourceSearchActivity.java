package com.goal98.flipdroid.activity;

import android.app.AlarmManager;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.*;
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.db.SourceDB;
import com.goal98.flipdroid.exception.NoNetworkException;
import com.goal98.flipdroid.model.Source;
import com.goal98.flipdroid.model.SourceRepo;
import com.goal98.flipdroid.model.sina.SinaArticleSource;
import com.goal98.flipdroid.util.AlarmSender;
import com.goal98.flipdroid.util.Constants;
import com.goal98.flipdroid.util.OneShotAlarm;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SourceSearchActivity extends ListActivity {

    private String type = Constants.TYPE_SINA_WEIBO;

    private List<Map<String, String>> sourceList;

    private SimpleAdapter adapter;

    private EditText queryText;

    private AlarmSender alarmSender;

    private SourceDB sourceDB;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        alarmSender = new AlarmSender(this);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setProgressBarIndeterminateVisibility(false);

        sourceDB = new SourceDB(this);

        setContentView(R.layout.source_search);

        queryText = (EditText) findViewById(R.id.source_query);

        Bundle extras = getIntent().getExtras();
        if (extras != null)
            type = extras.getString("type");
        Log.v(this.getClass().getName(), "Account type:" + type);

        sourceList = new LinkedList<Map<String, String>>();

        String[] from = new String[]{SourceDB.KEY_SOURCE_NAME, SourceDB.KEY_SOURCE_DESC};
        int[] to = new int[]{R.id.source_name, R.id.source_desc};
        adapter = new SimpleAdapter(this, sourceList, R.layout.source_item, from, to);
        setListAdapter(adapter);

        Button searchButton = (Button) findViewById(R.id.source_search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String query = queryText.getText().toString();
                sourceList.removeAll(sourceList);
                new SearchSourceTask().execute(query);
            }
        });
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Map<String, String> source = (Map<String, String>) l.getItemAtPosition(position);
        sourceDB.insert(source);

        startActivity(new Intent(this, IndexActivity.class));
    }

    private class SearchSourceTask extends AsyncTask<String, NoNetworkException, Integer> {

        @Override
        protected void onPreExecute() {
            setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected Integer doInBackground(String... strings) {

            SinaArticleSource sinaSource = new SinaArticleSource(false, "13774256612", "541116", null);
            List<Source> list = sinaSource.searchSource(strings[0]);

            if (list != null) {
                for (int i = 0; i < list.size(); i++) {
                    Source source = list.get(i);
                    Map<String, String> customeSection =
                            SourceRepo.buildSource(source.getAccountType() ,source.getName(), source.getId(), source.getDesc());
                    sourceList.add(customeSection);

                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Integer pageIndex) {

            adapter.notifyDataSetChanged();
            setProgressBarIndeterminateVisibility(false);
        }

        @Override
        protected void onProgressUpdate(NoNetworkException... exceptions) {
            if (exceptions != null && exceptions.length > 0)
                handleException(exceptions[0]);
        }
    }



    private void handleException(NoNetworkException e) {

        String msg = e.getMessage();
        alarmSender.sendAlarm(msg);
    }




}