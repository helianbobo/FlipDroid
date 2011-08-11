package com.goal98.flipdroid.util;

import android.database.Cursor;

/**
 * Created by IntelliJ IDEA.
 * User: ITS
 * Date: 11-8-11
 * Time: 下午1:55
 * To change this template use File | Settings | File Templates.
 */
public class ManagedCursor {
    private Cursor cursor;

    public ManagedCursor(Cursor cursor) {
        this.cursor = cursor;
    }

    public void each(EachCursor ec) {
        int i = 0;
        try {
            while (cursor.moveToNext()) {
                ec.call(cursor, i++);
            }
        } finally {
            cursor.close();
        }
    }
}
