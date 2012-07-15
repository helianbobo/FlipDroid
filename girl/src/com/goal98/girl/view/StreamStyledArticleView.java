package com.goal98.girl.view;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.goal98.android.WebImageView;
import com.goal98.girl.R;
import com.goal98.girl.activity.ArticleDetailInfo;
import com.goal98.girl.model.Article;
import com.goal98.girl.multiscreen.MultiScreenSupport;
import com.goal98.girl.util.Constants;
import com.srz.androidtools.util.DeviceInfo;
import com.goal98.girl.util.NetworkUtil;
import com.goal98.girl.util.PrettyTimeUtil;
import com.srz.androidtools.autoloadlistview.HeavyUIOperater;
import com.srz.androidtools.autoloadlistview.ItemView;

import java.util.concurrent.ExecutorService;

/**
 * Created by IntelliJ IDEA.
 * User: janexie
 * Date: 12-1-26
 * Time: 下午8:40
 * To change this template use File | Settings | File Templates.
 */
public class StreamStyledArticleView<T> extends ItemView implements HeavyUIOperater {
    private boolean toLoadImage;
    Handler handler = new Handler();
    private Article article;
    private WebImageView imageView;
    private WebImageView portraitViewWeiboContent;

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
//        toLoadImage = attrs.getAttributeBooleanValue(XMLNS,
//                "toLoadFromInternet",  NetworkUtil.toLoadImage(this.getContext()));
        toLoadImage = NetworkUtil.toLoadImage(this.getContext());
    }


    public void render(Object di) {
        ArticleDetailInfo articleDetailInfo = (ArticleDetailInfo) di;
        article = articleDetailInfo.getArticle();

        TextView titleView = (TextView) findViewById(R.id.title);

        TextView authorViewWeiboContent = (TextView) findViewById(R.id.author);

        TextView createDateViewWeiboContent = (TextView) findViewById(R.id.createDate);

        LinearLayout contentViewWrapperWeiboContent = (LinearLayout) findViewById(R.id.contentll);

        TextView contentView = new TextView(this.getContext());
        final DeviceInfo deviceInfo = DeviceInfo.getInstance((Activity) this.getContext());

        ThumbnailContentRender.setThumbnailContentText(contentView, article, deviceInfo);
        ThumbnailContentRender.setTitleText(titleView, article, deviceInfo);
        new ArticleTextViewRender(Constants.INDENT).renderTextView(contentView, article);

        portraitViewWeiboContent = (WebImageView) findViewById(R.id.portrait2);
        portraitViewWeiboContent.setAutoSize(true);
        authorViewWeiboContent.setText(article.getAuthor());
        String localeStr = this.getContext().getString(R.string.locale);
        String time = PrettyTimeUtil.getPrettyTime(localeStr, article.getCreatedDate());
        createDateViewWeiboContent.setText(time);

        if (article.getPortraitImageUrl() != null) {
            portraitViewWeiboContent.setImageUrl(article.getPortraitImageUrl().toString());
        } else {
            portraitViewWeiboContent.setVisibility(GONE);
        }

        MultiScreenSupport mss = MultiScreenSupport.getInstance(deviceInfo);
        if (article.getImageUrl() == null) {
            LayoutParams layoutParams = new LayoutParams(0, LayoutParams.FILL_PARENT);
            layoutParams.weight = 100;
            contentViewWrapperWeiboContent.addView(contentView, layoutParams);
        } else {
            imageView = new WebImageView(this.getContext(), article.getImageUrl().toExternalForm(), this.getResources().getDrawable(Constants.DEFAULT_PIC), this.getResources().getDrawable(Constants.DEFAULT_PIC), false, toLoadImage, ImageView.ScaleType.CENTER_CROP);
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
                LayoutParams layoutParamsText = new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
                LayoutParams layoutParamsImage = new LayoutParams(0, mss.getImageHeightThumbnailView());

                layoutParamsImage.gravity = Gravity.CENTER;
                layoutParamsText.weight = 50;
                layoutParamsImage.weight = 50;

                if (false) {
                    contentViewWrapperWeiboContent.addView(contentView, layoutParamsText);
                    contentViewWrapperWeiboContent.addView(imageView, layoutParamsImage);
                } else {
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
        }
    }


    public Article getArticle() {

        return article;
    }


    public synchronized void heavyUIOperation() {
        System.out.println("triggerred heavy");
        if (imageView != null){
//            imageView.setInAnimation(this.getContext(), android.R.anim.fade_in);
            imageView.loadImage();
        }
        portraitViewWeiboContent.loadImage();
    }
}
