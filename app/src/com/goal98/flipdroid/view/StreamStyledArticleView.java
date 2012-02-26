package com.goal98.flipdroid.view;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.goal98.android.WebImageView;
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.activity.ArticleDetailInfo;
import com.goal98.flipdroid.activity.DetailInfo;
import com.goal98.flipdroid.activity.ItemView;
import com.goal98.flipdroid.model.Article;
import com.goal98.flipdroid.multiscreen.MultiScreenSupport;
import com.goal98.flipdroid.util.Constants;
import com.goal98.flipdroid.util.DeviceInfo;
import com.goal98.flipdroid.util.PrettyTimeUtil;

import java.util.Random;
import java.util.concurrent.ExecutorService;

/**
 * Created by IntelliJ IDEA.
 * User: janexie
 * Date: 12-1-26
 * Time: 下午8:40
 * To change this template use File | Settings | File Templates.
 */
public class StreamStyledArticleView extends ItemView {
    private boolean toLoadImage;
    Handler handler = new Handler();
    private Article article;

    public void setToLoadImage(boolean toLoadImage) {
        this.toLoadImage = toLoadImage;
    }

    private ExecutorService executor;

    public StreamStyledArticleView(Context context, ExecutorService executor) {
        super(context);
        this.executor = executor;
    }

    public StreamStyledArticleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        final String XMLNS = "http://schemas.android.com/apk/res/" + context.getPackageName();
        toLoadImage = attrs.getAttributeBooleanValue(XMLNS,
                "toLoadFromInternet", true);
    }

    public void render(DetailInfo di) {
        ArticleDetailInfo articleDetailInfo = (ArticleDetailInfo) di;
        article = articleDetailInfo.getArticle();

        TextView titleView = (TextView) findViewById(R.id.title);

        TextView authorViewWeiboContent = (TextView) findViewById(R.id.author);

        TextView createDateViewWeiboContent = (TextView) findViewById(R.id.createDate);
//
        LinearLayout contentViewWrapperWeiboContent = (LinearLayout) findViewById(R.id.contentll);
//
        TextView contentView = new TextView(this.getContext());
        final DeviceInfo deviceInfo = DeviceInfo.getInstance((Activity) this.getContext());
//
        ThumbnailContentRender.setThumbnailContentText(contentView, article, deviceInfo);
        ThumbnailContentRender.setTitleText(titleView, article, deviceInfo);
        new ArticleTextViewRender(Constants.INDENT).renderTextView(contentView, article);
//
//        contentViewWrapperWeiboContent.addView(contentView);
//        if (article.hasLink()) {
//            View progressBar = findViewById(R.id.progressbar);
//            View textUrlLoading = findViewById(R.id.textUrlLoading);
//            progressBar.setVisibility(VISIBLE);
//            textUrlLoading.setVisibility(VISIBLE);
//        }
//
        final WebImageView portraitViewWeiboContent = (WebImageView) findViewById(R.id.portrait2);

        authorViewWeiboContent.setText(article.getAuthor());

        String time = PrettyTimeUtil.getPrettyTime(this.getContext(), article.getCreatedDate());
        createDateViewWeiboContent.setText(time);

        if (article.getPortraitImageUrl() != null) {
            portraitViewWeiboContent.setImageUrl(article.getPortraitImageUrl().toString());
        } else {
            portraitViewWeiboContent.setVisibility(GONE);
        }
        portraitViewWeiboContent.loadImage();
        MultiScreenSupport mss = MultiScreenSupport.getInstance(deviceInfo);
        if (article.getImageUrl() == null) {
            LayoutParams layoutParams = new LayoutParams(0, LayoutParams.FILL_PARENT);
            layoutParams.weight = 100;
            contentViewWrapperWeiboContent.addView(contentView, layoutParams);
        } else {
            final WebImageView imageView = new WebImageView(this.getContext(), article.getImageUrl().toExternalForm(), this.getResources().getDrawable(Constants.DEFAULT_PIC), this.getResources().getDrawable(Constants.DEFAULT_PIC), false, toLoadImage);
            imageView.setRoundImage(true);
//            imageView.imageView.setTag(article.getImageUrl().toExternalForm());
            imageView.setBackgroundResource(R.drawable.border);

            if (article.getHeight() == 0) {
                imageView.setDefaultWidth(deviceInfo.getWidth() / 2 - 8);
                imageView.setDefaultHeight(mss.getImageHeightThumbnailView());  //(largeScreen ? 15 : smallScreen ? 0 : 5)
            } else {
                imageView.setDefaultWidth(deviceInfo.getWidth());
                imageView.setDefaultHeight(deviceInfo.getHeight() - article.getTextHeight() - 30);
            }


            if (article.getHeight() == 0) {
                LayoutParams layoutParamsText = new LayoutParams(0, mss.getImageHeightThumbnailView());
                LayoutParams layoutParamsImage = new LayoutParams(0, LayoutParams.FILL_PARENT);
                layoutParamsText.weight = 50;
                layoutParamsImage.weight = 50;

                if (false) {
                    contentViewWrapperWeiboContent.addView(contentView, layoutParamsText);
                    layoutParamsImage.gravity = Gravity.RIGHT;
                    contentViewWrapperWeiboContent.addView(imageView, layoutParamsImage);
                } else {
                    layoutParamsImage.gravity = Gravity.LEFT;
                    contentViewWrapperWeiboContent.addView(imageView, layoutParamsImage);
                    contentViewWrapperWeiboContent.addView(contentView, layoutParamsText);
                }
            } else {
                LayoutParams layoutParamsText = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
                LayoutParams layoutParamsImage = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
                contentViewWrapperWeiboContent.setOrientation(VERTICAL);
                contentViewWrapperWeiboContent.addView(contentView, layoutParamsText);
                layoutParamsImage.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
                contentViewWrapperWeiboContent.addView(imageView, layoutParamsImage);
            }

            imageView.loadImage();
        }
    }


    public Article getArticle() {
        return article;
    }


}
