package com.goal98.flipdroid.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.goal98.android.WebImageView;
import com.goal98.flipdroid.activity.PageActivity;
import com.goal98.flipdroid.model.Article;
import com.goal98.flipdroid.util.DeviceInfo;

public abstract class ArticleView extends LinearLayout {

    protected Article article;

    protected TextView titleView;
    protected TextView authorView;
    protected TextView contentView;
    protected TextView createDateView;
    protected WebImageView portraitView;
    protected LinearLayout contentViewWrapper;
    protected boolean toLoadImage;


    protected ThumbnailViewContainer pageViewContainer;

    protected boolean placedAtBottom;
    protected DeviceInfo deviceInfo;


    public ThumbnailViewContainer getPageViewContainer() {
        return pageViewContainer;
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

    public DeviceInfo getDeviceInfoFromApplicationContext(){
        return DeviceInfo.getInstance((Activity) this.getContext());
    }

    public ArticleView(Context context, Article article, ThumbnailViewContainer pageViewContainer, boolean placedAtBottom) {
        super(context);
        pfd = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
        toLoadImage = ((PageActivity)context).isToLoadImage();
        this.deviceInfo = getDeviceInfoFromApplicationContext();
        this.setOrientation(VERTICAL);
        this.placedAtBottom = placedAtBottom;


        this.pageViewContainer = pageViewContainer;
        setArticle(article);

        buildView();
    }



    public abstract void buildView();

    public abstract void renderBeforeLayout();

    private PaintFlagsDrawFilter pfd;

    protected void dispatchDraw(android.graphics.Canvas canvas) {
        canvas.setDrawFilter(pfd);
        super.dispatchDraw(canvas);
    }
}
