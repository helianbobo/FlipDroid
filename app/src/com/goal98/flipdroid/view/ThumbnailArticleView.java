package com.goal98.flipdroid.view;

import android.content.Context;
import android.os.Handler;
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
import java.util.concurrent.ExecutorService;

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

    public ThumbnailArticleView(Context context, Article article, WeiboPageView pageView, boolean placedAtBottom, ExecutorService executor) {
        super(context, article, pageView, placedAtBottom, executor);
    }

    protected String getPrefix() {
        return Constants.INDENT;
    }

    public void displayLoadedThumbnail() {
        try {
            //System.out.println("taking content");
            if (!article.isAlreadyLoaded()) {
                article = future.get();
//                executor.shutdown();
            }

            isLoading = true;
            handler.post(new Runnable() {
                public void run() {
                    boolean scaled = false;
                    boolean largeScreen = false;
                    boolean smallScreen = false;
                    if (deviceInfo.isLargeScreen()) {
                        largeScreen = true;
                    }else if (deviceInfo.isSmallScreen()) {
                        smallScreen = true;
                    }
                    int titleSize = 18;
                    int maxTitleLength = 0;
                    if(!smallScreen){
                        maxTitleLength = 50;
                    }else{
                        maxTitleLength = 20;
                    }
                    if (article.getTitle() != null && article.getTitleLength() >= maxTitleLength) {
                        titleSize = 15;
                        scaled = true;
                        if (largeScreen) {
                            titleSize = 16;
                        }else if(smallScreen){
                            titleSize = 15;
                        }
                    } else {
                        if (largeScreen) {
                            titleSize = 21;
                        }else if (smallScreen) {
                            titleSize = 17;
                        }
                    }
                    titleView.setTextSize(titleSize);
                    titleView.setText(article.getTitle());
                    titleView.setWidth(deviceInfo.getWidth());

                    int maxLines = 5;
                    int textSize = 16;
                    if (largeScreen) {
                        maxLines = 6;
                        textSize = 18;
                    }else if (smallScreen) {
                        maxLines = 4;
                        textSize = 18;
                    }

                    TextView t = new TextView(ThumbnailArticleView.this.getContext());
                    t.getPaint().setAntiAlias(true);
                    int scaleTextSize = scaled ? textSize - 3 : textSize;
                    t.setTextSize(scaleTextSize);
                    if(!smallScreen)
                        t.setPadding(2, 8, 2, 8);
                    else
                        t.setPadding(2, 4, 2, 4);
                    t.setTextColor(0xff232323);
                    int maxLine = scaled ? maxLines + (smallScreen ? 0 : 1) : maxLines;
                    t.setMaxLines(maxLine);

                    new ArticleTextViewRender(getPrefix()).renderTextView(t, article);

                    //System.out.println("article.getImageUrl()" + article.getImageUrl());
                    if (article.getImageUrl() == null || !toLoadImage(getContext())) {
                        LayoutParams layoutParams = new LayoutParams(0, LayoutParams.FILL_PARENT);
                        layoutParams.weight = 100;
                        contentViewWrapper.addView(t, layoutParams);
                    } else {
                        imageView = new WebImageView(ThumbnailArticleView.this.getContext(), article.getImageUrl().toExternalForm(), false);
                        imageView.imageView.setTag(article.getImageUrl().toExternalForm());
                        imageView.setDefaultWidth(deviceInfo.getWidth() / 2 - 8);
                        imageView.setDefaultHeight((scaleTextSize + (largeScreen ? 15 : smallScreen?0:5)) * maxLine);
                        //System.out.println("article.getImage()" + article.getImage());
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

//                        article.getImage().recycle();
                        LayoutParams layoutParamsText = new LayoutParams(0, LayoutParams.FILL_PARENT);
                        LayoutParams layoutParamsImage = new LayoutParams(0, LayoutParams.FILL_PARENT);
//                        if (imageView.getFatOrSlim() == WebImageView.FAT) {
                            layoutParamsText.weight = 50;
                            layoutParamsImage.weight = 50;
//                        } else {
//                            if (imageHandled) {
//                                contentViewWrapper.setWeightSum(100);
//                                layoutParamsText.weight = 100 - imageView.getPercentageInWidth();
//                                layoutParamsImage.weight = imageView.getPercentageInWidth();
//                            } else {
//                                layoutParamsText.weight = 50;
//                                layoutParamsImage.weight = 50;
//                            }
//                        }

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
        else
            portraitView.setVisibility(GONE);

//        contentViewWrapper.setOnClickListener(new OnClickListener() {
//            public void onClick(View view) {
//                if (isLoading)
//                    return;
//
//                if (article.hasLink() && !ThumbnailArticleView.this.getPageView().loadingNext) {
//                    //Log.d("scale", ThumbnailArticleView.this.getLeft() + ":" + ThumbnailArticleView.this.getRight() + ":" + ThumbnailArticleView.this.getTop() + ":" + ThumbnailArticleView.this.getBottom());
//                    if (enlargedView != null) {//以前打开过的，直接显示
//                        //Log.d("scale", ThumbnailArticleView.this.getLeft() + ":" + ThumbnailArticleView.this.getRight() + ":" + ThumbnailArticleView.this.getTop() + ":" + ThumbnailArticleView.this.getBottom());
//                        ThumbnailArticleView.this.getPageView().enlarge(loadedArticleView, ThumbnailArticleView.this);
//
//                        return;
//                    }
//
//                    if (article.isAlreadyLoaded() || future.isDone()) { //如果加载好了，直接显示
//                        enlargeLoadedView();
//                        return;
//                    }
//
//                    ThumbnailArticleView.this.thumbnailViewWrapper.startAnimation(fadeOutAni);
//                    return;
//                }
////                if (event.getAction() == MotionEvent.ACTION_DOWN)//don't swallow action down event,or PageActivity won't handle it
////                    return;
//
//                return;
//            }
//        });
        addOnClickListener();
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