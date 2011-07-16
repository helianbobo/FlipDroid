package com.goal98.flipdroid.view;

import android.content.Context;
import android.graphics.Paint;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import com.goal98.android.WebImageView;
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.model.Article;
import com.goal98.flipdroid.util.AlarmSender;
import com.goal98.flipdroid.util.Constants;
import com.goal98.flipdroid.util.DeviceInfo;
import com.goal98.flipdroid.util.PrettyTimeUtil;

import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
public class ThumbnailArticleView extends ExpandableArticleView {
    private LinearLayout thumbnailViewWrapper;
    private View loadedThumbnail;
    private WebImageView imageView;
    volatile boolean isLoading = false;
    protected Handler handler;

    public ThumbnailArticleView(Context context, Article article, WeiboPageView pageView, boolean placedAtBottom) {
        super(context, article, pageView, placedAtBottom);

        preload();
    }

    protected String getPrefix() {
        return Constants.INDENT;
    }

    public void displayLoadedThumbnail() {
        try {
            //System.out.println("taking content");
            final Article article = future.get();
            isLoading = true;
            handler.post(new Runnable() {
                public void run() {
                    boolean scaled = false;
                    boolean largeScreen = false;
                    if (DeviceInfo.height == 800) {
                        largeScreen = true;
                    }
                    int titleSize = 18;
                    if (article.getTitle() != null && article.getTitleLength() >= 50) {
                        titleSize = 15;
                        scaled = true;
                        if (largeScreen) {
                            titleSize = 15;
                        }
                    }
                    titleView.setTextSize(titleSize);
                    titleView.setText(article.getTitle());
                    titleView.setWidth(DeviceInfo.width);

                    int maxLines = 5;
                    int textSize = 16;
                    if (largeScreen) {
                        maxLines = 6;
                        textSize = 18;
                    }

                    TextView t = new TextView(ThumbnailArticleView.this.getContext());
                    t.getPaint().setAntiAlias(true);
                    int scaleTextSize = scaled ? textSize - 3 : textSize;
                    t.setTextSize(scaleTextSize);
                    t.setPadding(2, 8, 2, 8);
                    t.setTextColor(0xff232323);
                    int maxLine = scaled ? maxLines + (largeScreen ? 1 : 1) : maxLines;
                    t.setMaxLines(maxLine);

                    if (article != null && article.getContent() != null) {
                        t.setText(getPrefix() + (article.getContent().trim()).replaceAll("\n+", "      \n"));
                    } else {
                        t.setVisibility(GONE);
                        titleView.setTextSize(25);
                    }
                    //System.out.println("article.getImageUrl()" + article.getImageUrl());
                    if (article.getImageUrl() == null || !toLoadImage(getContext())) {
                        LayoutParams layoutParams = new LayoutParams(0, LayoutParams.FILL_PARENT);
                        layoutParams.weight = 100;
                        contentViewWrapper.addView(t, layoutParams);
                    } else {
                        imageView = new WebImageView(ThumbnailArticleView.this.getContext(), article.getImageUrl().toExternalForm(), false);
                        imageView.imageView.setTag(article.getImageUrl().toExternalForm());
                        imageView.setDefaultWidth(DeviceInfo.width / 2 - 8);
                        imageView.setDefaultHeight((scaleTextSize + (largeScreen ? 15 : 5)) * maxLine);
                        //System.out.println("article.getImage()" + article.getImage());
                        if (article.getImage() != null)
                            imageView.handleImageLoaded(article.getImage(), null);
                        else
                            article.addNotifier(new Notifier());
                        LayoutParams layoutParamsText = new LayoutParams(0, LayoutParams.FILL_PARENT);
                        LayoutParams layoutParamsImage = new LayoutParams(0, LayoutParams.FILL_PARENT);
                        if (imageView.getFatOrSlim() == WebImageView.FAT) {
                            layoutParamsText.weight = 50;
                            layoutParamsImage.weight = 50;
                        } else {
                            layoutParamsText.weight = 50;
                            layoutParamsImage.weight = 50;
                            //System.out.println("imageView.getPercentageInWidth()"+imageView.getPercentageInWidth());
                        }

                        Random random = new Random();
                        random.setSeed(System.currentTimeMillis());
                        if (random.nextBoolean()) {
                            contentViewWrapper.addView(t, layoutParamsText);
                            layoutParamsImage.gravity = Gravity.RIGHT;
                            contentViewWrapper.addView(imageView, layoutParamsImage);
                        } else {
                            layoutParamsImage.gravity = Gravity.LEFT;
                            contentViewWrapper.addView(imageView, layoutParamsImage);
                            contentViewWrapper.addView(t, layoutParamsText);
                        }
                    }
                    reloadOriginalView();
                }


            });
        } catch (InterruptedException e) {
            handler.post(new Runnable() {
                public void run() {
                    AlarmSender.sendInstantMessage(R.string.tikatimeout, ThumbnailArticleView.this.getContext());
//                    reloadOriginalView();
                }
            });
        } catch (ExecutionException e) {
            //System.out.println("taking content error");
            handler.post(new Runnable() {
                public void run() {
                    AlarmSender.sendInstantMessage(R.string.tikaservererror, ThumbnailArticleView.this.getContext());
//                    reloadOriginalView();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            //System.out.println("taking content error");
            handler.post(new Runnable() {
                public void run() {
                    AlarmSender.sendInstantMessage(R.string.unknownerror, ThumbnailArticleView.this.getContext());
//                    reloadOriginalView();
                }
            });
        } finally {
            isLoading = false;
        }
    }



    public class Notifier {
        public void notifyImageLoaded() {
            handler.post(new Runnable() {
                public void run() {
                    imageView.handleImageLoaded(article.getImage(), null);
                }
            });
        }
    }

    protected void reloadOriginalView() {
        switcher.setDisplayedChild(0);
        fadeInAni.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {

            }

            public void onAnimationEnd(Animation animation) {
                thumbnailViewWrapper.setVisibility(VISIBLE);
            }

            public void onAnimationRepeat(Animation animation) {

            }
        });
        ThumbnailArticleView.this.thumbnailViewWrapper.startAnimation(fadeInAni);
    }

    public void buildView() {
        LayoutInflater inflater = LayoutInflater.from(this.getContext());
        switcher = (ViewSwitcher) inflater.inflate(R.layout.thumbnail, null);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

        this.addView(switcher, layoutParams);
        switcher.setDisplayedChild(1);
        this.thumbnailViewWrapper = (LinearLayout) switcher.findViewById(R.id.loadedView);
        thumbnailViewWrapper.setVisibility(INVISIBLE);
        loadedThumbnail = inflater.inflate(R.layout.loadedthumbnail, null);
        thumbnailViewWrapper.addView(loadedThumbnail, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

        this.titleView = (TextView) loadedThumbnail.findViewById(R.id.title);
        this.authorView = (TextView) loadedThumbnail.findViewById(R.id.author);
        this.createDateView = (TextView) loadedThumbnail.findViewById(R.id.createDate);
        this.contentViewWrapper = (LinearLayout) loadedThumbnail.findViewById(R.id.contentll);

        this.portraitView = (WebImageView) loadedThumbnail.findViewById(R.id.portrait2);
        authorView.setText(article.getAuthor());

        String time = PrettyTimeUtil.getPrettyTime(this.getContext(), article.getCreatedDate());
        createDateView.setText(time);
        if (article.getPortraitImageUrl() != null)
            portraitView.setImageUrl(article.getPortraitImageUrl().toString());

        contentViewWrapper.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (isLoading)
                    return;

                if (article.hasLink() && !ThumbnailArticleView.this.getPageView().loadingNext) {
                    //Log.d("scale", ThumbnailArticleView.this.getLeft() + ":" + ThumbnailArticleView.this.getRight() + ":" + ThumbnailArticleView.this.getTop() + ":" + ThumbnailArticleView.this.getBottom());
                    if (enlargedView != null) {//以前打开过的，直接显示
                        //Log.d("scale", ThumbnailArticleView.this.getLeft() + ":" + ThumbnailArticleView.this.getRight() + ":" + ThumbnailArticleView.this.getTop() + ":" + ThumbnailArticleView.this.getBottom());
                        ThumbnailArticleView.this.getPageView().enlarge(loadedArticleView, ThumbnailArticleView.this);

                        return;
                    }

                    if (future.isDone()) { //如果加载好了，直接显示
                        enlargeLoadedView();
                        return;
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
                    ThumbnailArticleView.this.thumbnailViewWrapper.startAnimation(fadeOutAni);
                    return;
                }
//                if (event.getAction() == MotionEvent.ACTION_DOWN)//don't swallow action down event,or PageActivity won't handle it
//                    return;

                return;
            }
        });
    }

    public void renderBeforeLayout() {
        if (handler == null)
            handler = new Handler();

        new Thread(new Runnable() {
            public void run() {
                displayLoadedThumbnail();
            }
        }).start();
        portraitView.loadImage();
    }
}