package com.goal98.flipdroid.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.goal98.android.WebImageView;
import com.goal98.flipdroid.model.Article;

public abstract class ArticleView extends LinearLayout {

    protected Article article;

    protected TextView titleView;
    protected TextView authorView;
    protected TextView contentView;
    protected TextView createDateView;
    protected WebImageView portraitView;
    protected LinearLayout contentViewWrapper;

    protected WeiboPageView pageView;
    public PaintFlagsDrawFilter pfd;
    protected boolean placedAtBottom;

    public WeiboPageView getPageView() {
        return pageView;
    }

    public View getContentView() {
        return contentViewWrapper;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public Article getArticle() {
        return article;
    }

    protected abstract String getPrefix();



    public ArticleView(Context context, Article article, WeiboPageView pageView, boolean placedAtBottom) {
        super(context);
        this.setOrientation(VERTICAL);
this.placedAtBottom= placedAtBottom;
        pfd = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG);

        this.pageView = pageView;
        setArticle(article);

        buildView();
    }

    public abstract void buildView();

    public abstract void renderBeforeLayout();


}
