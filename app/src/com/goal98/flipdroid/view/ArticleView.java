package com.goal98.flipdroid.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.model.Article;
import weibo4j.Version;

public class ArticleView extends LinearLayout {

    private Article article;

    private TextView titleView;
    private TextViewMultilineEllipse contentView;
    private InternetImageView portraitView;

    public void setArticle(Article article) {
        this.article = article;
        renderView();
    }

    private void renderView() {
        if (titleView == null) {
            titleView = new TextView(getContext());
            titleView.setPadding(2, 2, 2, 2);
            titleView.setTextSize(16);
            //titleView.
        }
        titleView.setText(article.getTitle());

        if (contentView == null) {
            contentView = new TextViewMultilineEllipse(getContext());
            contentView.setEllipsis("...");
        contentView.setEllipsisMore("More!");
        contentView.setText(article.getContent());
        contentView.setMaxLines(6);
        contentView.setPadding(10, 10, 10, 10);
        contentView.setBackgroundColor(0xFFE4BEF1);
            contentView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
            //contentView.setSingleLine(false);
            //contentView.setGravity(Gravity.LEFT|Gravity.TOP);
            //contentView.setWidth(1);
            //contentView.setMaxLines(4);
        }
        //contentView.setText(article.getContent());

        if (portraitView == null) {
            portraitView = new InternetImageView(getContext(), article.getPortraitImageUrl(), 2);
            portraitView.setImageResource(R.drawable.portrait_small);
            portraitView.setPadding(2, 2 ,2 ,2);
        }

    }

    public ArticleView(Context context) {
        super(context);
        this.setBaselineAligned(false);
    }

    public ArticleView(Context context, Article article) {
        super(context);
        this.setBaselineAligned(false);
        setArticle(article);

        buildView();
    }


    public ArticleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        buildView();
    }

    private void buildView() {

        LinearLayout titleLL = new LinearLayout(this.getContext());
        titleLL.setOrientation(HORIZONTAL);
        titleLL.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT,80));
        titleLL.setBaselineAligned(false);

        LinearLayout iconView = new LinearLayout(this.getContext());
        iconView.setLayoutParams(new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.FILL_PARENT, 30));
        iconView.addView(portraitView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));

        LinearLayout titleBarView = new LinearLayout(this.getContext());
        titleBarView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.FILL_PARENT, 70));
        titleBarView.addView(titleView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));

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
