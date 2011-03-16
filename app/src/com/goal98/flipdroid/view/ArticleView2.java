package com.goal98.flipdroid.view;

import android.content.Context;
import android.widget.LinearLayout;
import com.goal98.flipdroid.model.Article;

/**
 * Created by IntelliJ IDEA.
 * User: jleo
 * Date: 2/17/11
 * Time: 10:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class ArticleView2 extends ArticleView {
    public ArticleView2(Context context) {
        super(context);
    }

    public ArticleView2(Context context, Article article) {
        super(context, article);
    }

    protected void buildView() {
        LinearLayout titleLL = new LinearLayout(this.getContext());
        titleLL.setOrientation(HORIZONTAL);
        titleLL.setBaselineAligned(false);

        LinearLayout titleBarView = new LinearLayout(this.getContext());
        titleBarView.addView(titleView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));

        //titleLL.addView(iconView);
        titleLL.addView(titleBarView);

        LinearLayout noneTitle = new LinearLayout(this.getContext());
        noneTitle.setOrientation(VERTICAL);
        noneTitle.setBaselineAligned(false);

        LinearLayout publisherView = new LinearLayout(this.getContext());
        publisherView.addView(portraitView, new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.FILL_PARENT, 15));
        publisherView.addView(super.authorView, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.FILL_PARENT, 85));

        LinearLayout contentLL = new LinearLayout(this.getContext());
        contentLL.setBaselineAligned(false);
        contentView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT,1));
        contentLL.addView(contentView, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        this.setOrientation(VERTICAL);

        noneTitle.addView(publisherView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.FILL_PARENT, 85));
        noneTitle.addView(contentLL, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.FILL_PARENT, 15));

        this.addView(titleLL,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT,80));
        this.addView(noneTitle,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT, 20));

        this.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.FILL_PARENT,1));
        this.setBaselineAligned(false);
    }
}
