package com.goal98.girl.activity;

import android.content.Intent;
import com.goal98.girl.R;
import com.goal98.girl.db.SourceDB;
import com.goal98.girl.model.GroupedSource;
import com.goal98.girl.model.SourceRepo;
import com.goal98.girl.util.Constants;
import com.goal98.tika.common.TikaConstants;

import java.util.Map;

public class SinaSourceSelectionActivity extends RSSSourceSelectionActivity {
    protected void addExtraItem(GroupedSource groupedSource) {
        Map<String, String> customeSection = SourceDB.buildSource(TikaConstants.TYPE_SINA_WEIBO,
                getString(R.string.add_custom_source),
                Constants.ADD_CUSTOME_SOURCE,
                getString(R.string.add_custom_source_desc), null, this.getString(R.string.custom));

        groupedSource.addGroup(SourceRepo.KEY_NAME_GROUP,this.getString(R.string.custom));
        groupedSource.addChild(this.getString(R.string.custom), customeSection);
    }

    public void doWithAddCustomerSouce() {
        startActivity(new Intent(this, SinaSourceSearchActivity.class));
        finish();
    }


}