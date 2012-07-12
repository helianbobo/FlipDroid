package com.goal98.flipdroid2.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.goal98.flipdroid2.client.OAuth;
import com.goal98.flipdroid2.client.UserInfo;
import com.goal98.flipdroid2.db.AccountDB;
import com.goal98.flipdroid2.db.SourceDB;
import com.goal98.tika.common.TikaConstants;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 5/8/11
 * Time: 9:21 PM
 * To change this template use FileType | Settings | FileType Templates.
 */
public class WeiPaiWebViewClient extends WebViewClient {
    private Activity activity;
    protected AccountDB accountDB;
    protected SourceDB sourceDB;
    protected SharedPreferences preferences;
    public static final String SINA_ACCOUNT_PREF_KEY = "sina_account";
    public static final String PREVIOUS_SINA_ACCOUNT_PREF_KEY = "previous_sina_account";

    private String TAG = this.getClass().getName();
    private Handler handler = new Handler();

    public WeiPaiWebViewClient(Activity context) {
        this.activity = context;
    }

    public void onPageFinished(WebView view, final String url) {
        ////System.out.println("url"+url);
        if (url.indexOf("oauth_verifier") != -1) {
            sourceDB = new SourceDB(activity);
            accountDB = new AccountDB(activity);

            preferences = PreferenceManager.getDefaultSharedPreferences(activity.getBaseContext());
            FlipdroidApplications application = (FlipdroidApplications) activity.getApplication();
            final OAuth oauth = application.getOauth();
            if (oauth != null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        UserInfo user = oauth.GetAccessToken(url);
                        if (user != null) {
                            accountDB.insertOrUpdateOAuth(user.getUserId(), user.getToken(), user.getTokenSecret(), TikaConstants.TYPE_MY_SINA_WEIBO);
                            preferences.edit().putString(SINA_ACCOUNT_PREF_KEY, user.getUserId()).commit();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    activity.finish();
                                }
                            });
                        }
                    }
                }).start();

            }
//            activity.finish();
        }
    }
}
