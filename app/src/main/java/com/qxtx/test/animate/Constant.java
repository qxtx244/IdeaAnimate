package com.qxtx.test.animate;

import android.animation.ValueAnimator;
import android.support.annotation.IntDef;
import android.support.annotation.StringDef;
import android.widget.LinearLayout;

/**
 * @CreateDate 2019/01/16 14:04.
 * @Author QXTX-GOSPELL
 */
public class Constant {
    @IntDef({HORIZONTAL, VERTICAL})
    @interface Orientation {
    }

    public static final int HORIZONTAL = LinearLayout.HORIZONTAL;
    public static final int VERTICAL = LinearLayout.VERTICAL;

    @IntDef({MODE_RESTART, MODE_REVERSE, INFINITE})
    @interface RepeatMode {
    }

    public static final int MODE_RESTART = ValueAnimator.RESTART;
    public static final int MODE_REVERSE = ValueAnimator.REVERSE;
    public static final int INFINITE = ValueAnimator.INFINITE;

    @StringDef({TYPE_TRANSLATE, TYPE_ROTATION, TYPE_ALPHA, TYPE_SCALE})
    @interface AnimtionType {
    }
    public static final String TYPE_TRANSLATE = "Translate";
    public static final String TYPE_ROTATION = "Rotation";
    public static final String TYPE_ALPHA = "Alpha";
    public static final String TYPE_SCALE = "Scale";
    public static final String CUSTOM_3D_ROTATE = "3dRotate";
    public static final String CUSTOM_CIRCUAL_REVERAL = "CircularReveral";
}
