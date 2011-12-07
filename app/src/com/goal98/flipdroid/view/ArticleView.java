package com.goal98.flipdroid.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import com.goal98.android.WebImageView;
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.activity.FlipdroidApplications;
import com.goal98.flipdroid.activity.PageActivity;
import com.goal98.flipdroid.model.Article;
import com.goal98.flipdroid.util.AlarmSender;
import com.goal98.flipdroid.util.DeviceInfo;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public abstract class ArticleView extends LinearLayout {

    protected Article article;

    protected TextView titleView;
    protected TextView authorView;
    protected TextView contentView;
    protected TextView createDateView;
    protected WebImageView portraitView;
    protected LinearLayout contentViewWrapper;
    protected boolean toLoadImage;


    protected WeiboPageView pageView;
    public PaintFlagsDrawFilter pfd;
    protected boolean placedAtBottom;
    protected DeviceInfo deviceInfo;

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

    public DeviceInfo getDeviceInfoFromApplicationContext(){
        return DeviceInfo.getInstance((Activity) this.getContext());
    }

    public ArticleView(Context context, Article article, WeiboPageView pageView, boolean placedAtBottom) {
        super(context);
        toLoadImage = ((PageActivity)context).isToLoadImage();
        this.deviceInfo = getDeviceInfoFromApplicationContext();
        this.setOrientation(VERTICAL);
        this.placedAtBottom = placedAtBottom;
        pfd = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG);

        this.pageView = pageView;
        setArticle(article);

        buildView();
    }

    public abstract void buildView();

    public abstract void renderBeforeLayout();


}
