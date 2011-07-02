package com.goal98.flipdroid.view;


import android.widget.LinearLayout;
import com.goal98.flipdroid.activity.PageActivity;
import com.goal98.flipdroid.model.Article;
import com.goal98.flipdroid.util.DeviceInfo;

public class MagzinePageView extends WeiboPageView {
    public LinearLayout addArticleView(Article article, boolean last) {
        final ThumbnailArticleView thumnnailView = new ThumbnailArticleView(MagzinePageView.this.getContext(), article, this,last);
        weiboViews.add(thumnnailView);
        wrapperViews.add(thumnnailView);
        if (!last) {
            LayoutParams layoutParams = new LayoutParams(DeviceInfo.width, DeviceInfo.displayHeight / 2);
            contentLayout.addView(thumnnailView, layoutParams);
        } else {
            LayoutParams layoutParams = new LayoutParams(DeviceInfo.width, LayoutParams.MATCH_PARENT);
            contentLayout.addView(thumnnailView, layoutParams);
        }
        return thumnnailView;
    }

    public MagzinePageView(PageActivity pageActivity) {
        super(pageActivity);
    }
}
