package com.goal98.flipdroid.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import com.goal98.flipdroid.activity.WeiPaiWebViewClient;
import com.goal98.flipdroid.db.AccountDB;
import com.goal98.flipdroid.model.Account;
import com.goal98.flipdroid.model.sina.SinaToken;
import com.goal98.tika.common.TikaConstants;

/**
 * Created by IntelliJ IDEA.
 * User: ITS
 * Date: 11-7-5
 * Time: 下午1:29
 * To change this template use File | Settings | File Templates.
 */
public class SinaAccountUtil {
    public static boolean alreadyBinded(Context context) {
        AccountDB accountDB = null;
        try {
            accountDB = new AccountDB(context);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            boolean binded = preferences.getString(WeiPaiWebViewClient.SINA_ACCOUNT_PREF_KEY, null) != null;

            return binded;
        } finally {
            if (accountDB != null)
                accountDB.close();
        }
    }

    public static SinaToken getToken(Context context) {
        String token = null;
        String tokenSecret = null;

        AccountDB accountDB = new AccountDB(context);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        String userId = preferences.getString(WeiPaiWebViewClient.SINA_ACCOUNT_PREF_KEY, null);
        if (userId == null)
            userId = preferences.getString(WeiPaiWebViewClient.PREVIOUS_SINA_ACCOUNT_PREF_KEY, null);

        Cursor cursor = accountDB.findByTypeAndUsername(TikaConstants.TYPE_MY_SINA_WEIBO, userId);
        try {
            cursor.moveToFirst();
            token = cursor.getString(cursor.getColumnIndex(Account.KEY_PASSWORD));
            tokenSecret = cursor.getString(cursor.getColumnIndex(Account.KEY_PASSWORD_SECRET));
        } finally {
            cursor.close();
            accountDB.close();
        }

        SinaToken sinaToken = new SinaToken();
        sinaToken.setToken(token);
        sinaToken.setTokenSecret(tokenSecret);

        return sinaToken;
    }
}
