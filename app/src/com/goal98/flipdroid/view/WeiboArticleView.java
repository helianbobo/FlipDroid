package com.goal98.flipdroid.view;

import android.content.Context;
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

    protected String getPrefix() {
        return Constants.WITHURLPREFIX;
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//
//        if (handler == null)
//            handler = new Handler();
//
//        if (!isLoading && !this.getPageView().loadingNext && article.hasLink() && event.getAction() == MotionEvent.ACTION_UP) {
//            if (enlargedView != null) {//以前打开过的，直接显示
//                WeiboArticleView.this.getPageView().enlarge(loadedArticleView, WeiboArticleView.this);
//                return true;
//            }
//
//            if (future.isDone()) { //如果加载好了，直接显示
//                enlargeLoadedView();
//                return true;
//            }
//
//
//            fadeOutAni.setAnimationListener(new Animation.AnimationListener() {
//                public void onAnimationStart(Animation animation) {
//                }
//
//                public void onAnimationEnd(Animation animation) {
//                    fadeOutAni.setAnimationListener(null);
//                    switcher.setDisplayedChild(1);
//                    new Thread(new Runnable() {
//                        public void run() {
//                            enlargeLoadedView();
//                        }
//                    }).start();
//                }
//
//                public void onAnimationRepeat(Animation animation) {
//                }
//            });
//            WeiboArticleView.this.contentViewWrapper.startAnimation(fadeOutAni);
//            return true;
//        }
//        if (event.getAction() == MotionEvent.ACTION_DOWN)//don't swallow action down event,or PageActivity won't handle it
//            return true;
//
//        return false;
//    }

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
//        this.setBackgroundColor(0xff000000);//分割线颜色
//        progressBar = (LinearLayout) inflater.inflate(R.layout.progressbar, null);
        switcher = (ViewSwitcher) inflater.inflate(R.layout.weibo_article_view, null);
        switcher.setDisplayedChild(0);
        contentView = (TextView) switcher.findViewById(R.id.content);
        contentView.setText(getPrefix() + article.getStatus());

        this.titleView = (TextView) switcher.findViewById(R.id.title);
        this.authorView = (TextView) switcher.findViewById(R.id.author);
        this.createDateView = (TextView) switcher.findViewById(R.id.createDate);
        this.contentViewWrapper = (LinearLayout) switcher.findViewById(R.id.contentll);

        this.portraitView = (WebImageView) switcher.findViewById(R.id.portrait2);

        authorView.setText(article.getAuthor());
        createDateView.setText(PrettyTimeUtil.getPrettyTime(this.getContext(), article.getCreatedDate()));

        if (article.getPortraitImageUrl() != null)
            portraitView.setImageUrl(article.getPortraitImageUrl().toString());

        addOnClickListener();
        LayoutParams layoutParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

        this.addView(switcher, layoutParams);
    }

    public void renderBeforeLayout() {
        portraitView.loadImage();
    }


}