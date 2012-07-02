package com.goal98.flipdroid2.activity;

import com.goal98.flipdroid2.model.SearchSource;
import com.goal98.flipdroid2.model.baidu.BaiduSearchSource;

public class BaiduSearchSourceActivity extends SourceSearchActivity {
    public SearchSource getSearchSource() {
	// TODO Auto-generated method stub
	return new BaiduSearchSource();
    }

}
