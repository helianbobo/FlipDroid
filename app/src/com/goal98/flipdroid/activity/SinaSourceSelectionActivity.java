package com.goal98.flipdroid.activity;

import android.content.Intent;
import com.goal98.flipdroid.db.SourceDB;
import com.goal98.flipdroid.util.Constants;

import java.util.List;
import java.util.Map;

public class SinaSourceSelectionActivity extends RSSSourceSelectionActivity {
    @Override
    protected void addExtraItem(List<Map<String, String>> sourceList) {
        Map<String, String> customeSection = SourceDB.buildSource(Constants.TYPE_SINA_WEIBO,
                "Add Custom Source",
                Constants.ADD_CUSTOME_SOURCE,
                "Add any person.", null, null);

        sourceList.add(customeSection);
    }

    public void doWithAddCustomerSouce() {
        startActivity(new Intent(this, SourceSearchActivity.class));
        finish();
    }
}