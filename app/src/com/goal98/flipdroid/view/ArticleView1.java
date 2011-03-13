package com.goal98.flipdroid.view;

import android.content.Context;
import android.widget.LinearLayout;
import com.goal98.flipdroid.model.Article;

/**
 * Created by IntelliJ IDEA.
 * User: jleo
 * Date: 2/17/11
 * Time: 10:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class ArticleView1 extends ArticleView {
    public ArticleView1(Context context) {
        super(context);
    }

    public ArticleView1(Context context, Article article) {
        super(context, article);
    }

    protected void buildView() {

        LinearLayout titleLL = new LinearLayout(this.getContext());
        titleLL.setOrientation(HORIZONTAL);
        titleLL.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT,80));
        titleLL.setBaselineAligned(false);

        LinearLayout iconView = new LinearLayout(this.getContext());
//        iconView.setLayoutParams();
        iconView.addView(portraitView, new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.FILL_PARENT, 30));

        LinearLayout titleBarView = new LinearLayout(this.getContext());
//        titleBarView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.FILL_PARENT, 70));
        titleBarView.addView(titleView, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.FILL_PARENT, 70));

        titleLL.addView(iconView);
        titleLL.addView(titleBarView);

        LinearLayout contentLL = new LinearLayout(this.getContext());
        contentLL.setBaselineAligned(false);
        contentLL.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT, 20));
        contentView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT,1));
        contentLL.addView(contentView, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        this.setOrientation(VERTICAL);
        this.addView(titleLL);
        this.addView(contentLL);

        this.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.FILL_PARENT,1));
        this.setBaselineAligned(false);
    }
}
