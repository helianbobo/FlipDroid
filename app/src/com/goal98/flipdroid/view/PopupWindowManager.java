package com.goal98.flipdroid.view;

import android.widget.PopupWindow;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2/2/12
 * Time: 12:35 AM
 * To change this template use File | Settings | File Templates.
 */
public class PopupWindowManager {
    private PopupWindow window;
    private static PopupWindowManager manager;

    private PopupWindowManager() {

    }

    public void setWindow(PopupWindow window) {
        this.window = window;
    }

    public void dismissIfShowing() {
        if (window != null && window.isShowing() && window.isTouchable() ) {

            window.dismiss();
            window = null;
        }
    }

    public static synchronized PopupWindowManager getInstance() {
        if (manager == null) {
            manager = new PopupWindowManager();
        }
        return manager;
    }
}
