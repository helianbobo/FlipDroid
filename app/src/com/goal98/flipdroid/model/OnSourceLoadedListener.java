package com.goal98.flipdroid.model;

import com.goal98.flipdroid.client.LastModifiedStampedResult;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: ITS
 * Date: 11-7-29
 * Time: 下午1:44
 * To change this template use File | Settings | File Templates.
 */
public interface OnSourceLoadedListener {
    public String onLoaded(LastModifiedStampedResult s) throws IOException;
}
