package com.qxtx.idea.circularReveal;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * CreatedDate   2019/02/21 9:11.
 * Author  QXTX-GOSPELL
 *
 * A class for take circularReveal animation.
 *  You can use {@link IdeaCircularRevealManager} to take circularReveal animation easily.
 *
 * @see IdeaCircularRevealManager
 */

public class IdeaCircularReveal {
    public static final String TAG = "IdeaCircularReveal";
    public static final int DEFAULT_DURATION = 800;
    private final View target;
    private int centerX, centerY;
    private float startRadius, endRadius;
    private long delay;
    private long duration;
    private TimeInterpolator interpolator;

    public IdeaCircularReveal(@NonNull View target) {
        this.target = target;
        centerX = 0;
        centerY = 0;
        startRadius = 0f;
        duration = DEFAULT_DURATION;
        interpolator = new AccelerateDecelerateInterpolator();
    }

    public IdeaCircularReveal center(int centerX, int centerY) {
        this.centerX = centerX;
        this.centerY = centerY;
        return this;
    }

    public IdeaCircularReveal delay(long delay) {
        this.delay = delay;
        return this;
    }

    public IdeaCircularReveal duration(long duration) {
        this.duration = duration;
        return this;
    }

    public IdeaCircularReveal interpolator(TimeInterpolator interpolator) {
        this.interpolator = interpolator;
        return this;
    }

    public IdeaCircularReveal radius(float startRadius, float endRadius) {
        this.startRadius = startRadius;
        this.endRadius = endRadius;
        return this;
    }

    public void start() {
        Animator animator = ViewAnimationUtils
                .createCircularReveal(target, centerX, centerY, startRadius, endRadius)
                .setDuration(duration);
        animator.setInterpolator(interpolator);
        animator.setStartDelay(delay);

        target.post(animator::start);
    }
}
