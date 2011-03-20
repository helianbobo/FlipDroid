package com.goal98.flipdroid.view;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.goal98.flipdroid.model.Article;

/**
 * Created by IntelliJ IDEA.
 * User: jleo
 * Date: 2/17/11
 * Time: 10:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class ArticleWithURLView extends ArticleView {
    public ArticleWithURLView(Context context) {
        super(context);
    }

    public ArticleWithURLView(Context context, Article article) {
        super(context, article);
    }

    protected String getPrefix(){
      return "        ";
    }

    protected void buildView() {
        this.setBackgroundColor(0xffF7F7F7);
        this.setPadding(8,0,8,0);
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

        TextView sharedByView = new TextView(this.getContext());
        sharedByView.setText("Shared by");
        sharedByView.setPadding(2, 2, 5, 2);
        sharedByView.setTextSize(14);
        sharedByView.setTextColor(0xffAAAAAA);
        publisherView.addView(sharedByView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.FILL_PARENT));
        publisherView.addView(portraitView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.FILL_PARENT));
        publisherView.addView(super.authorView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT));

        LinearLayout contentLL = new LinearLayout(this.getContext());
        contentLL.setBaselineAligned(false);
        contentView.setText(getPrefix() + article.getContent());
        contentView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.FILL_PARENT,1));
        contentLL.addView(contentView, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.FILL_PARENT, 1));
        this.setOrientation(VERTICAL);

        noneTitle.addView(publisherView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.FILL_PARENT, 85));
        noneTitle.addView(contentLL, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.FILL_PARENT, 15));

        this.addView(titleLL,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT,75));
        this.addView(noneTitle,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT, 25));

        this.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.FILL_PARENT,1));
        this.setBaselineAligned(false);
    }
}
