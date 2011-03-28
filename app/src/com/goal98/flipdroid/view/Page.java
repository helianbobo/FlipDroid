package com.goal98.flipdroid.view;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import com.goal98.flipdroid.activity.IndexActivity;
import com.goal98.flipdroid.model.Article;
import com.goal98.flipdroid.util.Constants;
import com.goal98.flipdroid.view.ArticleView;
import com.goal98.flipdroid.view.ArticleWithoutURLView;
import com.goal98.flipdroid.view.PageView;

import java.util.LinkedList;
import java.util.List;

public class Page {
    private int heightSum;

    private PageView pageView;

    private List<Article> articleList;
    private Activity activity;

    public Page(Activity activity) {
        this.articleList = new LinkedList<Article>();

        pageView = new PageView(activity);
        this.activity = activity;
    }

    public List<Article> getArticleViewList() {
        return articleList;
    }


    public void settle() {
        pageView.centerAll();
    }

    boolean addResult;

    public boolean addArticle(final Article article) {
        DimensionMeasureTool dmt = new DimensionMeasureTool();
        dmt.setText(Constants.WITHURLPREFIX + article.getStatus());
        dmt.setTextSize(17);
        dmt.setMaxLines(18);
        int height = dmt.onMeasure()[1] + 29;
        DryRunResult result = overflowIfPut(height);
        if (!result.overflow) {
            articleList.add(article);
            heightSum += result.height;
            addResult = true;
        } else {
            addResult = false;
        }
        return addResult;
    }

    public DryRunResult overflowIfPut(int height) {
        int dryRunSumHeight = heightSum;
        //int dryRunSumHeight2 = 0;

        //int height = articleWrapper.getMeasuredHeight();

        // Log.d("height measure sum2", dryRunSumHeight2 + "");
        Log.d("height measure single", height + "");
        DryRunResult result = new DryRunResult();
        result.height = height;
        dryRunSumHeight += height;
        Log.d("height measure sum", dryRunSumHeight + "");
        if (dryRunSumHeight > IndexActivity.maxHeight) {
            result.overflow = true;
        } else {
            result.overflow = false;
        }
        return result;
    }

    //must be called from UI thread
    public PageView getPageView() {
        pageView.setPage(this);
        return pageView;
    }

    class DryRunResult {
        boolean overflow;
        int height;
    }
}
