package com.goal98.flipdroid.util;

import android.database.Cursor;

/**
 * Created by IntelliJ IDEA.
 * User: ITS
 * Date: 11-8-11
 * Time: 下午1:57
 * To change this template use File | Settings | File Templates.
 */
public interface EachCursor {
    public void call(Cursor cursor, int index);
}
