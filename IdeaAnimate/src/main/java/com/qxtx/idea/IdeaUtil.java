package com.qxtx.idea;

import android.animation.ValueAnimator;
import android.graphics.Paint;
import android.support.annotation.IntDef;
import android.support.annotation.StringDef;
import android.view.Gravity;
import android.view.animation.Animation;
import android.widget.LinearLayout;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * CreatedDate   2019/01/16 14:04.
 * Author  QXTX-GOSPELL
 *
 * A util class include only the constants.
 *  You can find any constant what is used by other class at this package in this.
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

    public static final Paint.Style PAINT_LINE = Paint.Style.STROKE;
    public static final Paint.Style PAINT_FILL = Paint.Style.FILL;
    public static final Paint.Style PAINT_FILL_AND_LINE = Paint.Style.FILL_AND_STROKE;

    public static final int ABSOLUTE = Animation.ABSOLUTE;
    public static final int RELATIVE_TO_SELF = Animation.RELATIVE_TO_SELF;
    public static final int RELATIVE_TO_PARENT = Animation.RELATIVE_TO_PARENT;

    public static final int DEFAULT_DURATION = 500;

    public static final String SVG_TEST = "M 48.0, 54.0, L31.0,42.0 15.0,54.0   21.0,35.0 6.0,23.0 " +
            "L25.0,23.0 L25.0,23.0,L25.0,23.0 L25.0,23.0 L32.0,4.0 L40.0,23.0 L58.0,23.0 L42.0,35.0 L48.0,54.0Z ";

    public static final String SVG_HEART = "M0,0 c-1.955,0,-3.83,1.268,-4.5,3 c-0.67,-1.732,-2.547,-3,-4.5,-3 C-11.543,0,-13.5,1.932,-13.5,4.5 " +
            "c0,3.53,3.793,6.257,9,11.5 c5.207,-5.242,9,-7.97,9,-11.5 C4.5,1.932,2.543,0,0,0z";

    public static final String SVG_PAR = "M0,0 L50,0 L50,10 L0,10 Z M0,20 L50,20 L50,30 L0,30 Z M0,40 L50,40 L50,50 L0,50 Z";

    public static final String SVG_ARROWS = "M5,35 L40,0 L47.072,7.072 L12.072,42.072 Z " +
            "M10,30 L60,30 L60,40 L10,40 Z M12.072,27.928 L47.072,62.928 L40,70 L5,35 Z";

    public static final String SVG_SKULL = "M501.799,865.691 c-38.7774,-23.4787,-30.5121,-64.4166,-55.4953,-90.1836 " +
            "c-45.9556,-47.3885,-126.496,-49.8182,-173.427,-104.054 c-157.357,-181.844,-6.25986,-521.809,291.356,-471.722 " +
            "c173.443,29.1883,305.578,272.185,187.3,443.973 c-50.8729,73.8967,-148.708,79.7476,-208.112,159.553 " +
            "c-17.4063,17.2734,-8.5924,60.7806,-41.6211,62.4333Z " +
            "M252.064,470.277 c-6.98228,131.96,180.364,125.277,180.364,6.93798 " +
            "c0,-60.2456,-54.1016,-108.604,-124.867,-83.2456 c-49.779,17.8357,-53.4371,37.3888,-55.4971,76.3076Z " +
            "m381.538,97.1198 c158.95,44.5738,162.964,-230.536,0,-173.427 c-77.0965,27.0176,-68.1105,154.329,0,173.427Z " +
            "m-159.551,83.2473 c25.4075,5.6976,32.9963,-68.5024,6.93628,-69.3747 c-14.4706,14.3275,-48.6051,59.225,-6.93628,69.3747Z " +
            "m62.4299,0 h13.8794 c17.914,-28.413,-6.00429,-65.4201,-34.6865,-69.3747 c2.32061,27.74,-8.91783,69.0374,20.8071,69.3747Z";

    public static final String SVG_STAR = "M48.0,54.0 L31.0,42.0 L15.0,54.0 L21.0,35.0 L6.0,23.0 " +
            "L25.0,23.0 L25.0,23.0 L25.0,23.0 L25.0,23.0 L32.0,4.0 L40.0,23.0 L58.0,23.0 L42.0,35.0 L48.0,54.0Z";

    public static final String SVG_BRIDE_2_HEART = "M22.46,6.0 l0.0,0.0 " +
            "C21.69,6.35,20.86,6.58,20.0,6.69 C20.88,6.16,21.56,5.32,21.88,4.31 c0.0,0.0,0.0,0.0,0.0,0.0 " +
            "C21.05,4.81,20.13,5.16,19.16,5.36 C18.37,4.5,17.26,4.0,16.0,4.0 c0.0,0.0,0.0,0.0,0.0,0.0 " +
            "L16.0,4.0 C13.65,4.0,11.73,5.92,11.73,8.29 C11.73,8.63,11.77,8.96,11.84,9.27 " +
            "C8.28,9.09,5.11,7.38,3.0,4.79 C2.63,5.42,2.42,6.16,2.42,6.94 C2.42,8.43,3.17,9.75,4.33,10.5 C3.62,10.5,2.96,10.3,2.38,10.0 " +
            "C2.38,10.0,2.38,10.0,2.38,10.03 C2.38,12.11,3.86,13.85,5.82,14.24 C5.46,14.34,5.08,14.39,4.69,14.39 C4.42,14.39,4.15,14.36,3.89,14.31 " +
            "C4.43,16.0,6.0,17.26,7.89,17.29 C6.43,18.45,4.58,19.13,2.56,19.13 C2.22,19.13,1.88,19.11,1.54,19.07 C3.44,20.29,5.7,21.0,8.12,21.0 " +
            "C16.0,21.0,20.33,14.46,20.33,8.79 C20.33,8.6,20.33,8.42,20.32,8.23 C21.16,7.63,21.88,6.87,22.46,6.0 L22.46,6.0 Z";

    public static final String SVG_HEART_2_BRIDE = "M12.0,21.35 l-1.45,-1.32 " +
            "C5.4,15.36,2.0,12.28,2.0,8.5 C2.0,5.42,4.42,3.0,7.5,3.0 c1.74,0.0,3.41,0.81,4.5,2.09 " +
            "C13.09,3.81,14.76,3.0,16.5,3.0 C19.58,3.0,22.0,5.42,22.0,8.5 c0.0,3.78,-3.4,6.86,-8.55,11.54 " +
            "L12.0,21.35 C12.0,21.35,12.0,21.35,12.0,21.35 C12.0,21.35,12.0,21.35,12.0,21.35 C12.0,21.35,12.0,21.35,12.0,21.35 " +
            "C12.0,21.35,12.0,21.35,12.0,21.35 C12.0,21.35,12.0,21.35,12.0,21.35 C12.0,21.35,12.0,21.35,12.0,21.35 C12.0,21.35,12.0,21.35,12.0,21.35 " +
            "C12.0,21.35,12.0,21.35,12.0,21.35 C12.0,21.35,12.0,21.35,12.0,21.35 C12.0,21.35,12.0,21.35,12.0,21.35 C12.0,21.35,12.0,21.35,12.0,21.35 " +
            "C12.0,21.35,12.0,21.35,12.0,21.35 C12.0,21.35,12.0,21.35,12.0,21.35 C12.0,21.35,12.0,21.35,12.0,21.35 C12.0,21.35,12.0,21.35,12.0,21.35 " +
            "C12.0,21.35,12.0,21.35,12.0,21.35 C12.0,21.35,12.0,21.35,12.0,21.35 L12.0,21.35 Z";

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({0, 1, 2, 3, 4, 5, 6, 7, 8, 9})
    public @interface SvgNumber {}

    /** not support keyword T and S because i don't want to parse their value. */
    public static final String VALID_CHAR = "MmLlCcQqHhVvZzAa.0123456789,- ";
//    public static final String VALID_CHAR = "MmLlCcQqHhVvZzSsTtAa.0123456789,- ";

    public static final String SVG_NUMBER_8 =
            //Top line
            "M68.00,14.00 C68.00,14.00,99.00,14.00,99.00,14.00 C108.66,14.02,109.21,16.08,116.00,23.00 " +
            "C117.63,24.67,120.80,27.51,120.80,30.00 C120.80,32.69,116.80,36.18,115.00,38.00 " +
            "C107.67,45.39,106.60,48.98,96.00,49.00 C96.00,49.00,63.00,49.00,63.00,49.00 " +
            "C52.18,48.98,50.42,44.56,43.00,37.00 C40.77,34.73,36.93,31.59,38.17,28.00 " +
            "C39.34,24.60,47.86,17.11,51.00,15.02 C56.32,13.24,62.43,14.00,68.00,14.00 Z " +
            //Left-top line
            "M18.00,55.00 C18.02,45.90,19.40,45.48,26.00,39.00 C27.67,37.37,30.51,34.20,33.00,34.20 " +
            "C35.96,34.20,40.91,39.91,43.00,42.00 C45.72,44.72,51.13,49.54,52.40,53.00 " +
            "C53.28,55.38,53.00,62.15,53.00,65.00 C53.00,65.00,53.00,89.00,53.00,89.00 " +
            "C53.00,91.43,53.20,94.67,51.98,96.82 C50.44,99.50,45.67,102.00,43.00,103.68 " +
            "C36.57,107.73,24.71,116.04,18.00,118.00 C18.00,118.00,18.00,55.00,18.00,55.00 Z " +
            //Right-top line
            "M140.40,49.00 C141.12,50.94,141.00,53.91,141.00,56.00 C141.00,56.00,141.00,118.00,141.00,118.00 " +
            "C141.00,118.00,116.00,103.95,116.00,103.95 C116.00,103.95,106.99,97.47,106.99,97.47 " +
            "C106.99,97.47,106.00,91.00,106.00,91.00 C106.00,91.00,106.00,60.00,106.00,60.00 " +
            "C106.00,60.00,106.60,53.00,106.60,53.00 C106.60,53.00,125.00,33.00,125.00,33.00 " +
            "C128.93,35.81,138.81,44.68,140.40,49.00 Z " +
            //Center line
            "M55.00,101.51 C55.00,101.51,62.00,101.00,62.00,101.00 C62.00,101.00,97.00,101.00,97.00,101.00 " +
            "C97.00,101.00,104.00,101.70,104.00,101.70 C104.00,101.70,132.00,119.00,132.00,119.00 " +
            "C132.00,119.00,132.00,121.00,132.00,121.00 C121.16,125.67,108.77,134.94,97.00,134.87 " +
            "C97.00,134.87,62.00,134.87,62.00,134.87 C59.81,135.00,57.12,135.07,55.00,134.87 " +
            "C55.00,134.87,26.00,121.00,26.00,121.00 C26.00,121.00,26.00,119.00,26.00,119.00 " +
            "C26.00,119.00,55.00,101.51,55.00,101.51 Z " +
            //Left-bottom line
            "M43.00,133.25 C45.22,134.36,50.69,136.75,52.01,138.63 C53.25,140.42,53.00,143.87,53.00,146.00 " +
            "C53.00,146.00,53.00,181.00,53.00,181.00 C52.98,183.43,53.10,185.76,51.98,188.00 " +
            "C49.96,192.04,37.04,204.11,33.00,207.00 C25.60,200.20,18.07,196.81,18.00,186.00 " +
            "C18.00,186.00,18.00,122.00,18.00,122.00 C24.76,123.27,36.37,129.93,43.00,133.25 Z " +
            //Right-bottom line
            "M107.02,188.00 C107.02,188.00,106.00,175.00,106.00,175.00 C106.00,175.00,106.00,146.00,106.00,146.00 " +
            "C106.00,146.00,106.99,138.63,106.99,138.63 C106.99,138.63,116.00,133.25,116.00,133.25 " +
            "C116.00,133.25,141.00,122.00,141.00,122.00 C141.00,122.00,141.00,184.00,141.00,184.00 " +
            "C141.00,186.09,141.12,189.06,140.40,191.00 C138.81,195.32,128.93,204.19,125.00,207.00 " +
            "C125.00,207.00,107.02,188.00,107.02,188.00 Z " +
            //Bottom line
            "M102.00,225.00 C102.00,225.00,57.00,225.00,57.00,225.00 C49.89,224.87,45.77,221.06,41.06,215.99 " +
            "C39.23,214.02,37.17,211.90,38.17,209.00 C38.94,206.76,43.22,202.78,45.00,201.00 " +
            "C47.72,198.28,52.55,192.87,56.00,191.60 C58.43,190.70,66.02,191.00,69.00,191.00 " +
            "C69.00,191.00,95.00,191.00,95.00,191.00 C97.09,191.00,100.06,190.88,102.00,191.60 " +
            "C106.39,193.21,118.99,206.80,122.00,211.00 C116.09,216.91,110.88,224.83,102.00,225.00 Z";

    public static final int[][] NUMBER_CHOOSE = new int[][]{
            {3},
            {0, 1, 3, 4, 6}, {1, 5}, {1, 4},
            {0, 4, 6},       {2, 4}, {2},
            {1, 3, 4, 6},    {},     {4}
    };
}
