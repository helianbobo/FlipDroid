package com.goal98.flipdroid.anim;

import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;

public class AnimationFactory {

    public static Animation buildHorizontalFlipAnimation(boolean forward, long duration, float centerX, float centerY) {

        final float fromDegrees = forward ? 0 : -90;
        final float toDegrees = forward ? -90 : 0;
        float depthZ = 0.0f;
        boolean reverse = true;

        RotateHorizontal3DAnimation rotation = new RotateHorizontal3DAnimation(fromDegrees, toDegrees, centerX, centerY, depthZ, reverse);
        rotation.setDuration(duration);
        rotation.setFillAfter(false);
        rotation.setInterpolator(new DecelerateInterpolator());

        return rotation;

    }

    public static Animation buildVerticalFlipAnimation(int fromDegrees, int toDegrees, long duration, float centerX, float centerY, LinearLayout shadow1, LinearLayout shadow2) {
        float depthZ = 0.0f;
        boolean reverse = true;

        RotateVertical3DAnimation rotation = new RotateVertical3DAnimation(fromDegrees, toDegrees, centerX, centerY, depthZ, reverse, shadow1, shadow2);
        rotation.setDuration(duration);
        rotation.setFillAfter(false);
        rotation.setInterpolator(new LinearInterpolator());
        return rotation;
    }

}
