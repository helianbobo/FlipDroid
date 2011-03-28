package com.goal98.flipdroid.model;

import com.goal98.flipdroid.exception.NoMorePageException;
import com.goal98.flipdroid.view.Page;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: lsha6086
 * Date: 3/24/11
 * Time: 1:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class PagedArticles {
    private List<Page> pageList;

    public PagedArticles(){
        this.pageList = new ArrayList();
    }

    public Page getPage(int pageNo) throws NoMorePageException {
        if (pageNo >= pageList.size()) {
            throw new NoMorePageException();
        }
        return pageList.get(pageNo);
    }

    public void add(Page page) {
        pageList.add(page);
    }

    public void addAll(List<Page> pages) {
        pageList.addAll(pages);
    }

    public int size() {
        return pageList.size();
    }

    public List<Page> getPages() {
        return pageList;
    }
}
