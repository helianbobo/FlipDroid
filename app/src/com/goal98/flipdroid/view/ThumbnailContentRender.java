package com.goal98.flipdroid.view;

import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;
import com.goal98.flipdroid.model.Article;
import com.goal98.flipdroid.multiscreen.MultiScreenSupport;
import com.goal98.flipdroid.util.Constants;
import com.srz.androidtools.util.DeviceInfo;

/**
 * Created by IntelliJ IDEA.
 * User: janexie
 * Date: 12-1-27
 * Time: 上午11:16
 * To change this template use File | Settings | File Templates.
 */
public class ThumbnailContentRender {

    public static void setTitleText(TextView titleView, Article article, DeviceInfo deviceInfo) {
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

     public static void setThumbnailContentText(TextView contentView, Article article, DeviceInfo deviceInfo) {
        MultiScreenSupport mss = MultiScreenSupport.getInstance(deviceInfo);

        int maxLines = mss.getMaxLineInThumbnailView();
        int[] paddings = mss.getTextViewPaddingInThumbnailView();

        int textSize = 0;
        textSize = mss.getTextViewTextSize();


        contentView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize);
         contentView.setLineSpacing(0,1.1f);
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
}
