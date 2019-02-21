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

    public static final String SVG_SKULL = "M501.799,865.691 " +
            "c-38.7774,-23.4787,-30.5121,-64.4166,-55.4953,-90.1836 " +
            "c-45.9556,-47.3885,-126.496,-49.8182,-173.427,-104.054 " +
            "c-157.357,-181.844,-6.25986,-521.809,291.356,-471.722 " +
            "c173.443,29.1883,305.578,272.185,187.3,443.973 " +
            "c-50.8729,73.8967,-148.708,79.7476,-208.112,159.553 " +
            "c-17.4063,17.2734,-8.5924,60.7806,-41.6211,62.4333Z " +

            "M252.064,470.277 c-6.98228,131.96,180.364,125.277,180.364,6.93798 " +
            "c0,-60.2456,-54.1016,-108.604,-124.867,-83.2456 c-49.779,17.8357,-53.4371,37.3888,-55.4971,76.3076Z " +

            "m381.538,97.1198 c158.95,44.5738,162.964,-230.536,0,-173.427 " +
            "c-77.0965,27.0176,-68.1105,154.329,0,173.427Z " +

            "m-159.551,83.2473 c25.4075,5.6976,32.9963,-68.5024,6.93628,-69.3747 " +
            "c-14.4706,14.3275,-48.6051,59.225,-6.93628,69.3747Z " +

            "m62.4299,0 h13.8794 c17.914,-28.413,-6.00429,-65.4201,-34.6865,-69.3747 " +
            "c2.32061,27.74,-8.91783,69.0374,20.8071,69.3747Z";

    public static final String SVG_STAR = "M48.0,54.0 L31.0,42.0 L15.0,54.0 L21.0,35.0 L6.0,23.0 " +
            "L25.0,23.0 L25.0,23.0 L25.0,23.0 L25.0,23.0 L32.0,4.0 L40.0,23.0 L58.0,23.0 L42.0,35.0 L48.0,54.0Z";

    public static final int DEFAULT_DURATION = 500;
}
