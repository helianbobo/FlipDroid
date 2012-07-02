package com.goal98.flipdroid2.activity;

import com.goal98.flipdroid2.model.SearchSource;
import com.goal98.flipdroid2.model.flipdroid.FlipdroidSearchSource;

public class FlipdroidSourceActivity extends SourceSearchActivity {
    public SearchSource getSearchSource() {
        return new FlipdroidSearchSource();
    }
}