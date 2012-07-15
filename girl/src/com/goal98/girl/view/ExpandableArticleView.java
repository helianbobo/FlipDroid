package com.goal98.girl.view;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.goal98.android.WebImageView;
import com.goal98.girl.R;
import com.goal98.girl.client.TikaClient;
import com.goal98.girl.client.TikaExtractResponse;
import com.goal98.girl.model.Article;
import com.goal98.girl.model.cachesystem.CacheSystem;
import com.goal98.girl.model.cachesystem.TikaCache;
import com.goal98.girl.multiscreen.MultiScreenSupport;
import com.goal98.girl.util.AlarmSender;
import com.goal98.girl.util.Constants;

import java.net.MalformedURLException;
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

    private String TAG = this.getClass().getName();

//    protected ViewSwitcher switcher;
    protected ArticleView loadedArticleView;
    //    protected LinearLayout enlargedView;
    protected LinearLayout enlargedView;
    protected final Animation fadeOutAni = AnimationUtils.loadAnimation(this.getContext(), R.anim.fade);
    protected final Animation fadeInAni = AnimationUtils.loadAnimation(this.getContext(), R.anim.fadein);
    protected Handler handler;
    protected Future<Article> future;
    protected volatile boolean isLoading = false;
    protected ExecutorService executor;
    private TikaCache tikaCache;
    protected WebImageView imageView;



    public ExpandableArticleView(Context context, Article article, ThumbnailViewContainer pageViewContainer, boolean placedAtBottom, ExecutorService executor) {
        super(context, article, pageViewContainer, placedAtBottom);

            this.executor = executor;
//        article.loadPrimaryImage(deviceInfo, toLoadImage);
        if (!article.isAlreadyLoaded() && article.hasLink()) {
            preload();
        }

        fadeOutAni.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                fadeOutAni.setAnimationListener(null);
                setDisplayedChild(0);
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
        contentView = new TextView(this.getContext());
        setThumbnailContentText(contentView);
        setText();
        MultiScreenSupport mss = MultiScreenSupport.getInstance(deviceInfo);
        if (article.getImageUrl() == null) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.FILL_PARENT);
            layoutParams.weight = 100;
            contentViewWrapper.addView(contentView, layoutParams);
        } else {
            imageView = new WebImageView(this.getContext(), article.getImageUrl().toExternalForm(), this.getResources().getDrawable(Constants.DEFAULT_PIC), this.getResources().getDrawable(Constants.DEFAULT_PIC), false, toLoadImage, ImageView.ScaleType.CENTER);
//            imageView.setRoundImage(true);
//            imageView.imageView.setTag(article.getImageUrl().toExternalForm());
//            imageView.setBackgroundResource(R.drawable.border);

//            if (article.getHeight() == 0) {
//                imageView.setDefaultWidth(deviceInfo.getWidth() / 2 - 8);
//                imageView.setDefaultHeight(mss.getImageHeightThumbnailView());  //(largeScreen ? 15 : smallScreen ? 0 : 5)
//            } else {
//                imageView.setDefaultWidth(deviceInfo.getWidth());
//                imageView.setDefaultHeight(deviceInfo.getHeight() - article.getTextHeight() - 30);
//            }

//            if (article.getImage() != null && !article.getImage().isRecycled()) {
//                imageView.handleImageLoaded(article.getImage(), null);
//            } else {
//                article.addNotifier(new ExpandableArticleViewNotifier());
//            }
            
            handler.post(new Runnable() {
                @Override
                public void run() {
                    imageView.loadImage();
                }
            });

            if (article.getHeight() == 0 ) {
                LinearLayout.LayoutParams layoutParamsText = new LinearLayout.LayoutParams(0, mss.getImageHeightThumbnailView());
                LinearLayout.LayoutParams layoutParamsImage = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
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
                LinearLayout.LayoutParams layoutParamsText = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                LinearLayout.LayoutParams layoutParamsImage = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//                contentViewWrapper.setOrientation(VERTICAL);
                contentViewWrapper.addView(contentView, layoutParamsText);
                layoutParamsImage.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
                contentViewWrapper.addView(imageView, layoutParamsImage);
            }
        }
    }

    protected void setThumbnailContentText(TextView contentView) {
        MultiScreenSupport mss = MultiScreenSupport.getInstance(deviceInfo);

        int maxLines = mss.getMaxLineInThumbnailView();
        int[] paddings = mss.getTextViewPaddingInThumbnailView();

        int textSize = mss.getTextViewTextSize();

        contentView.getPaint().setAntiAlias(true);
        contentView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize);
        contentView.setPadding(paddings[0], paddings[1], paddings[2], paddings[3]);
        contentView.setTextColor(Constants.LOADED_TEXT_COLOR);
        contentView.setGravity(Gravity.CENTER_VERTICAL);


        if (article.getHeight() == 0) {
            contentView.setMaxLines(maxLines);
        } else {
            maxLines = (article.getHeight() / textSize) - 5;
            maxLines = Math.max(maxLines, 5);
            contentView.setMaxLines(maxLines);
        }
    }

    public abstract void setText();

    public void preload() {
        if (article.hasLink()) {
            System.out.println("--------------------------------preloading "+article.extractURL()+"--------------------------------");
            isLoading = true;
            future = executor.submit(new Callable() {
                public Object call() throws Exception {
                    try {
                        String url = article.extractURL();
                        return getArticleFromURL(url);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return article;
                    } finally {
                        tikaCache.shutdown();
                        isLoading = false;
                    }
                }
            });
        }
    }

    private Object getArticleFromURL(String url) throws MalformedURLException {
        tikaCache = CacheSystem.getTikaCache(this.getContext());
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
                Log.e(TAG, e.getMessage(), e);
                article.setTitle("");
                article.setContent(article.getStatus());
                return article;
            }
            responseToArticle(extractResponse);
            if (extractResponse.hasContent())
                tikaCache.put(url, extractResponse);
            return article;
        }
    }

    private void responseToArticle(TikaExtractResponse extractResponse) {
        if (extractResponse.getContent() != null)
            article.setContent(extractResponse.getContent());
        else
            article.setContent("");

        article.setTitle(extractResponse.getTitle());
        article.setSourceURL(extractResponse.getSourceURL());
        article.setExpandable(true);
        if (toLoadImage) {
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
//                                    article.loadPrimaryImage(imageURL, deviceInfo, toLoadImage);
                                } catch (Exception e) {
                                    continue;
                                }
                            }
//                            article.getImagesMap().put(imageURL, null);
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
            if (!article.isAlreadyLoaded() && future != null)
                future.get();

            loadedArticleView = new ContentLoadedView(this.getContext(), article, this.getPageViewContainer());
            handler.post(new Runnable() {
                public void run() {
                    setDisplayedChild(0);
                    ExpandableArticleView.this.getPageViewContainer().enlarge(loadedArticleView, ExpandableArticleView.this);
                }
            });
        } catch (InterruptedException e) {
            handler.post(new Runnable() {
                public void run() {
                    new AlarmSender(ExpandableArticleView.this.getContext().getApplicationContext()).sendInstantMessage(R.string.tikatimeout);
                    reloadOriginalView();
                }
            });
        } catch (ExecutionException e) {
            e.printStackTrace();
            handler.post(new Runnable() {
                public void run() {
                    new AlarmSender(ExpandableArticleView.this.getContext().getApplicationContext()).sendInstantMessage(R.string.tikaservererror);
                    reloadOriginalView();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            handler.post(new Runnable() {
                public void run() {
                    new AlarmSender(ExpandableArticleView.this.getContext().getApplicationContext()).sendInstantMessage(R.string.unknownerror);
                    reloadOriginalView();
                }
            });
        }
    }

    protected abstract void reloadOriginalView();


    protected void addOnClickListener(LinearLayout contentViewWrapper) {
        contentViewWrapper.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (isLoading) {
                    return;
                }

                if (handler == null)
                    handler = new Handler();
                if (!ExpandableArticleView.this.getPageViewContainer().loadingNext) {

                    if (enlargedView != null) {//以前打开过的，直接显示
                        ExpandableArticleView.this.getPageViewContainer().enlarge(loadedArticleView, ExpandableArticleView.this);
                        return;
                    }

                    if (!article.hasLink() || article.isAlreadyLoaded() || (future != null && future.isDone())) { //如果加载好了，直接显示
                        enlargeLoadedView();
                        return;
                    }

                    ExpandableArticleView.this.contentViewWrapper.startAnimation(fadeOutAni);
                    return;
                }
                return;
            }
        });
    }
}
