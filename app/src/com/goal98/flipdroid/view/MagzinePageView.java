package com.goal98.flipdroid.view;


import android.view.Gravity;
import android.widget.LinearLayout;
import com.goal98.flipdroid.activity.PageActivity;
import com.goal98.flipdroid.model.Article;
import com.goal98.flipdroid.util.DeviceInfo;

public class MagzinePageView extends WeiboPageView {
    public LinearLayout addArticleView(Article article, boolean last) {
        final ThumbnailArticleView thumnnailView = new ThumbnailArticleView(MagzinePageView.this.getContext(), article, this,last);
        weiboViews.add(thumnnailView);

        if (!last) {
            LinearLayout articleWrapper = new LinearLayout(this.getContext());
            articleWrapper.setOrientation(LinearLayout.VERTICAL);
            articleWrapper.setBackgroundColor(0xffDDDDDD);//分割线颜色
            LayoutParams layoutParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
            articleWrapper.addView(thumnnailView, layoutParams);
            articleWrapper.setGravity(Gravity.TOP);
            articleWrapper.setPadding(0, 0, 0, 1);
            wrapperViews.add(articleWrapper);
            LayoutParams wrapperLayoutParams = new LayoutParams(DeviceInfo.width, DeviceInfo.displayHeight / 2);

            contentLayout.addView(articleWrapper, wrapperLayoutParams);

        } else {
            wrapperViews.add(thumnnailView);
            LayoutParams layoutParams = new LayoutParams(DeviceInfo.width, LayoutParams.MATCH_PARENT);
            contentLayout.addView(thumnnailView, layoutParams);
        }
        return thumnnailView;
    }

    public MagzinePageView(PageActivity pageActivity) {
        super(pageActivity);
    }
}
