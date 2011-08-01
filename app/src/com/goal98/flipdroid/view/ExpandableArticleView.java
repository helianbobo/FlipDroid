package com.goal98.flipdroid.view;

import android.content.Context;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ViewSwitcher;
import com.goal98.android.ImageLoader;
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.client.TikaClient;
import com.goal98.flipdroid.client.TikaExtractResponse;
import com.goal98.flipdroid.model.Article;
import com.goal98.flipdroid.model.PreloadImageLoaderHandler;
import com.goal98.flipdroid.model.cachesystem.CacheSystem;
import com.goal98.flipdroid.model.cachesystem.TikaCache;
import com.goal98.flipdroid.util.AlarmSender;
import com.goal98.flipdroid.util.Constants;

import java.net.URL;
import java.util.concurrent.*;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 7/16/11
 * Time: 3:48 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class ExpandableArticleView extends ArticleView {
    protected ViewSwitcher switcher;
    protected ArticleView loadedArticleView;
    protected LinearLayout enlargedView;
    protected final Animation fadeOutAni = AnimationUtils.loadAnimation(this.getContext(), R.anim.fade);
    protected final Animation fadeInAni = AnimationUtils.loadAnimation(this.getContext(), R.anim.fadein);
    protected Handler handler;
    protected Future<Article> future;
    public volatile boolean isLoading = false;
    protected ExecutorService executor;
    private TikaCache tikaCache;

    public ExpandableArticleView(Context context, Article article, WeiboPageView pageView, boolean placedAtBottom, ExecutorService executor) {
        super(context, article, pageView, placedAtBottom);
        if (!article.isAlreadyLoaded()) {
            this.executor = executor;
            preload();
        } else {
            if (toLoadImage(context))
                article.loadImage();
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
    }

    public void preload() {
        if (article.hasLink()) {
            isLoading = true;
            future = executor.submit(new Callable() {
                public Object call() throws Exception {
                    try {
                        String url = article.extractURL();
                        //Log.d("Weibo view", "preloading " + url);

                        tikaCache = CacheSystem.getTikaCache(ExpandableArticleView.this.getContext());
                        TikaExtractResponse loadedTikeExtractResponse = tikaCache.load(new URL(url));
                        if (loadedTikeExtractResponse != null) {
                            responseToArticle(loadedTikeExtractResponse);
                            return article;
                        } else {
                            TikaClient tc = new TikaClient(Constants.TIKA_HOST);
                            TikaExtractResponse extractResponse = null;
                            try {
                                extractResponse = tc.extract(url);
                            } catch (Exception e) {
                                e.printStackTrace();
                                return article;
                            }
                            //Log.d("Weibo view", "preloading " + url + " done");
                            responseToArticle(extractResponse);
                            if (extractResponse.getContent() != null && extractResponse.getContent().trim().length() != 0)
                                tikaCache.put(url, extractResponse);
                            return article;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        return article;
                    } finally {
                        isLoading = false;
                    }
                }
            });
        }
    }

    private void responseToArticle(TikaExtractResponse extractResponse) {
        if (extractResponse.getContent() != null)
            article.setContent(extractResponse.getContent().replaceFirst("\n", "\n      "));
        else
            article.setContent("");
        article.setTitle(extractResponse.getTitle());

        if (toLoadImage(this.getContext())) {
            try {
                if (!extractResponse.getImages().isEmpty()) {
                    String image = extractResponse.getImages().get(0);
                    if (image != null && image.length() != 0) {
                        article.setImageUrl(new URL(image));
                        article.loadImage(image);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    protected void enlargeLoadedView() {

        try {
            if (!article.isAlreadyLoaded())
                future.get();

            loadedArticleView = new ContentLoadedView(this.getContext(), article, pageView);


            handler.post(new Runnable() {
                public void run() {
                    switcher.setDisplayedChild(0);
                    ExpandableArticleView.this.getPageView().enlarge(loadedArticleView, ExpandableArticleView.this);
                }
            });
        } catch (InterruptedException e) {
            handler.post(new Runnable() {
                public void run() {
                    AlarmSender.sendInstantMessage(R.string.tikatimeout, ExpandableArticleView.this.getContext());
                    reloadOriginalView();
                }
            });
        } catch (ExecutionException e) {
            e.printStackTrace();
            handler.post(new Runnable() {
                public void run() {
                    AlarmSender.sendInstantMessage(R.string.tikaservererror, ExpandableArticleView.this.getContext());
                    reloadOriginalView();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            handler.post(new Runnable() {
                public void run() {
                    AlarmSender.sendInstantMessage(R.string.unknownerror, ExpandableArticleView.this.getContext());
                    reloadOriginalView();
                }
            });
        }
    }

    protected abstract void reloadOriginalView();

    protected boolean toLoadImage(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.key_load_image_preference), true);
    }

    protected void addOnClickListener() {
        contentView.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (handler == null)
                    handler = new Handler();
                if (!isLoading && !ExpandableArticleView.this.getPageView().loadingNext && article.hasLink()) {

                    if (enlargedView != null) {//以前打开过的，直接显示
                        ExpandableArticleView.this.getPageView().enlarge(loadedArticleView, ExpandableArticleView.this);
                        return;
                    }

                    if (future.isDone()) { //如果加载好了，直接显示
                        enlargeLoadedView();
                        return;
                    }

                    isLoading = true;

                    ExpandableArticleView.this.contentViewWrapper.startAnimation(fadeOutAni);
                    return;
                }
                return;
            }
        });
    }
}
