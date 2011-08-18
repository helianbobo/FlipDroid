package com.goal98.flipdroid.view;

import android.content.Context;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import com.goal98.android.WebImageView;
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.client.TikaClient;
import com.goal98.flipdroid.client.TikaExtractResponse;
import com.goal98.flipdroid.model.Article;
import com.goal98.flipdroid.model.cachesystem.CacheSystem;
import com.goal98.flipdroid.model.cachesystem.TikaCache;
import com.goal98.flipdroid.util.AlarmSender;
import com.goal98.flipdroid.util.Constants;

import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.List;
import java.util.Random;
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
    //    protected LinearLayout enlargedView;
    protected WeakReference<LinearLayout> enlargedView;
    protected final Animation fadeOutAni = AnimationUtils.loadAnimation(this.getContext(), R.anim.fade);
    protected final Animation fadeInAni = AnimationUtils.loadAnimation(this.getContext(), R.anim.fadein);
    protected Handler handler;
    protected Future<Article> future;
    public volatile boolean isLoading = false;
    protected ExecutorService executor;
    private TikaCache tikaCache;
    protected WebImageView imageView;

    public class Notifier {
        public void notifyImageLoaded() {
            handler.post(new Runnable() {
                public void run() {
                    imageView.handleImageLoaded(article.getImage(), null);
                }
            });
        }
    }

    public ExpandableArticleView(Context context, Article article, WeiboPageView pageView, boolean placedAtBottom, ExecutorService executor) {
        super(context, article, pageView, placedAtBottom);
        if (!article.isAlreadyLoaded()) {
            this.executor = executor;
            preload();
        } else {
            if (toLoadImage(context))
                article.loadPrimaryImage(deviceInfo);
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

    protected void buildImageAndContent() {
        boolean scaled = false;
        boolean largeScreen = false;
        boolean smallScreen = false;
        if (deviceInfo.isLargeScreen()) {
            largeScreen = true;
        } else if (deviceInfo.isSmallScreen()) {
            smallScreen = true;
        }


        int maxLines = 5;
        int textSize = 16;
        if (largeScreen) {
            maxLines = 6;
            textSize = 18;
        } else if (smallScreen) {
            maxLines = 4;
            textSize = 18;
        }

        contentView = new TextView(this.getContext());
        contentView.getPaint().setAntiAlias(true);
        int scaleTextSize = scaled ? textSize - 3 : textSize;
        contentView.setTextSize(scaleTextSize);
        if (!smallScreen)
            contentView.setPadding(2, 8, 2, 8);
        else
            contentView.setPadding(2, 4, 2, 4);

        contentView.setTextColor(0xff232323);
        int maxLine = scaled ? maxLines + (smallScreen ? 0 : 1) : maxLines;

        contentView.setGravity(Gravity.CENTER_VERTICAL);
        setText();

        if (article.getHeight() == 0) {
            contentView.setMaxLines(maxLine);
        } else {
            maxLine = (article.getHeight() / scaleTextSize) - 5;
            contentView.setMaxLines(maxLine);
        }

        //System.out.println("article.getImageUrl()" + article.getImageUrl());
        if (article.getImageUrl() == null || !toLoadImage(getContext())) {
            LayoutParams layoutParams = new LayoutParams(0, LayoutParams.FILL_PARENT);
            layoutParams.weight = 100;
            contentViewWrapper.addView(contentView, layoutParams);
        } else {
            imageView = new WebImageView(this.getContext(), article.getImageUrl().toExternalForm(), false);
            imageView.imageView.setTag(article.getImageUrl().toExternalForm());

            if (article.getHeight() == 0) {
                imageView.setDefaultWidth(deviceInfo.getWidth() / 2 - 8);
                imageView.setDefaultHeight((scaleTextSize + (largeScreen ? 15 : smallScreen ? 0 : 5)) * maxLine);
            } else {
                imageView.setDefaultWidth(deviceInfo.getWidth());
                imageView.setDefaultHeight(article.getHeight() - article.getTextHeight() - 30);
            }

//            imageView.setDefaultHeight((scaleTextSize + (largeScreen ? 15 : smallScreen ? 0 : 5)) * maxLine);
            boolean imageHandled = false;
            if (article.getImage() != null) {
                imageView.handleImageLoaded(article.getImage(), null);
                imageHandled = true;
            } else {
                article.addNotifier(new Notifier());
                if (!article.isLoading()) {
                    System.out.println("reloading..." + article.getImageUrl().toExternalForm());
                    article.loadPrimaryImage(deviceInfo);
                }
                imageHandled = false;
            }
            if (article.getHeight() == 0) {
                LayoutParams layoutParamsText = new LayoutParams(0, LayoutParams.FILL_PARENT);
                LayoutParams layoutParamsImage = new LayoutParams(0, LayoutParams.FILL_PARENT);
                layoutParamsText.weight = 50;
                layoutParamsImage.weight = 50;

                Random random = new Random();
                random.setSeed(System.currentTimeMillis());
                if (random.nextBoolean()) {
                    contentViewWrapper.addView(contentView, layoutParamsText);
                    layoutParamsImage.gravity = Gravity.RIGHT;
                    contentViewWrapper.addView(imageView, layoutParamsImage);
                } else {
                    layoutParamsImage.gravity = Gravity.LEFT;
                    contentViewWrapper.addView(imageView, layoutParamsImage);
                    contentViewWrapper.addView(contentView, layoutParamsText);
                }
            } else {
                LayoutParams layoutParamsText = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
                LayoutParams layoutParamsImage = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
                contentViewWrapper.setOrientation(VERTICAL);
                contentViewWrapper.addView(contentView, layoutParamsText);
                layoutParamsImage.gravity = Gravity.TOP;
                contentViewWrapper.addView(imageView, layoutParamsImage);
            }
        }
    }

    public abstract void setText();

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
            article.setContent(extractResponse.getContent());
        else
            article.setContent("");
        article.setTitle(extractResponse.getTitle());

        if (toLoadImage(this.getContext())) {
            try {
                if (!extractResponse.getImages().isEmpty()) {
                    List<String> responsedImages = extractResponse.getImages();
                    for (int i = 0; i < responsedImages.size(); i++) {
                        String imageURL = responsedImages.get(i);
                        if (imageURL != null && imageURL.length() != 0) {
                            int sizeInfoBeginAt = imageURL.lastIndexOf("#");
                            String sizeInfoStr = imageURL.substring(sizeInfoBeginAt + 1);
                            imageURL = imageURL.substring(0, sizeInfoBeginAt);

                            if (i == 0) {//primary image
                                String[] sizeInfo = sizeInfoStr.split(",");
                                int width = Integer.valueOf(sizeInfo[0]);
                                int height = Integer.valueOf(sizeInfo[1]);
                                article.setImageWidth(width);
                                article.setImageHeight(height);
                                try {
                                    URL url = new URL(imageURL);
                                    article.setImageUrl(url);
                                    article.loadPrimaryImage(imageURL, deviceInfo);
                                } catch (Exception e) {
                                    continue;
                                }
                            }
                            article.getImagesMap().put(imageURL, null);
                            article.getImages().add(imageURL);
                        }
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
        contentViewWrapper.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (handler == null)
                    handler = new Handler();
                if (!isLoading && !ExpandableArticleView.this.getPageView().loadingNext && article.hasLink()) {

                    if (enlargedView != null && enlargedView.get() != null) {//以前打开过的，直接显示
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
