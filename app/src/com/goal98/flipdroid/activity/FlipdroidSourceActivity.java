package com.goal98.flipdroid.activity;

import com.goal98.flipdroid.model.SearchSource;
import com.goal98.flipdroid.model.flipdroid.FlipdroidSearchSource;
import com.goal98.flipdroid.model.sina.SearchSourceTask;
import com.goal98.flipdroid.util.Constants;

public class FlipdroidSourceActivity extends SourceSearchActivity {
    public SearchSource getSearchSource() {
        return new FlipdroidSearchSource();
    }
}