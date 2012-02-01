package com.goal98.flipdroid.multiscreen;

import com.goal98.flipdroid.util.Constants;
import com.goal98.flipdroid.util.DeviceInfo;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12/28/11
 * Time: 9:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class WGA800 extends MultiScreenSupport {
    private static WGA800 wga800;

    private DeviceInfo deviceInfo;

    private WGA800(DeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public synchronized static MultiScreenSupport getInstance(DeviceInfo deviceInfo) {
        if (wga800 == null) {
            wga800 = new WGA800(deviceInfo);
        }
        return wga800;
    }

    public int getMaxLineInThumbnailView() {
        return 6;
    }

    public int[] getTextViewPaddingInThumbnailView() {
        return new int[]{5, 6, 5, 6};

    }

    public int getTextViewTextSize() {
        return Constants.WEIBO_CONENT_TEXT_SIZE;
    }

    public int getImageHeightThumbnailView() {
        return (getTextViewTextSize() + 15) * getMaxLineInThumbnailView();  //(largeScreen ? 15 : smallScreen ? 0 : 5)
    }

    public int getThumbnailMaxTitleLength() {
        return 35;
    }

    public int getThumbnailMaxTitleTextSize() {
        return 18;
    }

    @Override
    public int getThumbnailMaxLongTitleTextSize() {
        return 16;
    }

    public int getMinTitleHeight() {
        return 70;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getTopbarHeight() {
        return 65;
    }

    @Override
    public int getBottomRadioHeight() {
        return 75;
    }

    public int getTopBarTextSize() {
        return 18;
    }

    public int getBottomBarIconHeight() {
        return 33;
    }
}
