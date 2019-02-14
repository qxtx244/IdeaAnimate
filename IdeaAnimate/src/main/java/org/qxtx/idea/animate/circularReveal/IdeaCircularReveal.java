package org.qxtx.idea.animate.circularReveal;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * @CreateDate 2019/02/14 10:28.
 * @Author QXTX-GOSPELL
 *
 * Only Work on Android 5.0 or higher
 */

public final class IdeaCircularReveal {
    public static final String TAG = "IdeaCircularReveal";
    public static final int DEFAULT_DURATION = 800;

    public static void start(@NonNull View target) {
        int width = target.getWidth();
        int height = target.getHeight();
        base(target, width / 2, height / 2, 0f, (float) Math.hypot(width, height));
    }

    public static void start(@NonNull View target, float startRadius, float endRadius) {
        base(target, target.getWidth() / 2, target.getHeight() / 2, startRadius, endRadius);
    }

    public static void start(@NonNull View target, int centerX,  int centerY, float startRadius, float endRadius) {
        base(target, centerX, centerY, startRadius, endRadius);
    }

    private static void base(@NonNull View v, int centerX,  int centerY, float startRadius, float endRadius) {
        v.post(() -> {
            ViewAnimationUtils
                    .createCircularReveal(v, centerX, centerY, startRadius, endRadius)
                    .setDuration(IdeaCircularReveal.DEFAULT_DURATION)
                    .start();
        });
    }

    public static final class Builder {
        private final View target;
        private int centerX, centerY;
        private float startRadius, endRadius;
        private long delay;
        private long duration;
        private TimeInterpolator interpolator;
        private Animator animator;
        private boolean isReady;

        public Builder(@NonNull View target) {
            this.target = target;
            centerX = 0;
            centerY = 0;
            startRadius = 0f;
            duration = DEFAULT_DURATION;
            interpolator = new AccelerateDecelerateInterpolator();
            isReady = false;
        }

        public Builder center(int centerX, int centerY) {
            this.centerX = centerX;
            this.centerY = centerY;
            return this;
        }

        public Builder delay(long delay) {
            this.delay = delay;
            return this;
        }

        public Builder duration(long duration) {
            this.duration = duration;
            return this;
        }

        public Builder interpolator(TimeInterpolator interpolator) {
            this.interpolator = interpolator;
            return this;
        }

        public Builder radius(float startRadius, float endRadius) {
            this.startRadius = startRadius;
            this.endRadius = endRadius;
            return this;
        }

        public Animator build() {
            animator = ViewAnimationUtils
                    .createCircularReveal(target, centerX, centerY, startRadius, endRadius)
                    .setDuration(duration);
            animator.setInterpolator(interpolator);
            animator.setStartDelay(delay);
            isReady = true;
            return animator;
        }

        public void start() {
            if (isReady) {
                target.post(() -> {
                    animator.start();
                });
            } else {
                Log.e(TAG, "Failed to start circularReveal because not to build.");
            }
        }
    }
}
