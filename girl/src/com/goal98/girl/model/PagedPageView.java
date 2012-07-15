package com.goal98.girl.model;

import com.goal98.girl.exception.NoMorePageException;
import com.goal98.girl.view.Page;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: lsha6086
 * Date: 3/24/11
 * Time: 1:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class PagedPageView {
    private List<Page> pages;

    public PagedPageView() {
        this.pages = new ArrayList();
    }

    public Page getPage(int pageNo) throws NoMorePageException {
        if (pageNo >= pages.size()) {
            throw new NoMorePageException();
        }
        return pages.get(pageNo);
    }

    public void add(Page smartPage) {
        pages.add(smartPage);
    }

    public void addAll(List<Page> smartPages) {
        pages.addAll(smartPages);
    }

    public int size() {
        return pages.size();
    }

    public List<Page> getPages() {
        return pages;
    }

    public void clear() {
        pages.clear();
    }
}
