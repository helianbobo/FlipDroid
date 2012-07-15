package com.goal98.girl.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 6/21/11
 * Time: 11:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class FlipPage extends LinearLayout {
    public FlipPage(Context context) {
        super(context);
        setChildrenDrawingOrderEnabled(true);
    }

    public FlipPage(Context context, AttributeSet attrs) {
        super(context, attrs);
        setChildrenDrawingOrderEnabled(true);
    }

    protected int getChildDrawingOrder(int childCount, int i) {
        if(i == 0){
            return 0;
        }
        if(i == 1){
            return 1;
        }
        return 2;
    }
}
