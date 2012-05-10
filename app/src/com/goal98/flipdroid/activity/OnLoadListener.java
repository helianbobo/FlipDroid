package com.goal98.flipdroid.activity;

import com.srz.androidtools.autoloadlistview.NoSuchPageException;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 5/10/11
 * Time: 11:36 AM
 * To change this template use FileType | Settings | FileType Templates.
 */
public interface OnLoadListener {
    public List load() throws NoSuchPageException;
}
