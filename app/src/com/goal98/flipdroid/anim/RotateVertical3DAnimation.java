package com.goal98.flipdroid.anim;

import android.graphics.Camera;
import android.graphics.Color;
import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;

/**
 * An animation that rotates the view on the Y axis between two specified angles.
 * This animation also adds a translation on the Z axis (depth) to improve the effect.
 */
public class RotateVertical3DAnimation extends Animation {
    private float mFromDegrees;
    private float mToDegrees;
    private float mCenterX;
    private float mCenterY;
    private float mDepthZ;
    private boolean mReverse;
    private Camera mCamera;
    private LinearLayout shadow1;
    private LinearLayout shadow2;

    /**
     * Creates a new 3D rotation on the Y axis. The rotation is defined by its
     * start angle and its end angle. Both angles are in degrees. The rotation
     * is performed around a center point on the 2D space, definied by a pair
     * of X and Y coordinates, called centerX and centerY. When the animation
     * starts, a translation on the Z axis (depth) is performed. The length
     * of the translation can be specified, as well as whether the translation
     * should be reversed in time.
     *
     * @param fromDegrees the start angle of the 3D rotation
     * @param toDegrees   the end angle of the 3D rotation
     * @param centerX     the X center of the 3D rotation
     * @param centerY     the Y center of the 3D rotation
     * @param reverse     true if the translation should be reversed, false otherwise
     */
    public RotateVertical3DAnimation(float fromDegrees, float toDegrees,
                             float centerX, float centerY, float depthZ, boolean reverse, LinearLayout shadow1, LinearLayout shadow2) {
        mFromDegrees = fromDegrees;
        mToDegrees = toDegrees;
        mCenterX = centerX;
        mCenterY = centerY;
        mDepthZ = depthZ;
        mReverse = reverse;
        this.shadow1 = shadow1;
        this.shadow2 = shadow2;
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
        mCamera = new Camera();
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        final float fromDegrees = mFromDegrees;
        float degrees = fromDegrees + ((mToDegrees - fromDegrees) * interpolatedTime);
//        shadow1.setBackgroundColor(Color.parseColor("#15999999"));
//        int c = Color.argb((int) (100 * (1-interpolatedTime)), 153, 153, 153);
        int c2 = 0;
        if(Math.abs(mToDegrees) - Math.abs(fromDegrees) > 0)
            c2 = Color.argb((int) (255 * (1-interpolatedTime)), 153, 153, 153);
        else
            c2 = Color.argb((int) (255 * interpolatedTime), 153, 153, 153);
//
//
//        //System.out.println((100 * (1-interpolatedTime)));
        shadow2.setBackgroundColor(c2);
//        shadow1.setBackgroundColor(c2);
        final float centerX = mCenterX;
        final float centerY = mCenterY;
        final Camera camera = mCamera;

        final Matrix matrix = t.getMatrix();

        camera.save();
        if (mReverse) {
            camera.translate(0.0f, 0.0f, mDepthZ * interpolatedTime);
        } else {
            camera.translate(0.0f, 0.0f, mDepthZ * (1.0f - interpolatedTime));
        }
        camera.rotateX(degrees);
        camera.getMatrix(matrix);
        camera.restore();

        matrix.preTranslate(-centerX, -centerY);
        matrix.postTranslate(centerX, centerY);
    }

    public void setmCenterX(float mCenterX) {
        this.mCenterX = mCenterX;
    }

    public void setmCenterY(float mCenterY) {
        this.mCenterY = mCenterY;
    }

    public void setmDepthZ(float mDepthZ) {
        this.mDepthZ = mDepthZ;
    }

    public void setmFromDegrees(float mFromDegrees) {
        this.mFromDegrees = mFromDegrees;
    }

    public void setmReverse(boolean mReverse) {
        this.mReverse = mReverse;
    }

    public void setmToDegrees(float mToDegrees) {
        this.mToDegrees = mToDegrees;
    }
}
