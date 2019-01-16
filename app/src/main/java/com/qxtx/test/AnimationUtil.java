package com.qxtx.test;

import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TimeInterpolator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Camera;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import junit.framework.Assert;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * It must be run in MainThread.
 * You should to call {@link AnimationUtil#release()} to release all animation.<br/>
 *  Example1: Use newValueAnimator() to take a ValueAnimator.<br/>
 *  Example2: Call {@link #newObjectAnimator(Object, String, Number...)} to take a ObjectAnimator with Number values.<br/>
 *  Example3: Call {@link #newObjectAnimator(Object, String, TypeEvaluator, Object...)} to take a ObjectAnimator with Object values.<br/>
 **/
public class AnimationUtil {
    public static final String TAG = "AnimationUtil";

    public static final String PROPERTY_CUSTOM = "custom";

    public static final int COUNT_INFINITE = Animation.INFINITE;
    public static final int MODE_RESTART = Animation.RESTART;
    public static final int MODE_REVERSE = Animation.REVERSE;

    public static final String COLORFUL_TEXT = "textColor";
    public static final String COLORFUL_BACKGROUND = "backgroundColor";

    public static final int TYPE_3D_ROTATE = 0;
    public static final int TYPE_TRANSLATE = 1;
    public static final int TYPE_ROTATION = 2;
    public static final int TYPE_ALPHA = 3;
    public static final int TYPE_SCALE = 4;

    public static final int VERTICAL = LinearLayout.VERTICAL;
    public static final int HORIZONTAL= LinearLayout.HORIZONTAL;

    public static final float POINT_CENTER = 0.5f;
    public static final float POINT_LEFT_TOP = 0f;
    public static final float POINT_RIGHT_BOTTOM = 1f;

    private static AnimationUtil animationUtil;
    private List<IdeaAnimator> animatorList;
    private List<IdeaAnimation> animationList;

    /**
     * Construction method.
     *  It will be create a list of type List<IdeaAnimator> for collect all {@link IdeaAnimator} the object that be created.
     */
    private AnimationUtil() {
        if (animatorList == null) {
            animatorList = new ArrayList<>();
        }
        if (animationList == null) {
            animationList = new ArrayList<>();
        }
    }

    /**
     * Get the single instance of {@link AnimationUtil}. A single instance will be created for {@link AnimationUtil} only the first call.
     * @return The single instance of {@link AnimationUtil}
     **/
    public static AnimationUtil getInstance() {
        if (animationUtil == null) {
            synchronized (AnimationUtil.class) {
                if (animationUtil == null) {
                    animationUtil = new AnimationUtil();
                }
            }
        }
        return animationUtil;
    }

    /**
     * Create an {@link IdeaAnimator} to execute valueAnimator.
     *  It will be add into a list of type List<IdeaAnimator> when the IdeaAnimator be created in order to release in anytime.
     * @return {@link IdeaAnimator} The object called with this.
     */
    public IdeaAnimator newValueAnimator() {
        IdeaAnimator IdeaAnimator = new IdeaAnimator();
        animatorList.add(IdeaAnimator);

        return IdeaAnimator;
    }

    /**
     * Create an {@link IdeaAnimator} to execute the objectAnimator which be changed by a number value array.
     *  You can take obj with a {@link PropertyFactory} to customize your objectAnimator to create more possible. If you need to set a method
     *  name of "custom", you can take property to null.
     * @param obj The object which execute objectAnimator
     * @param property Property name. It can be null when obj instance of {@link PropertyFactory}
     *                     because it always be convert to {@link #PROPERTY_CUSTOM}
     * @param values some values of the objectAnimator changes
     * @return {@link IdeaAnimator} The object called with newObjectAnimator()
     */
    public IdeaAnimator newObjectAnimator(@NonNull Object obj, String property, @NonNull Number... values) {
        Object object = shouldBeUsePropertyFactory(obj, property);
        String instanceProperty = (object instanceof PropertyFactory && property == null) ? PROPERTY_CUSTOM : property;

        IdeaAnimator IdeaAnimator = new IdeaAnimator(obj, instanceProperty, values);
        animatorList.add(IdeaAnimator);

        return IdeaAnimator;
    }

    /**
     * Create an {@link IdeaAnimator} to execute the objectAnimator to take object change by a object value array.
     *  You can take obj with a {@link PropertyFactory} to customize your objectAnimator to create more possible. If you need to set a method
     *  name of "custom", you can take property to null.
     * @param obj  The object which execute objectAnimator
     * @param property  Property name. It can be null when obj instance of {@link PropertyFactory}
     *                     because it always be convert to {@link #PROPERTY_CUSTOM}
     * @param evaluator  A TypeEvaluator object is take a scheme for the change of objectAnimator. It is necessary to take the objectAnimator
     *                       that change by a value array of Object type
     * @param values  A value array to change objectAnimator
     * @return  {@link IdeaAnimator} The object called with this
     */
    public IdeaAnimator newObjectAnimator(@NonNull Object obj, String property, @NonNull TypeEvaluator evaluator, @NonNull Object... values) {
        Object object = shouldBeUsePropertyFactory(obj, property);
        String instanceProperty = (object instanceof PropertyFactory && property == null) ? PROPERTY_CUSTOM : property;

        IdeaAnimator idea = new IdeaAnimator(obj, instanceProperty, evaluator, values);
        animatorList.add(idea);

        return idea;
    }


    /**
     * Create an {@link IdeaAnimation} to execute a animation to take view center rotate with 3D.
     * @param durationMs  Duration the animation, in milliSecond
     * @param startAngle  Start value of angle, with float
     * @param endAngle  End value of angle, with float
     * @param zoomZ  Value of translate for z
     * @param centerX  Value of rotate center X
     * @param centerY  Value of rotate center Y
     * @return  {@link IdeaAnimation} the object call with this
     */
    public IdeaAnimation new3dRotateAnimation(long durationMs, float startAngle, float endAngle, float zoomZ, float centerX, float centerY) {
        IdeaAnimation idea = new IdeaAnimation.Builder(TYPE_3D_ROTATE)
                .setCenter(centerX, centerY)
                .setAngle(startAngle, endAngle)
                .setZoomZ(zoomZ)
                .setReverse(true)
                .build()
                .setDuration(durationMs);
        animationList.add(idea);

        return idea;
    }

    /**
     * Create an {@link IdeaAnimation} to execute a animation to take view translate.
     * @param durationMs  Duration the animation, in milliSecond
     * @param startX  Start value of X scale
     * @param endX  End value of scale
     * @param startY  Start value of Y scale
     * @param endY  End value of Y scale
     * @return  {@link IdeaAnimation} the object call with this
     */
    public IdeaAnimation newTranslateAnimation(long durationMs, float startX, float endX, float startY, float endY) {
        IdeaAnimation idea = new IdeaAnimation.Builder(TYPE_TRANSLATE)
                .setX(startX, endX)
                .setY(startY, endY)
                .build()
                .setDuration(durationMs);
        animationList.add(idea);
        return idea;
    }

    /**
     * Create an {@link IdeaAnimation} to execute a animation to take view visibility.
     * @param durationMs  Duration the animation, in milliSecond
     * @param startAlpha  Start value of alpha
     * @param endAlpha  End value of alpha
     * @return  {@link IdeaAnimation} the object call with this
     */
    public IdeaAnimation newAlphaAnimation(long durationMs, float startAlpha, float endAlpha) {
        startAlpha = startAlpha < 0f ? 0f : startAlpha;
        startAlpha = startAlpha > 1f ? 1f : startAlpha;
        endAlpha = endAlpha < 0f ? 0f : endAlpha;
        endAlpha = endAlpha > 1f ? 1f : endAlpha;

        IdeaAnimation idea = new IdeaAnimation.Builder(TYPE_ALPHA)
                .setAlpha(startAlpha, endAlpha)
                .build()
                .setDuration(durationMs);
        animationList.add(idea);
        return idea;
    }

    /**
     * Create an {@link IdeaAnimation} to execute a animation to take view rotate.
     * @param durationMs  Duration the animation, in milliSecond
     * @param startAngle  Start value of angle, with float
     * @param endAngle  End value of angle, with float
     * @return  {@link IdeaAnimation} the object call with this
     */
    public IdeaAnimation newRotateAnimation(long durationMs, float startAngle, float endAngle, float centerX, float centerY) {
        IdeaAnimation idea = new IdeaAnimation.Builder(TYPE_ROTATION)
                .setAngle(startAngle, endAngle)
                .setCenter(centerX, centerY)
                .build()
                .setDuration(durationMs);
        animationList.add(idea);

        return idea;
    }

    /**
     * Create an {@link IdeaAnimation} to execute a animation to take view scale.
     * @param durationMs  Duration the animation, in milliSecond
     * @param startX  Start value of X scale
     * @param endX  End value of scale
     * @param startY  Start value of Y scale
     * @param endY End value of Y scale
     * @return {@link IdeaAnimation} the object call with this
     */
    public IdeaAnimation newScaleAnimation(long durationMs, float startX, float endX, float startY, float endY, float centerX, float centerY) {
        IdeaAnimation idea = new IdeaAnimation.Builder(TYPE_SCALE)
                .setX(startX, endX)
                .setY(startY, endY)
                .setCenter(centerX, centerY)
                .build()
                .setDuration(durationMs);
        animationList.add(idea);

        return idea;
    }


    /**
     * @return Number of animate
     **/
    public int getAnimateCount() {
        int counter = 0;

        if (animatorList != null) {
            counter += animatorList.size();
        }

        if (animationList != null) {
            counter += animationList.size();
        }

        return counter;
    }

    /** release animationUtil **/
    public void release() {
        if (animationUtil != null) {
            animationUtil.release();
        }

        if (animatorList != null) {
            for (int i = 0; i < animatorList.size(); i++) {
                animatorList.get(i).release();
            }
            animatorList.clear();
            animatorList = null;
        }

        if (animationList != null) {
            for (int i = 0; i < animationList.size(); i++) {
                animationList.get(i).release();
            }
            animationList.clear();
            animationList = null;
        }

        animationUtil = null;
    }

    /**
     * A static method to check whether all of the method of getter and setter do exist.
     * @param obj  A object
     * @param property  Property name
     * @return true:One or all of the method of setter and getter do exist.  false:One or all of the method of getter and setter do not exist
     **/
    public static boolean canSetProperty(Object obj, String property) {
        boolean existMethod;

        Class c = obj.getClass();
        Method[] methods = c.getMethods();
        String firstStr = property.substring(0, 1).toUpperCase();
        String name = firstStr + property.substring(1);
        existMethod = false;
        for (Method method1 : methods) {
            if (method1.getName().equals("get" + name)) {
                existMethod = true;
            }
        }

        for (Method method1 : methods) {
            if (!existMethod) {
                break;
            }

            if (method1.getName().equals("set" + name)) {
                return true;
            }
        }

        return false;
    }

    private Object shouldBeUsePropertyFactory(Object obj, String property) {
        if (!(obj instanceof PropertyFactory) && !canSetProperty(obj, property)) {
            return new DefaultPropertyFactory(obj);
        } else {
            return obj;
        }
    }


    /*/********************** Some built-in Animator method **********************/

    /**
     * Start animator with view breathe.
     * @param v  The view execute the animator
     * @param repeat  Times of breathe
     * @param speed  Speed of breathe, in millisecond
     * @param delay  The value of set Animator delay
     * @return The Object of IdeaAnimator
     */
    public IdeaAnimator startBreathe(View v, int repeat, int speed, int delay) {
        float alpha = v.getAlpha();
        IdeaAnimator idea = newObjectAnimator(v, "alpha", alpha, alpha % 1f);
        animatorList.add(idea);
        idea.setDuration(speed).setRepeat(repeat, MODE_REVERSE).setStartDelay(delay).start();

        return idea;
    }

    /** See {@link #startBreathe(View, int, int, int)}. **/
    public IdeaAnimator startBreathe(View v) {
        return startBreathe(v, 1, 1000, 0);
    }

    /**
     * Start animator to view change with colorful.
     * You must be take more than one value of color that type of HexString.
     *  Also you can make a simple use by {@link #startColorfully(View)} to get a auto animator.
     *  As a View, it only have two setter method about color is setTextView() or setBackgroundColor(), you can use
     *   {@link #PROPERTY_CUSTOM}, {@link #COLORFUL_TEXT} or {@link #COLORFUL_BACKGROUND} to set property.
     *   If property be null, take a auto {@link #PROPERTY_CUSTOM} value for it.
     *
     * @param v  A view that execute animator for color
     * @param property  Name of property
     * @param speed  Speed of animator play
     * @param delay  Delay after call {@link IdeaAnimator#start()} for animator
     * @param color  The value array of animator changed
     * @return  {@link IdeaAnimator} the object called with this
     */
    public IdeaAnimator startColorfully(View v, String property, int speed, int delay, @NonNull Object... color) {
        Assert.assertTrue("Value of color is less than two!", color.length > 1);

        Object o = new DefaultPropertyFactory(v);
        property = property == null ? PROPERTY_CUSTOM : property;

        IdeaAnimator idea = newObjectAnimator(o, property, new ColorTypeEvaluator(), color);
        animatorList.add(idea);
        idea.setDuration(speed).setStartDelay(delay).start();

        return idea;
    }

    /** See {@link #startColorfully(View, String, int, int, Object...)}. **/
    public IdeaAnimator startColorfully(View v) {
        String startColors;
        String type = COLORFUL_BACKGROUND;
        String tempColor = Integer.toHexString(Color.parseColor("#ffffff"));

        if (v instanceof TextView) {
            TextView view = (TextView)v;
            type = COLORFUL_TEXT;
            startColors = Integer.toHexString(view.getCurrentTextColor());
        } else {
            startColors = Integer.toHexString(Color.parseColor("#0000ff"));
        }

        return startColorfully(v, type, 10000, 0, startColors, tempColor, startColors);
    }

    /**
     * Start Animator to view show.
     * @param v  A view that execute animator for show
     * @param speed  Speed of animator play
     * @param delay  Delay after call {@link IdeaAnimator#start()} for animator
     * @return  {@link IdeaAnimator} the object called with this
     */
    public IdeaAnimator startShow(View v, int speed, int delay) {
        IdeaAnimator idea = newObjectAnimator(v, "alpha", 0f, 1f);
        animatorList.add(idea);
        idea.setDuration(speed).setStartDelay(delay).start();

        return idea;
    }

    /** See {@link #startShow(View, int, int)}. **/
    public IdeaAnimator startShow(View v) {
        return startShow(v, 700, 0);
    }

    /**
     * Start Animator to view hide.
     * @param v  A view that execute animator for hide
     * @param speed  Speed of animator play
     * @param delay  Delay after call {@link IdeaAnimator#start()} for animator
     * @return  {@link IdeaAnimator} the object called with this
     */
    public IdeaAnimator startHide(View v, int speed, int delay) {
        IdeaAnimator idea = newObjectAnimator(v, "alpha", 1f, 0f);
        animatorList.add(idea);
        idea.setDuration(speed).setStartDelay(delay).start();

        return idea;
    }

    /** See {@link #startHide(View, int, int)} **/
    public IdeaAnimator startHide(View v) {
        return startHide(v, 700, 0);
    }

    /**
     * Start animator to view shake.
     * @param v  A view that execute animator for shake
     * @param orientation  Orientation of shake, in {@link #VERTICAL} or {@link #HORIZONTAL}
     * @param level  Level of shake, in minimum 1 and maximum 5.
     * @param delay  Delay after call {@link IdeaAnimator#start()} for animator
     * @return  {@link IdeaAnimator} the object called with this
     */
    public IdeaAnimator startShake(View v, int orientation, int level, int delay) {
        if (v.getAnimation() != null) {
            Log.e(TAG, "View is play animator now!");
            return null;
        }

        if (level <= 0) {
            level = 1;
        } else if (level > 10) {
            level = 5;
        }

        IdeaAnimator idea;
        String property = orientation == VERTICAL ? "translationY" : "translationX";
        float start = 0f;
        float size = (orientation == VERTICAL ? v.getLayoutParams().height : v.getLayoutParams().width) / 10f;

        idea = newObjectAnimator(v, property, start, start + size, start - size);
        animatorList.add(idea);
        idea.setDuration(500 / level).setStartDelay(delay).setRepeat(3, MODE_REVERSE).start();

        return idea;
    }

    /** See {@link #startShake(View, int, int, int)}. **/
    public IdeaAnimator startShake(View v, int orientation) {
        return startShake(v, orientation, 5, 0);
    }

    /**
     * Start animator to view wave.
     * @param v  A view that execute animator for wave
     * @param delay  Delay after call {@link IdeaAnimator#start()} for animator
     * @return  {@link IdeaAnimator} the object called with this
     */
    public IdeaAnimator startWaving(View v, int delay) {
        if (v.getAnimation() != null) {
            Log.e(TAG, "View is play animator now!");
            return null;
        }

        float zoom = (float)v.getLayoutParams().height / (float)v.getLayoutParams().width;
        float rotation = 15f * (zoom > 1f ? (1f / zoom) : zoom);

        IdeaAnimator idea = newObjectAnimator(v, "rotation", 0f, rotation, -rotation, 0f);
        animatorList.add(idea);
        idea.setDuration(400).setStartDelay(delay).setRepeat(1, MODE_RESTART).start();

        return idea;
    }

    /** See {@link #startWaving(View, int)} **/
    public IdeaAnimator startWaving(View v) {
        return startWaving(v, 0);
    }


    /*/************** Some built-in Animation method **************/

    /**
     * Start animation to view center rotate with 3D. See to {@link #new3dRotateAnimation(long, float, float, float, float, float)}.
     * @param delayMs  Delay for animation execute, in millisecond
     * @return  {@link IdeaAnimation} the object called with this
     */
    public IdeaAnimation start3dCenterRotateAnimation(View v, long durationMs, float zoomZ, long delayMs) {
        float centerX = v.getLayoutParams().width;
        float centerY = v.getLayoutParams().height;
        IdeaAnimation idea = new3dRotateAnimation(durationMs, 0f, 360f, zoomZ, centerX, centerY);
        postAnimation(idea, v, delayMs);
        return idea;
    }

    /**
     * Start animation to view rotate. See to {@link #newRotateAnimation(long, float, float, float, float)}.
     * @param delayMs  Delay for animation execute, in millisecond
     * @return  {@link IdeaAnimation} the object called with this
     */
    public IdeaAnimation startCenterRotateAnimation(View view, long durationMs, long delayMs, int repeatCount, int repeatMode) {
        float centerX = view.getLayoutParams().width / 2f;
        float centerY = view.getLayoutParams().height / 2f;
        IdeaAnimation idea = newRotateAnimation(durationMs, 0f, 360f, centerX, centerY)
                .setRepeat(repeatCount, repeatMode);
        postAnimation(idea, view, delayMs);
        return idea;
    }

    /** Start animation to view scale. See to {@link #newScaleAnimation(long, float, float, float, float, float, float)}.
     * @param delayMs  Delay for animation execute, in millisecond
     * @return  {@link IdeaAnimation} the object called with this
     */
    public IdeaAnimation startCenterScaleAnimation(View view, long durationMs, float startX, float endX, float startY, float endY, long delayMs) {
        float centerX = view.getLayoutParams().width / 2f;
        float centerY = view.getLayoutParams().height / 2f;
        IdeaAnimation idea = newScaleAnimation(durationMs, startX, endX, startY, endY, centerX, centerY);
        postAnimation(idea, view, delayMs);
        return idea;
    }

    /**
     * Start animation to view X scale. See to {@link #newScaleAnimation(long, float, float, float, float, float, float)}.
     * @param delayMs  Delay for animation execute, in millisecond
     * @return  {@link IdeaAnimation} the object called with this
     */
    public IdeaAnimation startCenterScaleXAnimation(View view, long durationMs, float startX, float endX, long delayMs) {
        float centerX = view.getLayoutParams().width / 2f;
        float centerY = 1f;
        IdeaAnimation idea = newScaleAnimation(durationMs, startX, endX, 1f, 1f, centerX, centerY);
        postAnimation(idea, view, delayMs);
        return idea;
    }

    /**
     * Start animation to view Y scale. See to {@link #newScaleAnimation(long, float, float, float, float, float, float)}.
     * @param delayMs  Delay for animation execute, in millisecond
     * @return  {@link IdeaAnimation} the object called with this
     */
    public IdeaAnimation startCenterScaleYAnimation(View view, long durationMs, float startY, float endY, long delayMs) {
        float centerX = 1f;
        float centerY = view.getLayoutParams().height / 2f;
        IdeaAnimation idea = newScaleAnimation(durationMs, 1f, 1f, startY, endY, centerX, centerY);
        postAnimation(idea, view, delayMs);
        return idea;
    }

    /**
     * Start animation to view X translate. See to {@link #newTranslateAnimation(long, float, float, float, float)}.
     * @param delayMs  Delay for animation execute, in millisecond
     * @return  {@link IdeaAnimation} the object called with this
     */
    public IdeaAnimation startTranslateXAnimation(View view, long durationMs, float startX, float endX, long delayMs) {
        IdeaAnimation idea = newTranslateAnimation(durationMs, startX, endX, 0f, 0f);
        postAnimation(idea, view, delayMs);
        return idea;
    }

    /**
     * Start animation to view Y translate. See to {@link #newTranslateAnimation(long, float, float, float, float)}.
     * @param delayMs  Delay for animation execute, in millisecond
     * @return  {@link IdeaAnimation} the object called with this
     */
    public IdeaAnimation startTranslateYAnimation(View view, long durationMs, float startY, float endY, long delayMs) {
        IdeaAnimation idea = newTranslateAnimation(durationMs,0f, 0f, startY, endY);
        postAnimation(idea, view, delayMs);
        return idea;
    }

    /**
     *  Start animation to show view. See to {@link #newAlphaAnimation(long, float, float)}.
     * @param delayMs  Delay for animation execute, in millisecond
     * @return  {@link IdeaAnimation} the object called with this
     */
    public IdeaAnimation startShowAnimation(View view, long durationMs, long delayMs) {
        IdeaAnimation idea = newAlphaAnimation(durationMs, 0f, 1f);
        postAnimation(idea, view, delayMs);
        return idea;
    }

    /**
     *  Start animation to hide view. See to {@link #newAlphaAnimation(long, float, float)}.
     * @param delayMs  Delay for animation execute, in millisecond
     * @return  {@link IdeaAnimation} the object called with this
     */
    public IdeaAnimation startHideAnimation(View view, long durationMs, long delayMs) {
        IdeaAnimation idea = newAlphaAnimation(durationMs, 1f, 0f);
        postAnimation(idea, view, delayMs);
        return idea;
    }

    private void postAnimation(IdeaAnimation idea, View v, long delayMs) {
        idea.startForView(v, delayMs);
    }

    /*/********************** Some class ***********************/

    /** A static class of the animator scheme. **/
    public static final class IdeaAnimator {
        private AnimatorSet animatorSet;
        private ValueAnimator animator;

        /*/********************* Construction **********************/

        private IdeaAnimator() {
            animator = new ValueAnimator();
        }
        private IdeaAnimator(Object obj, String property, Number... values) {
            if (values.length < 2) {
                Assert.fail("Error value. The value is not allowed to be null or less number than two.");
            }

            if (values[0] instanceof Integer) {
                int[] val = new int[values.length];
                for (int i = 0; i < val.length; i++) {
                    val[i] = values[i].intValue();
                    animator = ObjectAnimator.ofInt(obj, property, val);
                }
            } else if (values[0] instanceof Float) {
                float[] val = new float[values.length];
                for (int i = 0; i < val.length; i++) {
                    val[i] = values[i].floatValue();
                    animator = ObjectAnimator.ofFloat(obj, property, val);
                }
            } else {
                Assert.fail("Error value type. The value must be Integer or Float.");
            }

            animatorSet = new AnimatorSet();
        }
        private IdeaAnimator(Object obj, String property, TypeEvaluator evaluator, Object... object) {
            animator = ObjectAnimator.ofObject(obj, property, evaluator, object);
            animatorSet = new AnimatorSet();
        }

        /*/**************** Some public implement *****************/

        public IdeaAnimator addListenerAdapter(AnimatorListenerAdapter listenerAdapter) {
            animator.addListener(listenerAdapter);
            return this;
        }

        public IdeaAnimator addUpdateListener(ValueAnimator.AnimatorUpdateListener listener) {
            animator.addUpdateListener(listener);
            return this;
        }

        public void removeAllListeners() {
            animator.removeAllListeners();
        }

        public void removeAllUpdateListeners() {
            animator.removeAllUpdateListeners();
        }

        /**
         * Take animator come to the relative position of fraction immediately.
         * @return  {@link IdeaAnimator} The object called with setCntFraction()
         **/
        public IdeaAnimator setCntFraction(float fraction) {
            if (Build.VERSION.SDK_INT >= 22) {
                animator.setCurrentFraction(fraction);
            } else {
                Log.e(TAG, "Fail to setCntFraction because of the version lower than Android 5.1");
            }
            return this;
        }

        /**
         * Set duration for animator. It was not include the delay time before start.
         * @param durationMs The length of the animator, in milliseconds. This value cannot be negative
         * @return  {@link IdeaAnimator} The object called with setDuration().
         */
        public IdeaAnimator setDuration(long durationMs) {
            durationMs = durationMs < 0 ? 0 : durationMs;
            animator.setDuration(durationMs);
            return this;
        }

        /**
         * Set a {@link TypeEvaluator} to make control the objectAnimator.
         * @param evaluator  A typeEvaluator to take a scheme to set values to change animator
         * @return  {@link IdeaAnimator} The object called with setTypeEvaluator()
         */
        public IdeaAnimator setTypeEvaluator(@NonNull TypeEvaluator evaluator) {
            animator.setEvaluator(evaluator);
            return this;
        }

        /**
         * Speed control for the objectAnimator.
         * @param interpolator  A timeInterpolator to make speed control for Animator play
         * @return  {@link IdeaAnimator} The object called with setInterpolator()
         **/
        public IdeaAnimator setInterpolator(@NonNull TimeInterpolator interpolator) {
            animator.setInterpolator(interpolator);
            return this;
        }

        /**
         * It will be auto set a {@link android.animation.FloatEvaluator},
         *   so you could not need to call {@link #setTypeEvaluator(TypeEvaluator)}.
         *  @return  {@link IdeaAnimator} The object called with setFloatValues()
         **/
        public IdeaAnimator setFloatValues(@NonNull float... values) {
            animator.setFloatValues(values);
            return this;
        }

        /**
         * It will be auto set the {@link android.animation.IntEvaluator},
         *   so you could not need to call {@link #setTypeEvaluator(TypeEvaluator)}.
         * @param values A value array type of int
         * @return  {@link IdeaAnimator} The object called with setIntValues()
         **/
        public IdeaAnimator setIntValues(@NonNull int... values) {
            Assert.assertTrue(values.length > 1);
            animator.setIntValues(values);
            return this;
        }

        /**
         * It must be use with {@link #setTypeEvaluator(TypeEvaluator)} because the objectAnimator
         *   don't have any objectEvaluator implement to make change with the object value.
         * @param values  A value array type of Object
         * @param evaluator  A typeEvaluator to take a scheme to set values to change animator
         * @return  {@link IdeaAnimator} The object called with setObjectValues()
         **/
        public IdeaAnimator setObjectValues(@NonNull TypeEvaluator evaluator, @NonNull Object... values) {
            Assert.assertTrue(values.length > 1);

            animator.setObjectValues(values);
            animator.setEvaluator(evaluator);
            return this;
        }

        /**
         * Set animation repeat with a mode.
         * The times of animator execute is count + 1
         * @param count Times of animator repeat
         * @param mode The mode of animator repeat. set {@code REPEAT_MODE}
         * @return  {@link IdeaAnimator} The object called with setRepeat()
         **/
        public IdeaAnimator setRepeat(int count, int mode) {
            animator.setRepeatCount(count);
            animator.setRepeatMode(mode);
            return this;
        }

        /**
         * It can be make animation execute delay when call {@link #start()}.
         * @param delay  Delay time of running after call {@link #start()}
         * @return  {@link IdeaAnimator} The object called with setStartDelay()
         **/
        public IdeaAnimator setStartDelay(long delay) {
            animator.setStartDelay(delay);
            return this;
        }

        /**
         * A Unknown method. You can call it if you know what the fuck method use to.
         * @param holders  Unknown
         * @return  {@link IdeaAnimator} The object called with setValues()
         **/
        public IdeaAnimator setValues(PropertyValuesHolder... holders) {
            animator.setValues(holders);
            return this;
        }


        /*/**************** The method what changed animator status *****************/

        public void start() {
            animator.start();
        }

        /** Make animator end in the final state immediately **/
        public void end() {
            if (animator != null) {
                animator.end();
            }

            if (animatorSet != null) {
                animatorSet.end();
            }
        }

        /**
         * It will be force interrupt animator immediately
         *  so it is impossible to ensure that animator execute fully.
         *  If you don't want to stop with a unknown state, you should call {@link #end()}
         *  to make animation end in the final state;
         */
        public void cancel() {
            if (animator != null) {
                animator.cancel();
                animator = null;
            }

            if (animatorSet != null) {
                animatorSet.cancel();
                animatorSet = null;
            }
        }

        /** Same as {@link #end()} **/
        void release() {
            cancel();
        }

        /*/**************** Check animator status *****************/

        public boolean isPause() {
            return animator.isPaused();
        }

        /** It means animator is running when return true **/
        public boolean isRunning() {
            return animator.isRunning();
        }

        /** It will return true during animator wait to start cause by a delay time **/
        public boolean isStarted() {
            return animator.isStarted();
        }

        /*/**************** AnimatorSet implement *****************/

        /** Set some animator execute with the same time **/
        public AnimatorSet playTogether(IdeaAnimator... animations) {
            ValueAnimator[] animatorArray = convertType(animations);
            animatorSet.playTogether(animatorArray);

            return animatorSet;
        }

        /** Set some animator execute sequentially **/
        public AnimatorSet playSequentially(IdeaAnimator... animations) {
            ValueAnimator[] animatorArray = convertType(animations);
            animatorSet.playSequentially(animatorArray);

            return animatorSet;
        }

        /** Start a animatorSet **/
        public void startAnimatorSet() {
            animatorSet.start();
        }

        private boolean checkArrays(IdeaAnimator... animators) {
            boolean allowed = animatorSet == null || animators == null || animators.length < 2;
            if (!allowed) {
                Log.e(TAG, "Animator number is too little to play in range");
                return false;
            }

            return true;
        }

        private ValueAnimator[] convertType(IdeaAnimator... animations) {
            if (!checkArrays(animations)) {
                return null;
            }

            ValueAnimator[] animatorArray = new ValueAnimator[animations.length];
            for (int i = 0; i < animatorArray.length; i++) {
                animatorArray[i] = animations[i].animator;
            }
            return animatorArray;
        }
    }

    /** A static class of the animation scheme **/
    public static final class IdeaAnimation {
        private Animation idea;
        private int type;
        private float startAngle, endAngle;
        private float startAlpha, endAlpha;
        private float startX, endX;
        private float startY, endY;
        private float centerX, centerY;
        private float zoomZ;
        private boolean isReverse;

        private IdeaAnimation() {
        }

        /* parse type to create different animation */
        private void initWithType() {
            switch (type) {
                case TYPE_3D_ROTATE:
                    idea = new Idea3dRotateAnimation(startAngle, endAngle, centerX, centerY, zoomZ, isReverse);
                    break;

                case TYPE_ROTATION:
                    idea = new RotateAnimation(startAngle, endAngle, Animation.ABSOLUTE, centerX, Animation.ABSOLUTE, centerY);
                    break;
                case TYPE_SCALE:
                    idea = new ScaleAnimation(startX, endX, startY, endY, Animation.ABSOLUTE, centerX, Animation.ABSOLUTE, centerY);
                    break;
                case TYPE_ALPHA:
                    idea = new AlphaAnimation(startAlpha, endAlpha);
                    break;
                case TYPE_TRANSLATE:
                    idea = new TranslateAnimation(startX, endX, startY, endY);
                    break;
            }
        }

        public static class Builder {
            private IdeaAnimation ideaAnimation;

            Builder(int type) {
                ideaAnimation = new IdeaAnimation();
                ideaAnimation.type = type;
            }

            /**
             *  Set a center point to take animation.
             * @param centerX  Value of X
             * @param centerY Value of Y
             * @return  {@link Builder}
             */
            Builder setCenter(float centerX, float centerY) {
                ideaAnimation.centerX = centerX;
                ideaAnimation.centerY = centerY;
                return this;
            }

            Builder setX(float startX, float endX) {
                ideaAnimation.startX = startX;
                ideaAnimation.endX = endX;
                return this;
            }

            Builder setY(float startY, float endY) {
                ideaAnimation.startY = startY;
                ideaAnimation.endY = endY;
                return this;
            }

            /**
             * It use for {@link #new3dRotateAnimation(long, float, float, float, float, float)} or {@link #newRotateAnimation(long, float, float, float, float)}.
             * @param startAngle
             * @param endAngle
             * @return
             */
            Builder setAngle(float startAngle, float endAngle) {
                ideaAnimation.startAngle = startAngle;
                ideaAnimation.endAngle = endAngle;
                return this;
            }

            /**
             * Only use for {@link #newAlphaAnimation(long, float, float)}.
             * @param startAlpha
             * @param endAlpha
             * @return
             */
            Builder setAlpha(float startAlpha, float endAlpha) {
                startAlpha = startAlpha < 0f ? 0f : startAlpha;
                startAlpha = startAlpha > 1f ? 1f : startAlpha;
                endAlpha = endAlpha < 0f ? 0f : endAlpha;
                endAlpha = endAlpha > 1f ? 1f : endAlpha;

                ideaAnimation.startAlpha = startAlpha;
                ideaAnimation.endAlpha = endAlpha;
                return this;
            }

            /**
             * Only use for {@link #new3dRotateAnimation(long, float, float, float, float, float)}.
             * @param zoomZ
             * @return
             */
            Builder setZoomZ(float zoomZ) {
                ideaAnimation.zoomZ = zoomZ;
                return this;
            }

            /**
             * Only use for {@link #new3dRotateAnimation(long, float, float, float, float, float)}.
             * @param isReverse Reverse value
             * @return
             */
            Builder setReverse(boolean isReverse) {
                ideaAnimation.isReverse = isReverse;
                return this;
            }

            IdeaAnimation build() {
                ideaAnimation.initWithType();
                return ideaAnimation;
            }
        }

        public IdeaAnimation setDuration(long durationMs) {
            idea.setDuration(durationMs);
            return this;
        }

        public IdeaAnimation setListener(Animation.AnimationListener listener) {
            idea.setAnimationListener(listener);
            return this;
        }

        public IdeaAnimation setFillBefore(boolean fillBefore) {
            idea.setFillBefore(fillBefore);
            return this;
        }

        public IdeaAnimation setFillAfter(boolean fillAfter) {
            idea.setFillAfter(fillAfter);
            return this;
        }

        public IdeaAnimation setFillEnabled(boolean fillEnabled) {
            idea.setFillEnabled(fillEnabled);
            return this;
        }

        public IdeaAnimation setInterpolator(Interpolator interpolator) {
            idea.setInterpolator(interpolator);
            return this;
        }

        public IdeaAnimation setInterpolator(Context context, int resId) {
            idea.setInterpolator(context, resId);
            return this;
        }

        public IdeaAnimation setRepeat(int repeatCount, int repeatMode) {
            idea.setRepeatCount(repeatCount);
            idea.setRepeatMode(repeatMode);
            return this;
        }

        public IdeaAnimation setStartOffset(long startOffsetMs) {
            idea.setStartOffset(startOffsetMs);
            return this;
        }

        public IdeaAnimation setStartTime(long startTimeMs) {
            idea.setStartTime(startTimeMs);
            return this;
        }

        public IdeaAnimation setBackgroundColor(int backgroundColor) {
            idea.setBackgroundColor(backgroundColor);
            return this;
        }

        /**
         * Start animation by view.
         *
         * @param view    The view to execute animation
         * @param delayMs Delay to execute animation, in millisecond
         */
        public void startForView(View view, long delayMs) {
            view.postDelayed(() -> view.startAnimation(idea), delayMs);
        }

        /**
         * Cancel animation.
         */
        public void cancel() {
            if (idea != null) {
                idea.cancel();
                idea = null;
            }
        }

        /** Same as {@link #cancel()}. **/
        void release() {
            cancel();
        }
    }

    /** A static class of the 3d rotation animation implement. **/
    private static final class Idea3dRotateAnimation extends Animation {
        private float startAngle, endAngle;
        private float centerX, centerY;
        private float zoomZ;
        private boolean isReverse;
        private Camera camera;

        Idea3dRotateAnimation(float startAngle, float endAngle, float centerX, float centerY, float zoomZ, boolean isReverse) {
            this.startAngle = startAngle;
            this.endAngle = endAngle;
            this.centerX = centerX;
            this.centerY = centerY;
            this.zoomZ = zoomZ;
            this.isReverse = isReverse;
            camera = new Camera();
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            float degrees = startAngle + ((endAngle - startAngle) * interpolatedTime);
            camera.save();
            float z = isReverse ? (zoomZ * interpolatedTime) : (zoomZ * (1.0f - interpolatedTime));
            camera.translate(0.0f, 0.0f, z);
            camera.rotateY(degrees);
            camera.getMatrix(t.getMatrix());
            camera.restore();

            t.getMatrix().preTranslate(-centerX, -centerY);
            t.getMatrix().postTranslate(centerX, centerY);
        }

        @Override
        public void cancel() {
            super.cancel();

            camera = null;
        }
    }


    /*/************** Property factory implement *****************/

    /**
     * A static class that have two special methods name getCustom() and setCustom(T).
     *  It was useless unless you override it. You must override the method that getCustom() and setCustom(T).
     *   or be created in subclass if not such method in the parent
     **/
    public static class DefaultPropertyFactory implements PropertyFactory {
        private Object object;
        private final String MSG_ERROR = "It was useless now, you should to override it.";
        
        public DefaultPropertyFactory(Object object) {
            this.object = object;
        }

        //visibility
        public void setVisibility(int visibility) {
            checkObjectType();
            if (visibility != View.GONE && visibility != View.INVISIBLE) {
                visibility = View.VISIBLE;
            }
            ((View)object).setVisibility(visibility);
        }

        //alpha
        public void setAlpha(float alpha) {
            checkObjectType();
            ((View)object).setAlpha(alpha);
        }

        //pos
        public void setX(int x) {
            checkObjectType();
            ((View)object).setX(x);
            ((View)object).requestLayout();
        }
        public void setY(int y) {
            checkObjectType();
            ((View)object).setY(y);
            ((View)object).requestLayout();
        }
        public void setZ(int z) {
            checkObjectType();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ((View)object).setZ(z);
            }
            ((View)object).requestLayout();
        }

        //width and height
        public void setWidth(int width) {
            checkObjectType();
            ((View)object).getLayoutParams().width = width;
            ((View)object).requestLayout();
        }
        public void setHeight(int height) {
            checkObjectType();
            ((View)object).getLayoutParams().height = height;
            ((View)object).requestLayout();
        }

        //rotation
        public void setRotation(int rotation) {
            checkObjectType();
            ((View)object).setRotation(rotation);
        }
        public void setRotationX(int rotationX) {
            checkObjectType();
            ((View)object).setRotationX(rotationX);
        }
        public void setRotationY(int rotationY) {
            checkObjectType();
            ((View)object).setRotationY(rotationY);
        }

        //scale
        public void setScaleX(int scaleX) {
            checkObjectType();
            ((View)object).setScaleX(scaleX);
        }
        public void setScaleY(int scaleY) {
            checkObjectType();
            ((View)object).setScaleY(scaleY);
        }

        //translate
        public void setTranslationX(float translationX) {
            checkObjectType();
            ((View)object).setTranslationX(translationX);
        }
        public void setTranslationY(float translationY) {
            checkObjectType();
            ((View)object).setTranslationY(translationY);
        }
        public void setTranslationZ(float translationZ) {
            checkObjectType();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ((View)object).setTranslationZ(translationZ);
            }
        }

        //padding
        public void setPaddingLeft(int left) {
            setPadding(left, 0, 0, 0);
        }
        public void setPaddingTop(int top) {
            setPadding(0, top, 0, 0);
        }
        public void setPaddingRight(int right) {
            setPadding(0, 0, right, 0);
        }
        public void setPaddingBottom(int bottom) {
            setPadding(0, 0, 0, bottom);
        }
        public void setPadding(int left, int top, int right, int bottom) {
            checkObjectType();
            ((View)object).setPadding(left, top, right, bottom);
        }

        public void setTextColor(String value) {
            if (object instanceof TextView) {
                ((TextView)object).setTextColor(Color.parseColor(value));
            }
        }

        public void setBackgroundColor(String value) {
            if (object instanceof View) {
                ((View)object).setBackgroundColor(Color.parseColor(value));
            }
        }

        private void checkObjectType() {
            Assert.assertTrue("Error: Object if not a View!", object != null && object instanceof View);
        }
        
        @Override
        public void setCustom(boolean value) {
            Log.e(TAG, MSG_ERROR);
        }
        @Override
        public void setCustom(short value) {
            Log.e(TAG, MSG_ERROR);
        }
        @Override
        public void setCustom(byte value) {
            Log.e(TAG, MSG_ERROR);
        }
        @Override
        public void setCustom(int value) {
            Log.e(TAG, MSG_ERROR);
        }
        @Override
        public void setCustom(float value) {
            Log.e(TAG, MSG_ERROR);
        }
        @Override
        public void setCustom(double value) {
            Log.e(TAG, MSG_ERROR);
        }
        @Override
        public void setCustom(long value) {
            Log.e(TAG, MSG_ERROR);
        }
        @Override
        public void setCustom(String value) {
            Log.e(TAG, MSG_ERROR);
        }
        @Override
        public void setCustom(Object value) {
            Log.e(TAG, MSG_ERROR);
        }
    }

    /*/******************************** Interface ******************************/

    /**
     * Interface for a Factory by property of object.
     **/
    public interface PropertyFactory {
        void setCustom(boolean value);
        void setCustom(short value);
        void setCustom(byte value);
        void setCustom(int value);
        void setCustom(float value);
        void setCustom(long value);
        void setCustom(double value);
        void setCustom(String value);
        void setCustom(Object value);
    }


    /*/************************* Some type evaluator  *************************/

    /**
     * Color smooth transition.
     *  It must be to use the params that take a specific flavor just like as "123456","#123456", "12345678" or "#12345678".
     *  It will return value type of String.
     **/
    public static final class ColorTypeEvaluator implements TypeEvaluator {
        private final int[] currentColors = {-1, -1, -1};
        private final int[] startColors = new int[3];
        private final int[] endColors = new int[3];
        private final int[] deltaColors = new int[3];
        private int colorDelta = 0;

        @Override
        public Object evaluate(float fraction, Object startValue, Object endValue) {
            String startColorValue = (String) startValue;
            String endColorValue = (String) endValue;

            //分离ARGB四个值
            parseValue(startColorValue, endColorValue);
            
            for (int i = 0; i < currentColors.length; i++) {
                currentColors[i] = currentColors[i] == -1 ? startColors[i] : currentColors[i];  // 初始化颜色的值
                deltaColors[i] = Math.abs(startColors[i] - endColors[i]);  // 计算初始颜色和结束颜色之间的差值
            }
            colorDelta = deltaColors[0] + deltaColors[1] + deltaColors[2];

            //？？？
            if (currentColors[0] != endColors[0]) {
                currentColors[0] = getCurrentColor(startColors[0], endColors[0], colorDelta, 0, fraction);
            } else if (currentColors[1] != endColors[1]) {
                currentColors[1] = getCurrentColor(startColors[1], endColors[1], colorDelta, deltaColors[0], fraction);
            } else if (currentColors[2] != endColors[2]) {
                currentColors[2] = getCurrentColor(startColors[2], endColors[2], colorDelta, deltaColors[0] + deltaColors[1], fraction);
            }

            // 将计算出的当前颜色的值组装返回
            return "#" + getHexString(currentColors[0]) + getHexString(currentColors[1]) + getHexString(currentColors[2]);
        }

        /**
         * 根据fraction值来计算当前的颜色。
         */
        private int getCurrentColor(int startColors, int endColors, int colorDiff, int offset, float fraction) {
            int currentColors;
            if (startColors > endColors) {
                currentColors = (int) (startColors - (fraction * colorDiff - offset));
                if (currentColors < endColors) {
                    currentColors = endColors;
                }
            } else {
                currentColors = (int) (startColors + (fraction * colorDiff - offset));
                if (currentColors > endColors) {
                    currentColors = endColors;
                }
            }
            return currentColors;
        }

        /**
         * 将10进制颜色值转换成16进制。
         */
        private String getHexString(int value) {
            String hexString = Integer.toHexString(value);
            return  hexString.length() == 1 ? "0" + hexString : hexString;
        }

        /**
         * 得到的颜色等效值可能有几种情况：
         * "123456"  "#123456" "#12345678"  "12345678"
         * 全部转换为RGB格式
         */
        private void parseValue(String startValue, String endValue) {
            startValue = parseValueNum(startValue);
            endValue = parseValueNum(endValue);

            for (int i = 0; i < startColors.length; i += 2) {
                startColors[i] = Integer.parseInt(startValue.substring(i, i + 2), 16);
                endColors[i] = Integer.parseInt(endValue.substring(i, i + 2), 16);
            }
        }

        private String parseValueNum(String value) {
            switch (value.length()) {
                case 6:
                    return value;
                case 7:
                    return value.substring(1);
                case 8:
                    return value.substring(3);
                case 9:
                    return value.substring(2);
                default:
                    return "ffffff";
            }
        }
    }
}
