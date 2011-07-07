package com.goal98.flipdroid.activity;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import com.goal98.flipdroid.db.AccountDB;
import com.goal98.flipdroid.model.Account;
import com.goal98.flipdroid.model.SearchSource;
import com.goal98.flipdroid.model.sina.SearchSourceTask;
import com.goal98.flipdroid.model.sina.SinaSearchSource;
import com.goal98.flipdroid.util.Constants;

public class SinaSourceSearchActivity extends SourceSearchActivity {
    public SearchSource getSearchSource() {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        AccountDB accountDB = new AccountDB(this);

        String token = null;
        String tokenSecret = null;
        String userId = preferences.getString(WeiPaiWebViewClient.SINA_ACCOUNT_PREF_KEY, null);
        Cursor cursor = accountDB.findByTypeAndUsername(Constants.TYPE_MY_SINA_WEIBO, userId);
        cursor.moveToFirst();
        token = cursor.getString(cursor.getColumnIndex(Account.KEY_PASSWORD));
        tokenSecret = cursor.getString(cursor.getColumnIndex(Account.KEY_PASSWORD_SECRET));
        cursor.close();

        return new SinaSearchSource(true, token, tokenSecret, null);
    }
}