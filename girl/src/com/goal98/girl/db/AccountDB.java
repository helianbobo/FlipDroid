package com.goal98.girl.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.goal98.girl.model.Account;

public class AccountDB extends AbstractDB{


    public AccountDB(Context context) {
        super(context);
    }

    @Override
    protected String getTableName() {
        return Account.TABLE_NAME;
    }

    public boolean exist(String username, String accountType) {
        String[] projection = {Account.KEY_USERNAME, Account.KEY_ACCOUNT_TYPE};
        String selection = Account.KEY_USERNAME + " = ? and " + Account.KEY_ACCOUNT_TYPE + " = ?";
        String[] selectionArgs = {username, accountType};

        Cursor cursor = query(projection, selection, selectionArgs, null);
        boolean result = cursor != null && cursor.getCount() > 0;
        if(cursor!=null)
            cursor.close();
        return result;

    }

    public Cursor findByType(String accountType){
        String[] projection = null;
        String selection = Account.KEY_ACCOUNT_TYPE + " = ?";
        String[] selectionArgs = {accountType};

        Cursor cursor = query(projection, selection, selectionArgs, null);
        return cursor;
    }

    public Cursor findByTypeAndUsername(String accountType, String username){
        String[] projection = null;
        String selection = Account.KEY_ACCOUNT_TYPE + " = ? AND " + Account.KEY_USERNAME + " = ?";
        String[] selectionArgs = {accountType, username};

        Cursor cursor = query(projection, selection, selectionArgs, null);
        return cursor;
    }

    public long insert(ContentValues values) {
        SQLiteDatabase db = helper.getWritableDatabase();
        return db.insert(Account.TABLE_NAME, Account.KEY_ACCOUNT_TYPE, values);
    }

    public long insert(String username, String password, String accountType) {
        ContentValues values = new ContentValues();
        values.put(Account.KEY_USERNAME, username);
        values.put(Account.KEY_PASSWORD, password);
        values.put(Account.KEY_ACCOUNT_TYPE, accountType);
        return insert(values);
    }

    public long insertOrUpdate(String username, String password, String accountType) {

        boolean accountExsit = exist(username, accountType);

        ContentValues values = new ContentValues();
        values.put(Account.KEY_USERNAME, username);
        values.put(Account.KEY_PASSWORD, password);
        values.put(Account.KEY_ACCOUNT_TYPE, accountType);

        if (!accountExsit) {
            return insert(values);
        } else {
            return update(values, Account.KEY_USERNAME + " = ? and " + Account.KEY_ACCOUNT_TYPE + " = ?", new String[]{username, accountType});
        }


    }

    public long insertOrUpdateOAuth(String id, String token, String tokenSecret, String accountType) {

        boolean accountExsit = exist(id, accountType);

        ContentValues values = new ContentValues();
        values.put(Account.KEY_USERNAME, id);
        values.put(Account.KEY_PASSWORD, token);
        values.put(Account.KEY_PASSWORD_SECRET, tokenSecret);
        values.put(Account.KEY_ACCOUNT_TYPE, accountType);

        if (!accountExsit) {
            return insert(values);
        } else {
            return update(values, Account.KEY_USERNAME + " = ? and " + Account.KEY_ACCOUNT_TYPE + " = ?", new String[]{id, accountType});
        }


    }

    public boolean hasAccount(String type) {
        String[] projection = {Account.KEY_ACCOUNT_TYPE};
        String selection = Account.KEY_ACCOUNT_TYPE + " = ?";
        String[] selectionArgs = {type};

        Cursor cursor = query(projection, selection, selectionArgs, null);
        boolean result = cursor != null && cursor.getCount() > 0;
        if(cursor != null){
            cursor.close();
        }
        return result;
    }
}
