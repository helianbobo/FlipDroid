package com.goal98.tika.common;

import android.view.View;

/**
 * Created by IntelliJ IDEA.
 * User: ITS
 * Date: 11-8-15
 * Time: 下午4:46
 * To change this template use File | Settings | File Templates.
 */
public interface TikaUIObject {
    public static final String TYPE_IMAGE = "IMAGE";
    public static final String TYPE_TEXT = "TEXT";

    public String getType();

    public String getObjectBody();

    public String getOutput();
}
