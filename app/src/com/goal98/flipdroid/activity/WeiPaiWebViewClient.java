package com.goal98.flipdroid.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.client.OAuth;
import com.goal98.flipdroid.client.UserInfo;
import com.goal98.flipdroid.db.AccountDB;
import com.goal98.flipdroid.db.SourceDB;
import com.goal98.flipdroid.util.Constants;

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

    public WeiPaiWebViewClient(Activity context) {
        this.activity = context;
    }

    public void onPageFinished(WebView view, String url) {
        ////System.out.println("url"+url);
        if (url.indexOf("oauth_verifier") != -1) {
            sourceDB = new SourceDB(activity);
            accountDB = new AccountDB(activity);

            preferences = PreferenceManager.getDefaultSharedPreferences(activity.getBaseContext());
            FlipdroidApplications application = (FlipdroidApplications) activity.getApplication();
            final OAuth oauth = application.getOauth();
            if (oauth != null) {
                UserInfo user = oauth.GetAccessToken(url);
                if (user != null) {
                    sourceDB.insert(Constants.TYPE_MY_SINA_WEIBO, activity.getString(R.string.my_timeline), Constants.SOURCE_HOME, activity.getString(R.string.my_timeline_desc), null,"mysina");
                    accountDB.insertOrUpdateOAuth(user.getUserId(), user.getToken(), user.getTokenSecret(), Constants.TYPE_MY_SINA_WEIBO);
                    preferences.edit().putString(SINA_ACCOUNT_PREF_KEY, user.getUserId()).commit();

                    activity.startActivity(new Intent(activity, IndexActivity.class));

                    activity.finish();
                }
            }
            activity.finish();
        }
    }
}
