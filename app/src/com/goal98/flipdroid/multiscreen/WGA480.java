package com.goal98.flipdroid.multiscreen;

import com.goal98.flipdroid.util.Constants;
import com.goal98.flipdroid.util.DeviceInfo;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12/28/11
 * Time: 11:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class WGA480 extends MultiScreenSupport {
    private static WGA480 wga480;

    private DeviceInfo deviceInfo;

    private WGA480(DeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public synchronized static MultiScreenSupport getInstance(DeviceInfo deviceInfo) {
        if (wga480 == null) {
            wga480 = new WGA480(deviceInfo);
        }
        return wga480;
    }

    public int getMaxLineInThumbnailView() {
        return 4;
    }

    public int[] getTextViewPaddingInThumbnailView() {
        return new int[]{5, 4, 5, 4};
    }

    public int getTextViewTextSize() {
        return Constants.WEIBO_CONENT_TEXT_SIZE;
    }

    public int getImageHeightThumbnailView() {
        return (getTextViewTextSize() * 2) * getMaxLineInThumbnailView();  //(largeScreen ? 15 : smallScreen ? 0 : 5)
    }

    public int getThumbnailMaxTitleLength() {
        return 20;
    }

    public int getThumbnailMaxTitleTextSize() {
        return 17;
    }

    @Override
    public int getThumbnailMaxLongTitleTextSize() {
        return 15;
    }
}
