package com.goal98.flipdroid.anim;

import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;

public class AnimationFactory {

    public Animation buildFlipAnimation(boolean forward, long duration, float centerX, float centerY){

        final float fromDegrees = forward ? 0 : -90;
        final float toDegrees = forward ? -90 : 0;
        float depthZ = 0.0f;
        boolean reverse = true;

        Rotate3DAnimation rotation = new Rotate3DAnimation(fromDegrees, toDegrees, centerX, centerY, depthZ, reverse);
        rotation.setDuration(duration);
        rotation.setFillAfter(false);
        rotation.setInterpolator(new DecelerateInterpolator());

        return rotation;

    }

}
