package com.goal98.flipdroid.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.model.Article;

import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class ArticleView extends LinearLayout {

    protected Article article;

    protected TextView titleView;
    protected TextView statusView;
    protected TextView authorView;
    protected TextViewMultilineEllipse contentView;
    protected TextView createDateView;
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
            titleView.setTextSize(18);
            titleView.setTextColor(0xff1A1A1A);
            titleView.setTypeface(Typeface.DEFAULT_BOLD);
            titleView.setText(article.getTitle());
            titleView.setGravity(Gravity.CENTER);
        }
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
            contentView.setText(article.getContent());
            contentView.setMaxLines(16);
            contentView.setTextSize(14);
            contentView.setPadding(2, 10, 2, 10);
            contentView.setTextColor(0xff232323);
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
            portraitView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            portraitView.setAdjustViewBounds(true);
            portraitView.setMaxHeight(20);
        }

        if (createDateView == null) {
            createDateView = new TextView(this.getContext());
            createDateView.setText(transformCreatedDate(article.getCreatedDate()));
            createDateView.setTextSize(16);
            createDateView.setPadding(2, 2, 2, 2);
        }
        if (statusView == null) {
            statusView = new TextView(this.getContext());
            statusView.setTextSize(18);
            statusView.setTypeface(Typeface.SERIF);
            statusView.setText(article.getStatus());
        }
    }

    protected abstract String getPrefix();

    private String transformCreatedDate(Date createdDate) {
        if (createdDate == null)
            return "";

        return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(createdDate);
    }

    public ArticleView(Context context) {
        super(context);
    }

    public ArticleView(Context context, Article article) {
        super(context);
        setArticle(article);

        buildView();
    }

    protected abstract void buildView();

    public void enlarge() {
       contentView.invalidate();
       contentView.requestLayout();
    }
}
