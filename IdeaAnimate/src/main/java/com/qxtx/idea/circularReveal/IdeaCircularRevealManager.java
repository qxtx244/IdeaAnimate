package com.qxtx.idea.circularReveal;

import android.support.annotation.NonNull;
import android.view.View;

/**
 * CreatedDate   2019/02/14 10:28.
 * Author  QXTX-GOSPELL
 *
 * A class for take circularReveal animation easily. Only Work on Android 5.0 or higher.
 *
 * see {@link IdeaCircularReveal}
 */

public final class IdeaCircularRevealManager {
    public static final String TAG = "IdeaCircularRevealManager";
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
        new IdeaCircularReveal(v)
                .duration(DEFAULT_DURATION)
                .center(centerX, centerY)
                .radius(startRadius, endRadius)
                .start();
    }
}
