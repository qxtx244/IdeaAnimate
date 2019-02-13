package qxtx.idea.animate;

import android.animation.ValueAnimator;
import android.support.annotation.IntDef;
import android.support.annotation.StringDef;
import android.view.Gravity;
import android.view.animation.Animation;
import android.widget.LinearLayout;

/**
 * @CreateDate 2019/01/16 14:04.
 * @Author QXTX-GOSPELL
 */
public class IdeaUtil {
    @IntDef({LEFT, TOP, RIGHT, BOTTOM})
    @interface Direction {}
    public static final int LEFT = Gravity.LEFT;
    public static final int TOP = Gravity.TOP;
    public static final int RIGHT = Gravity.RIGHT;
    public static final int BOTTOM = Gravity.BOTTOM;

    @IntDef({HORIZONTAL, VERTICAL})
    @interface Orientation {}
    public static final int HORIZONTAL = LinearLayout.HORIZONTAL;
    public static final int VERTICAL = LinearLayout.VERTICAL;

    @IntDef({MODE_RESTART, MODE_REVERSE, INFINITE})
    @interface RepeatMode {}
    public static final int MODE_RESTART = ValueAnimator.RESTART;
    public static final int MODE_REVERSE = ValueAnimator.REVERSE;
    public static final int INFINITE = ValueAnimator.INFINITE;

    @StringDef({TYPE_TRANSLATE, TYPE_ROTATION, TYPE_ALPHA, TYPE_SCALE})
    @interface AnimtionType {}
    public static final String TYPE_TRANSLATE = "Translate";
    public static final String TYPE_ROTATION = "Rotation";
    public static final String TYPE_ALPHA = "Alpha";
    public static final String TYPE_SCALE = "Scale";
    public static final String CUSTOM_3D_ROTATE = "3dRotate";
    public static final String CUSTOM_CIRCUAL_REVERAL = "CircularReveral";

    public static final int LEVEL_MIN = 1;
    public static final int LEVEL_MAX = 10;

    public static final int ABSOLUTE = Animation.ABSOLUTE;
    public static final int RELATIVE_TO_SELF = Animation.RELATIVE_TO_SELF;
    public static final int RELATIVE_TO_PARENT = Animation.RELATIVE_TO_PARENT;
}
