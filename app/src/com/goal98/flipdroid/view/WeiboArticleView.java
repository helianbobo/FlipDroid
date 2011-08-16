package com.goal98.flipdroid.view;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import com.goal98.android.WebImageView;
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.model.Article;
import com.goal98.flipdroid.util.Constants;
import com.goal98.flipdroid.util.PrettyTimeUtil;

import java.net.URL;
import java.util.Random;
import java.util.concurrent.*;

//import com.goal98.flipdroid.client.TikaClient;
//import com.goal98.flipdroid.client.TikaClientException;
//import com.goal98.flipdroid.client.TikaExtractResponse;

/**
 * Created by IntelliJ IDEA.
 * User: jleo
 * Date: 2/17/11
 * Time: 10:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class WeiboArticleView extends ExpandableArticleView {
    public WeiboArticleView(Context context, Article article, WeiboPageView pageView, boolean placedAtBottom, ExecutorService executor) {
        super(context, article, pageView, placedAtBottom, executor);
    }

    public void setText() {
        contentView.setText(article.getStatus());
    }

    protected String getPrefix() {
        return Constants.WITHURLPREFIX;
    }

    protected void reloadOriginalView() {
        switcher.setDisplayedChild(0);
        switcher.getChildAt(0).setVisibility(INVISIBLE);
        fadeInAni.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {

            }

            public void onAnimationEnd(Animation animation) {
                switcher.getChildAt(0).setVisibility(VISIBLE);
            }

            public void onAnimationRepeat(Animation animation) {

            }
        });
        WeiboArticleView.this.contentView.startAnimation(fadeInAni);
    }

    protected ExecutorService executor;

    public void buildView() {


        LayoutInflater inflater = LayoutInflater.from(this.getContext());


        switcher = (ViewSwitcher) inflater.inflate(R.layout.weibo_article_view, null);
        switcher.setDisplayedChild(0);


        this.titleView = (TextView) switcher.findViewById(R.id.title);
        this.authorView = (TextView) switcher.findViewById(R.id.author);
        this.createDateView = (TextView) switcher.findViewById(R.id.createDate);
        this.contentViewWrapper = (LinearLayout) switcher.findViewById(R.id.contentll);

//        buildImageAndContent();
        this.portraitView = (WebImageView) switcher.findViewById(R.id.portrait2);

        authorView.setText(article.getAuthor());
        createDateView.setText(PrettyTimeUtil.getPrettyTime(this.getContext(), article.getCreatedDate()));

        if (article.getPortraitImageUrl() != null)
            portraitView.setImageUrl(article.getPortraitImageUrl().toString());

        addOnClickListener();

        LayoutParams switcherLayoutParam = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        this.addView(switcher, switcherLayoutParam);
    }

    public void renderBeforeLayout() {
        if (handler == null)
            handler = new Handler();

        portraitView.loadImage();
        new Thread(new Runnable() {
            public void run() {
                try {
                    isLoading = true;
                    handler.post(new Runnable() {
                        public void run() {
                            buildImageAndContent();
                        }

                    });
                } finally {
                    isLoading = false;
                }

            }
        }).start();
    }
}