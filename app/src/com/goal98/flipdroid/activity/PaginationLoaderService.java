package com.goal98.flipdroid.activity;

import com.goal98.flipdroid.exception.NoMorePageException;
import com.goal98.flipdroid.model.NoSuchPageException;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 5/16/11
 * Time: 11:45 PM
 * To change this template use File | Settings | File Templates.
 */
public interface PaginationLoaderService {
    List load(int page, int count) throws NoSuchPageException;
}
