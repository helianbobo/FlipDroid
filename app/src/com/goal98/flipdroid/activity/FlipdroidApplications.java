package com.goal98.flipdroid.activity;

import android.app.Application;
import com.goal98.flipdroid.client.OAuth;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 6/23/11
 * Time: 9:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class FlipdroidApplications extends Application {
    public OAuth getOauth() {
        return oauth;
    }

    public void setOauth(OAuth oauth) {
        this.oauth = oauth;
    }

    private OAuth oauth;


}
