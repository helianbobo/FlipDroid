package com.goal98.flipdroid.model.sina;

import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.widget.ExpandableListAdapter;
import android.widget.SimpleAdapter;
import com.goal98.flipdroid.activity.SourceSearchActivity;
import com.goal98.flipdroid.db.SourceDB;
import com.goal98.flipdroid.exception.NoNetworkException;
import com.goal98.flipdroid.model.GroupedSource;
import com.goal98.flipdroid.model.SearchSource;
import com.goal98.flipdroid.model.Source;
import com.goal98.flipdroid.model.flipdroid.FlipdroidSearchSource;
import com.goal98.flipdroid.util.AlarmSender;
import com.goal98.flipdroid.view.SourceExpandableListAdapter;

import java.util.List;
import java.util.Map;

public class SearchSourceTask extends AsyncTask<String, NoNetworkException, Integer> {
    private SourceSearchActivity sourceSearchActivity;
    private SourceExpandableListAdapter adapter;
    private GroupedSource groupedSource;
    private AlarmSender alarmSender;
    public SearchSource source;

    public SearchSourceTask(SourceSearchActivity sourceSearchActivity, SourceExpandableListAdapter adapter, GroupedSource groupedSource, SearchSource searchSource) {
        this.sourceSearchActivity = sourceSearchActivity;
        this.adapter = adapter;
        this.groupedSource = groupedSource;
        this.alarmSender = new AlarmSender(sourceSearchActivity);
        this.source = searchSource;
    }

    @Override
    protected void onPreExecute() {
        sourceSearchActivity.setProgressBarIndeterminateVisibility(true);
    }

    @Override
    protected Integer doInBackground(String... strings) {
        GroupedSource groupedSource = source.searchSource(strings[0]);
        this.groupedSource.getChildren().addAll(groupedSource.getChildren());
        this.groupedSource.getGroups().addAll(groupedSource.getGroups());

        return null;
    }

    @Override
    protected void onPostExecute(Integer pageIndex) {

        sourceSearchActivity.setProgressBarIndeterminateVisibility(false);
        adapter.notifyDataSetChanged();
        System.out.println("wawanotified");

    }

    @Override
    protected void onProgressUpdate(NoNetworkException... exceptions) {
        if (exceptions != null && exceptions.length > 0)
            handleException(exceptions[0]);
    }

    private void handleException(NoNetworkException e) {
        String msg = e.getMessage();
        alarmSender.sendAlarm(msg);
    }


}