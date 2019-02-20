package org.qxtx.idea.animate;

import android.animation.ValueAnimator;
import android.support.annotation.IntDef;
import android.support.annotation.StringDef;
import android.view.Gravity;
import android.view.animation.Animation;
import android.widget.LinearLayout;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @CreateDate 2019/01/16 14:04.
 * @Author QXTX-GOSPELL
 */
public class IdeaUtil {
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({LEFT, TOP, RIGHT, BOTTOM})
    public @interface Direction {}
    public static final int LEFT = Gravity.LEFT;
    public static final int TOP = Gravity.TOP;
    public static final int RIGHT = Gravity.RIGHT;
    public static final int BOTTOM = Gravity.BOTTOM;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({HORIZONTAL, VERTICAL})
    public @interface Orientation {}
    public static final int HORIZONTAL = LinearLayout.HORIZONTAL;
    public static final int VERTICAL = LinearLayout.VERTICAL;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({MODE_RESTART, MODE_REVERSE, INFINITE})
    public @interface RepeatMode {}
    public static final int MODE_RESTART = ValueAnimator.RESTART;
    public static final int MODE_REVERSE = ValueAnimator.REVERSE;
    public static final int INFINITE = ValueAnimator.INFINITE;

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({TYPE_TRANSLATE, TYPE_ROTATION, TYPE_ALPHA, TYPE_SCALE})
    public @interface AnimationType {}
    public static final String TYPE_TRANSLATE = "Translate";
    public static final String TYPE_ROTATION = "Rotation";
    public static final String TYPE_ALPHA = "Alpha";
    public static final String TYPE_SCALE = "Scale";
    public static final String TYPE_CIRCUAL_REVERAL = "CircularReveral";

    public static final int LEVEL_MIN = 1;
    public static final int LEVEL_MAX = 10;

    public static final int ABSOLUTE = Animation.ABSOLUTE;
    public static final int RELATIVE_TO_SELF = Animation.RELATIVE_TO_SELF;
    public static final int RELATIVE_TO_PARENT = Animation.RELATIVE_TO_PARENT;

    public static final String SVG_HEART = "M0,0 c-1.955,0,-3.83,1.268,-4.5,3 c-0.67,-1.732,-2.547,-3,-4.5,-3 " +
            "C-11.543,0,-13.5,1.932,-13.5,4.5 c0,3.53,3.793,6.257,9,11.5 c5.207,-5.242,9,-7.97,9,-11.5 " +
            "C4.5,1.932,2.543,0,0,0z";
    public static final String SVG_PAR = "M0,0 L50,0 L50,10 L0,10 Z " +
            "M0,20 L50,20 L50,30 L0,30 Z " +
            "M0,40 L50,40 L50,50 L0,50 Z";
    public static final String SVG_ARROWS = "M5,35 L40,0 L47.072,7.072 L12.072,42.072 Z " +
            "M10,30 L60,30 L60,40 L10,40 Z " +
            "M12.072,27.928 L47.072,62.928 L40,70 L5,35 Z";

    public static final int DEFAULT_DURATION = 500;
}
