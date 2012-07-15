package com.goal98.girl.activity;

import com.goal98.girl.model.SearchSource;
import com.goal98.girl.model.flipdroid.FlipdroidSearchSource;

public class FlipdroidSourceActivity extends SourceSearchActivity {
    public SearchSource getSearchSource() {
        return new FlipdroidSearchSource();
    }
}