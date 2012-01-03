package com.goal98.flipdroid.view;

import android.content.Context;
import android.os.Handler;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import com.goal98.android.WebImageView;
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.model.Article;
import com.goal98.flipdroid.multiscreen.MultiScreenSupport;
import com.goal98.flipdroid.util.AlarmSender;
import com.goal98.flipdroid.util.Constants;
import com.goal98.flipdroid.util.PrettyTimeUtil;

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

    private LinearLayout weiboContentWrapper;
    private View weiboContent;
    private WebImageView portraitViewWeiboContent;

    public ThumbnailArticleView(Context context, Article article, WeiboPageView pageView, boolean placedAtBottom, ExecutorService executor) {
        super(context, article, pageView, placedAtBottom, executor);
    }

    public void setText() {
        new ArticleTextViewRender(getPrefix()).renderTextView(contentView, article);
    }

    protected String getPrefix() {
        return Constants.INDENT;
    }

    public void displayLoadedThumbnail() {
        try {
            if (!article.isAlreadyLoaded() && article.hasLink()) {
                article = future.get();
            }

            isLoading = true;
            handler.post(new Runnable() {
                public void run() {
                    setTitleText();
                    buildImageAndContent();
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

    private void setTitleText() {
        MultiScreenSupport mss = MultiScreenSupport.getInstance(deviceInfo);

        int maxTitleLength = mss.getThumbnailMaxTitleLength();
        int titleSize = 0;
        if (article.getTitle() != null && article.getTitleLength() >= maxTitleLength) {
            titleSize = mss.getThumbnailMaxLongTitleTextSize();
        } else {
            titleSize = mss.getThumbnailMaxTitleTextSize();
        }
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, titleSize);
        titleView.setText(article.getTitle());
        titleView.setWidth(deviceInfo.getWidth());
        titleView.setMinHeight(mss.getMinTitleHeight());
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
        this.weiboContentWrapper = (LinearLayout) switcher.findViewById(R.id.weiboContent);

        thumbnailViewWrapper.setVisibility(INVISIBLE);
        loadedThumbnail = inflater.inflate(R.layout.loadedthumbnail, null);
        thumbnailViewWrapper.addView(loadedThumbnail, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

        weiboContent = inflater.inflate(R.layout.loadedthumbnail, null);
        weiboContentWrapper.addView(weiboContent, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));


        this.titleView = (TextView) loadedThumbnail.findViewById(R.id.title);
//        TextView titleViewWeiboContent = (TextView) weiboContent.findViewById(R.id.title);

        this.authorView = (TextView) loadedThumbnail.findViewById(R.id.author);
        TextView authorViewWeiboContent = (TextView) weiboContent.findViewById(R.id.author);

        this.createDateView = (TextView) loadedThumbnail.findViewById(R.id.createDate);
        TextView createDateViewWeiboContent = (TextView) weiboContent.findViewById(R.id.createDate);

        this.contentViewWrapper = (LinearLayout) loadedThumbnail.findViewById(R.id.contentll);
        LinearLayout contentViewWrapperWeiboContent = (LinearLayout) weiboContent.findViewById(R.id.contentll);

        TextView contentView = new TextView(this.getContext());
        setThumbnailContentText(contentView);
        contentView.setText(article.getStatus());

        contentViewWrapperWeiboContent.addView(contentView);
        if(article.hasLink()){
            View progressBar = weiboContent.findViewById(R.id.progressbar);
            View textUrlLoading = weiboContent.findViewById(R.id.textUrlLoading);
            progressBar.setVisibility(VISIBLE);
            textUrlLoading.setVisibility(VISIBLE);
        }

        this.portraitView = (WebImageView) loadedThumbnail.findViewById(R.id.portrait2);
        portraitViewWeiboContent = (WebImageView) weiboContent.findViewById(R.id.portrait2);

        authorView.setText(article.getAuthor());
        authorViewWeiboContent.setText(article.getAuthor());

        String time = PrettyTimeUtil.getPrettyTime(this.getContext(), article.getCreatedDate());
        createDateView.setText(time);
        createDateViewWeiboContent.setText(time);

        if (article.getPortraitImageUrl() != null) {
            portraitView.setImageUrl(article.getPortraitImageUrl().toString());
            portraitViewWeiboContent.setImageUrl(article.getPortraitImageUrl().toString());
        } else {
            portraitView.setVisibility(GONE);
            portraitViewWeiboContent.setVisibility(GONE);
        }

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
        portraitViewWeiboContent.loadImage();
    }
}