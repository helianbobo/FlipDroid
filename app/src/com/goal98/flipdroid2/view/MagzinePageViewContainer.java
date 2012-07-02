package com.goal98.flipdroid2.view;


import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import com.goal98.flipdroid2.activity.PageActivity;
import com.goal98.flipdroid2.model.Article;
import com.goal98.flipdroid2.util.Constants;

public class MagzinePageViewContainer extends ThumbnailViewContainer {
    public View addArticleView(Article article, boolean last) {
        final ThumbnailArticleView thumnnailView = new ThumbnailArticleView(MagzinePageViewContainer.this.getContext(), article, this,last,executor);
        weiboViews.add(thumnnailView);

        if (!last) {
            LinearLayout articleWrapper = new LinearLayout(this.getContext());
            articleWrapper.setOrientation(LinearLayout.VERTICAL);
            articleWrapper.setBackgroundColor(Constants.LINE_COLOR);//分割线颜色
            LayoutParams layoutParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
            articleWrapper.addView(thumnnailView, layoutParams);
            articleWrapper.setGravity(Gravity.TOP);
            articleWrapper.setPadding(0, 0, 0, 1);
            wrapperViews.add(articleWrapper);
            LayoutParams wrapperLayoutParams = new LayoutParams(deviceInfo.getWidth(), deviceInfo.getDisplayHeight() / 2);

            addView(articleWrapper, wrapperLayoutParams);

        } else {
            wrapperViews.add(thumnnailView);
            LayoutParams layoutParams = new LayoutParams(deviceInfo.getWidth(), LayoutParams.MATCH_PARENT);
            addView(thumnnailView, layoutParams);
        }
        return thumnnailView;
    }

    public MagzinePageViewContainer(PageActivity pageActivity) {
        super(pageActivity);
    }
}
