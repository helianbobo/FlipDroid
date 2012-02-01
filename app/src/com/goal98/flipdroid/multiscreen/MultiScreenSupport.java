package com.goal98.flipdroid.multiscreen;

import com.goal98.flipdroid.util.DeviceInfo;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12/28/11
 * Time: 9:32 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class MultiScreenSupport {
    public static MultiScreenSupport getInstance(DeviceInfo info) {
        if (info.getHeight() >= 800) {
            return WGA800.getInstance(info);
        }
        if (info.getHeight() == 480) {
            return WGA480.getInstance(info);
        }
        return null;
    }

    abstract public int getMaxLineInThumbnailView();
    abstract public int[] getTextViewPaddingInThumbnailView();

    public abstract int getTextViewTextSize();

    public abstract int getImageHeightThumbnailView();

    public abstract int getThumbnailMaxTitleLength();

    public abstract int getThumbnailMaxTitleTextSize();
    public abstract int getThumbnailMaxLongTitleTextSize();
    public abstract int getMinTitleHeight();

    public abstract int getTopbarHeight();

    public abstract int getBottomRadioHeight();

    public abstract int getTopBarTextSize();

    public abstract int getBottomBarIconHeight();
}
