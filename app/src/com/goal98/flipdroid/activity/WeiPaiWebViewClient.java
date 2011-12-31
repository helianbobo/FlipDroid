package com.goal98.flipdroid.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.client.OAuth;
import com.goal98.flipdroid.client.UserInfo;
import com.goal98.flipdroid.db.AccountDB;
import com.goal98.flipdroid.db.SourceDB;
import com.goal98.flipdroid.util.Constants;
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

                    try {
                        if(!sourceDB.isMySinaWeiboAccountExist())
                            sourceDB.insert(TikaConstants.TYPE_MY_SINA_WEIBO, activity.getString(R.string.my_timeline), Constants.SOURCE_HOME, activity.getString(R.string.my_timeline_desc), null,"mysina", "http://www.sinaimg.cn/blog/developer/wiki/48x48.png");
                    } catch (Exception e) {
                        Log.w(TAG, e.getMessage(), e);
                    }
                    accountDB.insertOrUpdateOAuth(user.getUserId(), user.getToken(), user.getTokenSecret(), TikaConstants.TYPE_MY_SINA_WEIBO);
                    preferences.edit().putString(SINA_ACCOUNT_PREF_KEY, user.getUserId()).commit();

//                    activity.startActivity(new Intent(activity, IndexActivity.class));

                    activity.finish();
                }
            }
            activity.finish();
        }
    }
}
