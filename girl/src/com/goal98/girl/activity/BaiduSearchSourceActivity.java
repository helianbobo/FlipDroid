package com.goal98.girl.activity;

import com.goal98.girl.model.SearchSource;
import com.goal98.girl.model.baidu.BaiduSearchSource;

public class BaiduSearchSourceActivity extends SourceSearchActivity {
    public SearchSource getSearchSource() {
	// TODO Auto-generated method stub
	return new BaiduSearchSource();
    }

}
