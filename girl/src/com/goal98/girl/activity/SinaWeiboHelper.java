package com.goal98.girl.activity;

import android.app.Activity;
import android.preference.PreferenceManager;
import com.goal98.girl.client.WeiboExt;
import com.goal98.girl.exception.NoSinaAccountBindedException;
import com.goal98.girl.model.Article;
import com.goal98.girl.model.sina.SinaToken;
import com.goal98.girl.util.Constants;
import com.goal98.girl.util.SinaAccountUtil;
import weibo4j.Weibo;
import weibo4j.WeiboException;

public class SinaWeiboHelper {
    private Weibo weibo;
    public SinaToken sinaToken;
    private Activity activity;

    public SinaWeiboHelper(Activity activity) {
        this.activity = activity;
    }

    public void comment(String comment, Article article) throws WeiboException, NoSinaAccountBindedException {
        String userId =  PreferenceManager.getDefaultSharedPreferences(activity).getString(WeiPaiWebViewClient.SINA_ACCOUNT_PREF_KEY, null);
        if (userId == null)
            throw new NoSinaAccountBindedException();

        if (weibo == null) {
            initSinaWeibo();
        }
        weibo.updateStatus(comment, article.getStatusId());
    }

    public void forward(String comment, Article article) throws WeiboException, NoSinaAccountBindedException {
        String userId = PreferenceManager.getDefaultSharedPreferences(activity).getString("sina_account", null);
        if (userId == null)
            throw new NoSinaAccountBindedException();

        if (weibo == null) {
            initSinaWeibo();
        }


        weibo.updateStatus(comment);
    }

    void initSinaWeibo() {
        System.setProperty("weibo4j.oauth.consumerKey", Constants.CONSUMER_KEY);
        System.setProperty("weibo4j.oauth.consumerSecret", Constants.CONSUMER_SECRET);

        weibo = new WeiboExt();

        weibo.setHttpConnectionTimeout(5000);
        if (sinaToken== null)
            sinaToken = SinaAccountUtil.getToken(activity);

        weibo.setToken(sinaToken.getToken(), sinaToken.getTokenSecret());
    }
}