package com.goal98.flipdroid.model;

import android.content.Context;
import android.database.Cursor;
import com.goal98.flipdroid.db.SourceDB;
import com.goal98.flipdroid.model.cachesystem.SourceCache;
import com.goal98.flipdroid.util.Constants;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 8/11/11
 * Time: 11:23 AM
 * To change this template use File | Settings | File Templates.
 */
public class SourceUpdateManager {
    private Context context;
    public SourceDB sourceDB;
    public SourceCache sourceCache;

    public SourceUpdateManager(Context context) {
        this.context = context;
        this.sourceDB = new SourceDB(context);
        this.sourceCache = new SourceCache(context);
    }

    public void updateAll() {
        Cursor c = null;
        try {
            c = sourceDB.findAll();
            while (c.moveToNext()) {
                String sourceType = c.getString(c.getColumnIndex(Source.KEY_SOURCE_TYPE));
                if (sourceType.equals(Constants.TYPE_RSS)) {

                }
            }
        } finally {
            if (c != null)
                c.close();
        }
    }
}
