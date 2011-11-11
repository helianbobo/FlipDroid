package com.goal98;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class CardScrollView extends ViewGroup {

    public CardScrollView(Context context) {
        super(context);
    }

    public CardScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CardScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec),
                getDefaultSize(0, heightMeasureSpec));

        int mChildWidthMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth() -
                getPaddingLeft() - getPaddingRight(), MeasureSpec.EXACTLY);
        int mChildHeightMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredHeight() -
                getPaddingTop() - getPaddingBottom(), MeasureSpec.EXACTLY);


        final int size = getChildCount();
        for (int i = 0; i < size; ++i) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                child.measure(mChildWidthMeasureSpec, mChildHeightMeasureSpec);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        final int count = getChildCount();

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {

                int childLeft = getPaddingLeft();
                int childTop = getPaddingTop();
                child.layout(childLeft, childTop,
                        childLeft + child.getMeasuredWidth(),
                        childTop + child.getMeasuredHeight());
            }
        }

    }
}
