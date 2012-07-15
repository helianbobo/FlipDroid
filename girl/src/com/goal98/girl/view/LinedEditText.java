package com.goal98.girl.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 6/3/11
 * Time: 10:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class LinedEditText extends EditText {
    private Rect mRect;
    private Paint mPaint;

    // we need this constructor for LayoutInflater
    public LinedEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        mRect = new Rect();
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.parseColor("#D0D0D0"));
    }

    public LinedEditText(Context context) {
        super(context);

        mRect = new Rect();
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.parseColor("#D0D0D0"));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int count = getLineCount();
        Rect r = mRect;
        Paint paint = mPaint;
        int baseline = getLineBounds(0, r);
        for (int i = 0; i < 12; i++) {
            canvas.drawLine(r.left, (baseline + 3) * (i + 1), r.right, (baseline + 3) * (i + 1), paint);
        }
        super.onDraw(canvas);
    }
}