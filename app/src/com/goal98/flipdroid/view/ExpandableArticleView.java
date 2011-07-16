package com.goal98.flipdroid.view;

import android.content.Context;
import android.os.Handler;
import android.preference.PreferenceManager;
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
import com.goal98.flipdroid.util.AlarmSender;

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
    private ExecutorService executor;

    public ExpandableArticleView(Context context, Article article, WeiboPageView pageView, boolean placedAtBottom) {
        super(context, article, pageView, placedAtBottom);
        executor = Executors.newFixedThreadPool(1);
        preload();
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

                            if (toLoadImage(ExpandableArticleView.this.getContext())) {
                                try {
                                    if (!extractResponse.getImages().isEmpty()) {
                                        String image = extractResponse.getImages().get(0);
                                        article.setImageUrl(new URL(image));
                                        PreloadImageLoaderHandler preloadImageLoaderHandler = new PreloadImageLoaderHandler(article);
//                                    preloadImageLoaderHandler.setWidth(DeviceInfo.width/2-32);
//                                    preloadImageLoaderHandler.setHeight(DeviceInfo.height/2-100);

                                        final ImageLoader loader = new ImageLoader(image, preloadImageLoaderHandler);
                                        new Thread(new Runnable() {
                                            public void run() {
                                                loader.run();
                                            }
                                        }).start();

                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
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

    protected void enlargeLoadedView() {

        try {
            loadedArticleView = new ContentLoadedView(this.getContext(), future.get(), pageView);


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
}
