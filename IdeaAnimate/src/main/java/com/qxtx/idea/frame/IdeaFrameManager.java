package com.qxtx.idea.frame;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.widget.ImageView;

/**
 * CreatedDate   2019/02/14 9:45.
 * Author  QXTX-GOSPELL
 *
 * A class for take frame animate easily.
 *
 * see {@link IdeaFrame}
 */

public class IdeaFrameManager {
    public static final String TAG = "IdeaAnimationManager";

    private static IdeaFrame baseFrame(@NonNull ImageView v) {
        return new IdeaFrame(v).setDuration(IdeaFrame.DEFAULT_DURATION);
    }

    public static IdeaFrame frame(@NonNull ImageView target, @NonNull Drawable... drawables) {
        return baseFrame(target).addFrame(drawables);
    }

    public static IdeaFrame frame(@NonNull ImageView target, @NonNull int... resIds) {
        return baseFrame(target).addFrame(target.getContext(), resIds);
    }
}
