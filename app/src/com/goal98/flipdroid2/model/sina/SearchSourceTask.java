package com.goal98.flipdroid2.model.sina;

import android.os.AsyncTask;
import com.goal98.flipdroid2.activity.SourceSearchActivity;
import com.goal98.flipdroid2.exception.NoNetworkException;
import com.goal98.flipdroid2.model.GroupedSource;
import com.goal98.flipdroid2.model.SearchSource;
import com.goal98.flipdroid2.util.AlarmSender;
import com.goal98.flipdroid2.view.SourceExpandableListAdapter;

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
        this.alarmSender = new AlarmSender(sourceSearchActivity.getApplicationContext());
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