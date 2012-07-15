package com.goal98.girl.view;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.goal98.android.WebImageView;
import com.goal98.girl.R;
import com.goal98.girl.model.Article;
import com.goal98.girl.util.AlarmSender;
import com.goal98.girl.util.Constants;
import com.goal98.girl.util.PrettyTimeUtil;
import com.goal98.tika.common.TikaConstants;

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

    public ThumbnailArticleView(Context context, Article article, ThumbnailViewContainer pageViewContainer, boolean placedAtBottom, ExecutorService executor) {
        super(context, article, pageViewContainer, placedAtBottom, executor);
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
                    if (titleView != null)
                        ThumbnailContentRender.setTitleText(titleView, article, deviceInfo);
                    buildImageAndContent();
                    reloadOriginalView();
                }


            });
        } catch (InterruptedException e) {
            handler.post(new Runnable() {
                public void run() {
                    new AlarmSender(ThumbnailArticleView.this.getContext().getApplicationContext()).sendInstantMessage(R.string.tikatimeout);
//                    reloadOriginalView();
                }
            });
        } catch (ExecutionException e) {
            //System.out.println("taking content error");
            handler.post(new Runnable() {
                public void run() {
                    new AlarmSender(ThumbnailArticleView.this.getContext().getApplicationContext()).sendInstantMessage(R.string.tikaservererror);
//                    reloadOriginalView();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            //System.out.println("taking content error");
            handler.post(new Runnable() {
                public void run() {
                    new AlarmSender(ThumbnailArticleView.this.getContext().getApplicationContext()).sendInstantMessage(R.string.unknownerror);
//                    reloadOriginalView();
                }
            });
        } finally {
            isLoading = false;
        }
    }


    protected void reloadOriginalView() {
        setDisplayedChild(0);
//        thumbnailViewWrapper.setVisibility(VISIBLE);
    }

    public void buildView() {
        LayoutInflater inflater = LayoutInflater.from(this.getContext());
        inflater.inflate(R.layout.thumbnail, this, true);

        setDisplayedChild(1);
        this.thumbnailViewWrapper = (LinearLayout) findViewById(R.id.loadedView);
//        this.weiboContentWrapper = (LinearLayout) switcher.findViewById(R.id.weiboContent);

//        thumbnailViewWrapper.setVisibility(INVISIBLE);
        loadedThumbnail = inflater.inflate(R.layout.loadedthumbnail, thumbnailViewWrapper, true);

        String localeStr = this.getContext().getString(R.string.locale);
        String time = PrettyTimeUtil.getPrettyTime(localeStr, article.getCreatedDate());

//        if (!article.getSourceType().equals(TikaConstants.TYPE_RSS) && article.getSourceURL()!=null) {
//            weiboContent = inflater.inflate(R.layout.loadedthumbnail, weiboContentWrapper, true);
////            weiboContentWrapper.addView(weiboContent, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
//            TextView authorViewWeiboContent = (TextView) weiboContent.findViewById(R.id.author);
//            TextView createDateViewWeiboContent = (TextView) weiboContent.findViewById(R.id.createDate);
//            LinearLayout contentViewWrapperWeiboContent = (LinearLayout) weiboContent.findViewById(R.id.contentll);
//
//            TextView contentView = new TextView(this.getContext());
//            contentViewWrapperWeiboContent.addView(contentView);
//            ThumbnailContentRender.setThumbnailContentText(contentView, article, deviceInfo);
//            contentView.setText(article.getStatus());
//
//            authorViewWeiboContent.setText(article.getAuthor());
//            createDateViewWeiboContent.setText(time);
//
//            if (article.hasLink()) {
//                View progressBar = weiboContent.findViewById(R.id.progressbar);
//                View textUrlLoading = weiboContent.findViewById(R.id.textUrlLoading);
//                progressBar.setVisibility(VISIBLE);
//                textUrlLoading.setVisibility(VISIBLE);
//            }
//            portraitViewWeiboContent = (WebImageView) weiboContent.findViewById(R.id.portrait2);
//            if (article.getPortraitImageUrl() != null) {
//                portraitViewWeiboContent.setImageUrl(article.getPortraitImageUrl().toString());
//            } else {
//                portraitViewWeiboContent.setVisibility(GONE);
//            }
//        }

        this.titleView = (TextView) loadedThumbnail.findViewById(R.id.title);

        this.authorView = (TextView) loadedThumbnail.findViewById(R.id.author);

        this.createDateView = (TextView) loadedThumbnail.findViewById(R.id.createDate);

        this.contentViewWrapper = (LinearLayout) loadedThumbnail.findViewById(R.id.contentll);


        this.portraitView = (WebImageView) loadedThumbnail.findViewById(R.id.portrait2);


        authorView.setText(article.getAuthor());


        createDateView.setText(time);

        if (article.getPortraitImageUrl() != null) {
            portraitView.setAutoSize(true);
            portraitView.setImageUrl(article.getPortraitImageUrl().toString());
        } else {
            portraitView.setVisibility(GONE);
        }
        addOnClickListener(contentViewWrapper);
        if (titleView != null)
            ThumbnailContentRender.setTitleText(titleView, article, deviceInfo);

        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
        }

        buildImageAndContent();
        reloadOriginalView();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (!article.getSourceType().equals(TikaConstants.TYPE_RSS) && portraitViewWeiboContent != null)
                    portraitViewWeiboContent.loadImage();
                else
                    portraitView.loadImage();
            }
        });
    }

    public void renderBeforeLayout() {

//        System.gc();
    }
}