package com.goal98.flipdroid.view;

import android.util.Log;
import com.goal98.flipdroid.activity.FlipdroidApplications;
import com.goal98.flipdroid.activity.PageActivity;
import com.goal98.flipdroid.model.Article;
import com.goal98.flipdroid.util.Constants;
import com.goal98.flipdroid.util.DeviceInfo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class SmartPage extends Page {
    private List<Integer> heights = new ArrayList();

    public SmartPage(PageActivity activity) {
        super(activity);
        this.articleList = new LinkedList<Article>();
    }



    boolean addResult;

    public boolean addArticle(final Article article) {
        DimensionMeasureTool dmt = new DimensionMeasureTool(deviceInfo);
        dmt.setText(Constants.WITHURLPREFIX + article.getStatus());
        dmt.setTextSize(17);
        dmt.setMaxLines(18);
        int height = dmt.onMeasure()[1] + 29;
        DryRunResult result = overflowIfPut(height);
        if (!result.overflow) {
            article.setHeight(height);
            articleList.add(article);
            heights.add(result.height);
            heightSum += result.height;
            addResult = true;
        } else {
            addResult = false;
        }
        return addResult;
    }

    private DryRunResult overflowIfPut(int height) {
        int dryRunSumHeight = heightSum;

        //Log.d("height measure single", height + "");
        DryRunResult result = new DryRunResult();
        result.height = height;
        dryRunSumHeight += height;
        //Log.d("height measure sum", dryRunSumHeight + "");
        if (dryRunSumHeight > deviceInfo.getDisplayHeight()) {
            result.overflow = true;
        } else {
            result.overflow = false;
        }
        return result;
    }

    //must be called from UI thread
    public WeiboPageView getWeiboPageView() {
        weiboPageView.setPage(this);
        return weiboPageView;
    }

    class DryRunResult {
        boolean overflow;
        int height;
    }
}
