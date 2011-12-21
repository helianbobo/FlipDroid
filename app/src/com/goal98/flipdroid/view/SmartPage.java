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
        int height = 0;

        DimensionMeasureTool dmt = new DimensionMeasureTool(deviceInfo);
        dmt.setText(Constants.WITHURLPREFIX + article.getStatus());
        if (!deviceInfo.isLargeScreen()) {
            dmt.setTextSize(Constants.WEIBO_CONENT_TEXT_SIZE);
            dmt.setMaxLines(18);
            height = dmt.onMeasure()[1] + 29;
        } else {
            dmt.setTextSize(Constants.WEIBO_CONENT_TEXT_SIZE);
            dmt.setMaxLines(18);
            height = dmt.onMeasure()[1] + 130;
        }

        if (article.getImageUrl() != null) {
            article.setTextHeight(height);
            height = deviceInfo.getDisplayHeight() - 1;
        }
        System.out.println("view height" + height);
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

        DryRunResult result = new DryRunResult();
        result.height = height;
        dryRunSumHeight += height;

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
