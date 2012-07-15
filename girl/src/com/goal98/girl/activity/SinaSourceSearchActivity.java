package com.goal98.girl.activity;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import com.goal98.girl.db.AccountDB;
import com.goal98.girl.model.Account;
import com.goal98.girl.model.SearchSource;
import com.goal98.girl.model.sina.SinaSearchSource;
import com.goal98.tika.common.TikaConstants;

public class SinaSourceSearchActivity extends SourceSearchActivity {
    public SearchSource getSearchSource() {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        AccountDB accountDB = new AccountDB(this);

        String token = null;
        String tokenSecret = null;
        String userId = preferences.getString(WeiPaiWebViewClient.SINA_ACCOUNT_PREF_KEY, null);
        Cursor cursor = null;
        try {
            cursor = accountDB.findByTypeAndUsername(TikaConstants.TYPE_MY_SINA_WEIBO, userId);
            cursor.moveToFirst();
            token = cursor.getString(cursor.getColumnIndex(Account.KEY_PASSWORD));
            tokenSecret = cursor.getString(cursor.getColumnIndex(Account.KEY_PASSWORD_SECRET));
        } finally {
            if (cursor != null)
                cursor.close();
        }

        return new SinaSearchSource(true, token, tokenSecret, null);
    }
}