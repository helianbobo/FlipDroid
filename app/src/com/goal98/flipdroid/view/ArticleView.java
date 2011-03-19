package com.goal98.flipdroid.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.model.Article;

public abstract class ArticleView extends LinearLayout {

    private Article article;

    protected TextView titleView;
    protected TextView authorView;
    protected TextViewMultilineEllipse contentView;
    protected InternetImageView portraitView;
    protected InternetImageView illustrationView;

    public void setArticle(Article article) {
        this.article = article;
        renderView();
    }

    String indent = "    ";
    private void renderView() {
        if (titleView == null) {
            titleView = new TextView(getContext());
            titleView.setPadding(2, 2, 2, 5);
            titleView.setTextSize(16);
            titleView.setTextColor(0xff1A1A1A);
            titleView.setTypeface(Typeface.DEFAULT_BOLD);
            //titleView.
        }
        titleView.setText(article.getTitle());

        if (authorView == null) {
            authorView = new TextView(getContext());
            authorView.setPadding(2, 2, 2, 2);
            authorView.setText(article.getAuthor());
            authorView.setTextSize(14);
        }

        if (contentView == null) {
            contentView = new TextViewMultilineEllipse(getContext());
            contentView.setEllipsis("...");
            contentView.setEllipsisMore("More!");
            contentView.setText(indent + article.getContent());
            contentView.setMaxLines(7);
            contentView.setTextSize(14);
            contentView.setPadding(2, 10, 2, 10);
            contentView.setTextColor(0xff232323);
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
            portraitView.setPadding(2, 2, 2, 2);
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

    protected abstract void buildView();
}
