package com.goal98.flipdroid.model;

import com.goal98.flipdroid.view.ThumbnailViewContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 5/30/11
 * Time: 7:38 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class Window {
    protected int arrayPos;

    protected boolean isLastWindow;
    protected boolean skip;
    protected boolean loaded;
    protected volatile boolean loading;
    protected int pageNumber;
    protected Lock reloadingLock;

    Window(){

    }
    Window(int arrayPos, int pageNumber, Lock reloadingLock) {
        this.arrayPos = arrayPos;
        this.pageNumber = pageNumber;
        this.reloadingLock = reloadingLock;
    }

    public abstract void startTask();

    public boolean isLoaded() {
        return loaded;
    }

    public boolean isSkip() {
        return skip;
    }

    public boolean isLoading() {
        return loading;
    }

    public abstract ThumbnailViewContainer get();

    public void registerOnLoadListener(OnLoadListener listener) {
        this.addListener(listener);
    }

    protected List<OnLoadListener> onLoadListeners = new ArrayList<OnLoadListener>();

    protected void addListener(OnLoadListener listener) {
        onLoadListeners.add(listener);
    }

    public List<OnLoadListener> getOnLoadListeners() {
        return onLoadListeners;
    }

    public interface OnLoadListener {
        void onWindowLoaded(Window window);

        void onWindowSkipped(Window pageViewWindow);
    }
}
