package com.goal98.flipdroid2.activity;

import android.app.Application;
import com.goal98.flipdroid2.client.OAuth;
import com.srz.androidtools.util.DeviceInfo;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 6/23/11
 * Time: 9:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class FlipdroidApplications extends Application {
    private DeviceInfo deviceInfo;

    public OAuth getOauth() {
        return oauth;
    }

    public void setOauth(OAuth oauth) {
        this.oauth = oauth;
    }

    private OAuth oauth;


    public void setDeviceInfo(DeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public DeviceInfo getDeviceInfo() {
        return deviceInfo;
    }
}
