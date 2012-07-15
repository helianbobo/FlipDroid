package com.goal98.girl.view;

import java.util.List;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import com.goal98.girl.R;
import com.goal98.girl.util.ImageUtil;

/**
 * 控制图片翻转
 * 1.点击：将图片分为左右两部分
 * 2.移动：获取移动的距离使之成为页面翻转的角度改变矩阵，根据首次点击判断左右两张图片哪一张翻转哪一张不动并绘图
 * 3. 抬起：判断当前翻转后的角度是否超出规定范围执行对应的翻页操作
 *
 * @author bywyu
 */
public class CubeView extends View {

    private static final String LOGTAG = "CubeView";

    /**
     * 绘图需要的资源
     */
    private Camera mCamera;
    private Matrix mMatrix;
    private Paint mPaint;

    /**
     * turnBmp 翻页成功后显示的图片
     * turnBmpBack 翻页过程中的第二张图片
     * tmpLbmp 显示的左侧图片
     * tmpRbmp 显示的右侧图片
     * Rbmp 当前页面的右侧图片
     * Lbmp 当前页面的左侧图片
     */
    private Bitmap turnBmp, turnBmpBack, tmpLbmp, tmpRbmp, Rbmp, Lbmp;

    /**
     * 阴影距离
     */
    private float shadowX;

    /**
     *
     */
    private int mLastMotionX;

    /**
     * 图片的中心点
     */
    private int centerX, centerY;

    /**
     * 图片反转角度
     */
    private int deltaX;

    /**
     * 图片宽、高
     */
    private int bWidth, bHeight;

    /**
     * 点击后所在点属于图片区域的左（true）、右（false）
     */
    private boolean isLR = true;

    public CubeView(Context context) {
        super(context);
        initView();
    }

    private void initView() {

        mCamera = new Camera();
        mMatrix = new Matrix();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        Bitmap wholeBmp = BitmapFactory.decodeResource(getResources(), R.drawable.book_48x);
        initBmpWH(wholeBmp);
        initLRBmp(wholeBmp);

        Log.d(LOGTAG, "initView centerX = " + centerX + " centerY = " + centerY + " bWidth = " + bWidth);
    }


    public void setBmp(List<Bitmap> bmps) {
        Bitmap wholeBmp = bmps.get(0);
        //下一张需要展示的图片
        this.turnBmp = bmps.get(1);

        initBmpWH(wholeBmp);
        initLRBmp(wholeBmp);
    }

    /**
     * 初始化图片宽高
     */
    private void initBmpWH(Bitmap wholeBmp) {

        bWidth = wholeBmp.getWidth();
        bHeight = wholeBmp.getHeight();

        centerX = bWidth >> 1;
        centerY = bHeight >> 1;
    }


    void rotate(int degreeX, Bitmap turnBmpBack) {

        deltaX += degreeX;

        if (isLR) {

            if (deltaX > 180) {
                deltaX = 180;
            } else if (deltaX < 0) {
                deltaX = 0;
            } else if (deltaX > 90) {
                tmpLbmp = turnBmpBack;
            } else if (deltaX < 90) {
                tmpLbmp = Lbmp;
            }

            mCamera.save();
            mCamera.rotateY(deltaX);
            mCamera.translate(-centerX, 0, 0);
            mCamera.getMatrix(mMatrix);
            mCamera.restore();

            mMatrix.preTranslate(0, -centerY);
            mMatrix.postTranslate(centerX, centerY);

            Log.d(LOGTAG, "rotate you click [LEFT] deltaX " + deltaX);
        } else {

            if (deltaX < -180) {
                deltaX = -180;
            } else if (deltaX > 0) {
                deltaX = 0;
            } else if (deltaX < -90) {

                tmpRbmp = turnBmpBack;
            } else if (deltaX > -90) {
                tmpRbmp = Rbmp;
            }

            mCamera.save();
            mCamera.translate(0, 0, -1);
            mCamera.rotateY(deltaX);
            mCamera.getMatrix(mMatrix);
            mCamera.restore();

            mMatrix.preTranslate(0, -centerY);
            mMatrix.postTranslate(0, centerY);

            Log.d(LOGTAG, "rotate you click [RIGHT] deltaX " + deltaX);
        }

        mCamera.save();

        shadowX = (float) (centerX * Math.cos(Math.abs(deltaX * 1d)));

        Log.d(LOGTAG, "rotate shadowX >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> " + shadowX + " Math.abs(deltaX*1d)) = " + Math.abs(deltaX * 1d) + "Math.cos(Math.abs(deltaX*1d) = " + Math.cos(Math.abs(deltaX * 1d)));
        Log.d(LOGTAG, "rotate shadowX >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Math.cos(75) = " + Math.cos(75.0 * 180 / Math.PI));
        postInvalidate();
    }

    /**
     * 判断下一次是否更换View 供上一层（TurnpageView）调用
     *
     * @return
     */
    public boolean isTurn() {
        boolean isTurn = false;
        if (deltaX >= 90 || deltaX <= -90) {
            isTurn = true;
        }
        return isTurn;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isLR = isLR(x);
                turnBmpBack = cutTurnBmp(isLR);
                mLastMotionX = x;
                break;
            case MotionEvent.ACTION_MOVE:
                int dx = x - mLastMotionX;
                rotate(dx, turnBmpBack);
                mLastMotionX = x;
                break;
            case MotionEvent.ACTION_UP:
                isTurnpage(turnBmpBack);
                turnBmpBack = null;
                break;
        }
        return true;
    }

    /**
     * 根据放手时的角度判断是否翻页
     */
    private void isTurnpage(Bitmap turnBmpBack) {
        if (isLR) {
            if (deltaX >= 90) {
                rotate(180, turnBmpBack);
            } else {
                deltaX = 0;
                rotate(0, turnBmpBack);
            }
        } else {
            if (deltaX < -90) {
                rotate(-180, turnBmpBack);
            } else {
                deltaX = 0;
                rotate(0, turnBmpBack);
            }
        }
    }

    /**
     * 截取第二张图片供翻页切换使用
     *
     * @param isLR
     * @return
     */
    private Bitmap cutTurnBmp(boolean isLR) {
        Matrix matrix = new Matrix();
        matrix.preScale(-1, 1);
        Bitmap turnBmpBack;
        if (isLR) {
            turnBmpBack = Bitmap.createBitmap(turnBmp, centerX, 0, centerX, bHeight, matrix, false);
        } else {
            turnBmpBack = Bitmap.createBitmap(turnBmp, 0, 0, centerX, bHeight, matrix, false);
        }
        return turnBmpBack;
    }

    /**
     * 判断x点所在视图区域
     *
     * @param x
     */
    private boolean isLR(int x) {
        boolean isLR = true;
        if (deltaX != 180 && deltaX != -180) {
            if (x > 0 && x < centerX) {
                isLR = true;
                Log.d(LOGTAG, "SegmentationBmp you click [LEFT] ");
            } else if ((x > centerX && x < bWidth) || x == 0) {
                isLR = false;
                Log.d(LOGTAG, "SegmentationBmp you click [RIGHT] ");
            }
        }
        return isLR;
    }

    @Override
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        if (isLR) {
            canvas.drawBitmap(tmpRbmp, centerX, 0, mPaint);
            canvas.drawBitmap(tmpLbmp, mMatrix, mPaint);

//			Paint paint = new Paint();
//			Shader linearShader = new LinearGradient( 0, 0, centerX -shadowX, bHeight,Color.argb(0, 0, 0, 0), Color.argb(70, 0, 0, 0), TileMode.CLAMP);
//			paint.setShader(linearShader);
//			canvas.drawRect( 0, 0, centerX - shadowX, bHeight, paint);

        } else {
            canvas.drawBitmap(tmpLbmp, 0, 0, mPaint);
            canvas.translate(centerX, 0);
            canvas.drawBitmap(tmpRbmp, mMatrix, mPaint);

//			Paint paint = new Paint();
//			Shader linearShader = new LinearGradient(centerX+shadowX, 0, bWidth, bHeight,Color.argb(0, 0, 0, 0), Color.argb(70, 0, 0, 0), TileMode.CLAMP);
//			paint.setShader(linearShader);
//			canvas.drawRect(bWidth - (centerX+shadowX), 0, bWidth, bHeight, paint);
        }

    }

    /**
     * 将图片截取为左右两部分图片
     */
    private void initLRBmp(Bitmap wholeBmp) {

        Lbmp = Bitmap.createBitmap(wholeBmp, 0, 0, centerX, bHeight);
        Rbmp = Bitmap.createBitmap(wholeBmp, centerX, 0, centerX, bHeight);

        tmpLbmp = Lbmp;
        tmpRbmp = Rbmp;
    }

    /**
     * 释放图片资源
     */
    public void destory() {

        ImageUtil.recycleBmp(Lbmp);
        ImageUtil.recycleBmp(turnBmp);
        ImageUtil.recycleBmp(tmpLbmp);
        ImageUtil.recycleBmp(tmpRbmp);
        ImageUtil.recycleBmp(Rbmp);
        ImageUtil.recycleBmp(Lbmp);
        ImageUtil.recycleBmp(turnBmpBack);
    }
}
