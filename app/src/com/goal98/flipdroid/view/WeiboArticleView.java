package com.goal98.flipdroid.view;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import com.goal98.android.ImageLoader;
import com.goal98.android.WebImageView;
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.client.TikaClient;
import com.goal98.flipdroid.client.TikaExtractResponse;
import com.goal98.flipdroid.model.Article;
import com.goal98.flipdroid.model.PreloadImageLoaderHandler;
import com.goal98.flipdroid.model.cachesystem.CacheSystem;
import com.goal98.flipdroid.util.AlarmSender;
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
public class WeiboArticleView extends ArticleView {
    protected ArticleView loadedArticleView;
    public LinearLayout enlargedView;
    protected ViewSwitcher switcher;

    public WeiboArticleView(Context context, Article article, WeiboPageView pageView, boolean placedAtBottom) {
        super(context, article, pageView,placedAtBottom);
        executor = Executors.newFixedThreadPool(1);
        preload();
    }

    protected String getPrefix() {
        return Constants.WITHURLPREFIX;
    }

    protected final Animation fadeOutAni = AnimationUtils.loadAnimation(this.getContext(), R.anim.fade);
    protected final Animation fadeInAni = AnimationUtils.loadAnimation(this.getContext(), R.anim.fadein);

    volatile boolean isLoading = false;

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    protected Handler handler;


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (handler == null)
            handler = new Handler();

        if (!isLoading && !this.getPageView().loadingNext && article.hasLink() && event.getAction() == MotionEvent.ACTION_UP) {
            if (enlargedView != null) {//以前打开过的，直接显示
                WeiboArticleView.this.getPageView().enlarge(loadedArticleView, WeiboArticleView.this);
                return true;
            }

            if (future.isDone()) { //如果加载好了，直接显示
                enlargeLoadedView();
                return true;
            }


            fadeOutAni.setAnimationListener(new Animation.AnimationListener() {
                public void onAnimationStart(Animation animation) {
                }

                public void onAnimationEnd(Animation animation) {
                    fadeOutAni.setAnimationListener(null);
                    switcher.setDisplayedChild(1);
                    new Thread(new Runnable() {
                        public void run() {
                            enlargeLoadedView();
                        }
                    }).start();
                }

                public void onAnimationRepeat(Animation animation) {
                }
            });
            WeiboArticleView.this.contentViewWrapper.startAnimation(fadeOutAni);
            return true;
        }
        if (event.getAction() == MotionEvent.ACTION_DOWN)//don't swallow action down event,or PageActivity won't handle it
            return true;

        return false;
    }

    protected void enlargeLoadedView() {

        try {
            loadedArticleView = new ContentLoadedView(this.getContext(), future.get(), pageView);


            handler.post(new Runnable() {
                public void run() {
                    switcher.setDisplayedChild(0);
                    WeiboArticleView.this.getPageView().enlarge(loadedArticleView, WeiboArticleView.this);
                }
            });
        } catch (InterruptedException e) {
            handler.post(new Runnable() {
                public void run() {
                    AlarmSender.sendInstantMessage(R.string.tikatimeout, WeiboArticleView.this.getContext());
                    reloadOriginalView();
                }
            });
        } catch (ExecutionException e) {
            e.printStackTrace();
            handler.post(new Runnable() {
                public void run() {
                    AlarmSender.sendInstantMessage(R.string.tikaservererror, WeiboArticleView.this.getContext());
                    reloadOriginalView();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            handler.post(new Runnable() {
                public void run() {
                    AlarmSender.sendInstantMessage(R.string.unknownerror, WeiboArticleView.this.getContext());
                    reloadOriginalView();
                }
            });
        }
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

    protected Future<Article> future;

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

        contentView.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (handler == null)
                    handler = new Handler();
                if (!isLoading && !WeiboArticleView.this.getPageView().loadingNext && article.hasLink()) {

                    if (enlargedView != null) {//以前打开过的，直接显示
                        WeiboArticleView.this.getPageView().enlarge(loadedArticleView, WeiboArticleView.this);
                        return;
                    }

                    if (future.isDone()) { //如果加载好了，直接显示
                        enlargeLoadedView();
                        return;
                    }

                    isLoading = true;
                    fadeOutAni.setAnimationListener(new Animation.AnimationListener() {
                        public void onAnimationStart(Animation animation) {
                        }

                        public void onAnimationEnd(Animation animation) {
                            fadeOutAni.setAnimationListener(null);
                            switcher.setDisplayedChild(1);
                            new Thread(new Runnable() {
                                public void run() {
                                    enlargeLoadedView();
                                }
                            }).start();
                        }

                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                    WeiboArticleView.this.contentViewWrapper.startAnimation(fadeOutAni);
                    return;
                }
                return;
            }
        });
        LayoutParams layoutParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

        this.addView(switcher, layoutParams);
    }

    public void renderBeforeLayout() {
        portraitView.loadImage();
    }

    public void preload() {
        if (article.hasLink()) {
            isLoading = true;
            future = executor.submit(new Callable() {
                public Object call() throws Exception {
                    try {
                        String url = article.extractURL();
                        //Log.d("Weibo view", "preloading " + url);

                        TikaExtractResponse loadedTikeExtractResponse = CacheSystem.getTikaCache().load(new URL(url));
                        if (loadedTikeExtractResponse != null) {
                            return loadedTikeExtractResponse;
                        } else {
                            TikaClient tc = new TikaClient();
                            TikaExtractResponse extractResponse = null;
                            try {
                                extractResponse = tc.extract(url);
                            } catch (Exception e) {
                                e.printStackTrace();
                                return article;
                            }
                            //Log.d("Weibo view", "preloading " + url + " done");
                            if (extractResponse.getContent() != null)
                                article.setContent(extractResponse.getContent().replaceFirst("\n", "\n      "));
                            else
                                article.setContent("");
                            article.setTitle(extractResponse.getTitle());


                            try {
                                if (!extractResponse.getImages().isEmpty()) {
                                    String image = extractResponse.getImages().get(0);
                                    article.setImageUrl(new URL(image));
                                    PreloadImageLoaderHandler preloadImageLoaderHandler = new PreloadImageLoaderHandler(article);
//                                    preloadImageLoaderHandler.setWidth(DeviceInfo.width/2-32);
//                                    preloadImageLoaderHandler.setHeight(DeviceInfo.height/2-100);

                                    final ImageLoader loader = new ImageLoader(image, preloadImageLoaderHandler);
                                    new Thread(new Runnable(){
                                        public void run() {
                                            loader.run();
                                        }
                                    }).start();

                                }

                            } catch (Exception e) {
                                 e.printStackTrace();
                            }
                            return article;
                        }
                    } finally {
                        isLoading = false;
                    }

                }
            });
        }
    }
}