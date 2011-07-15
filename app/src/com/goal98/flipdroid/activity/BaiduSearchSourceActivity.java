package com.goal98.flipdroid.activity;

import com.goal98.flipdroid.model.SearchSource;
import com.goal98.flipdroid.model.baidu.BaiduSearchSource;
import com.goal98.flipdroid.model.flipdroid.FlipdroidSearchSource;

public class BaiduSearchSourceActivity extends SourceSearchActivity {
    public SearchSource getSearchSource() {
	// TODO Auto-generated method stub
	return new BaiduSearchSource();
    }

}
