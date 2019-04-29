package com.qxtx.idea.animator;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.Path;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import com.qxtx.idea.ArgbTypeEvaluator;
import com.qxtx.idea.IManager;
import com.qxtx.idea.IdeaUtil;
import com.qxtx.idea.PropertyFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * A class for take animator easily.
 *
 * @see IdeaAnimator
 * @see IdeaUtil
 */
public class IdeaAnimatorManager implements IManager<IdeaAnimator> {
    private static final String TAG = "IdeaAnimatorManager";
    private static final long DEFAULT_DURATION = 500;
    private static IdeaAnimatorManager manager;
    private List<IdeaAnimator> animatorList;

    private IdeaAnimatorManager() {
        animatorList = new ArrayList<>();
    }

    /**
     * It must be call before create a new animate.
     * @return {@link IdeaAnimatorManager} The object of this class
     */
    public static IdeaAnimatorManager getInstance() {
        if (manager == null) {
            synchronized (IdeaAnimatorManager.class) {
                if (manager == null) {
                    manager = new IdeaAnimatorManager();
                }
            }
        }
        return manager;
    }

    @Override
    public void add(IdeaAnimator idea) {
        animatorList.add(idea);
    }

    @Override
    public void remove(IdeaAnimator idea) {
        animatorList.remove(idea);
    }

    @Override
    public void remove(@NonNull String tag) {
        for (int i = 0; i < animatorList.size(); i++) {
            if (animatorList.get(i).getTag().equals(tag)) {
                animatorList.remove(i);
                i--;
            }
        }
    }

    @Override
    public void remove(int index) {
        animatorList.remove(index);
    }

    @Override
    public int getCount() {
        return animatorList.size();
    }

    @Override
    public List<IdeaAnimator> getAnimateList() {
        return animatorList;
    }

    @Override
    public List<IdeaAnimator> get(@NonNull String tag) {
        List<IdeaAnimator> ideas = new ArrayList<>();
        for (IdeaAnimator idea : animatorList) {
            if (idea.getTag().equals(tag)) {
                ideas.add(idea);
            }
        }
        return ideas;
    }

    public static IdeaAnimator path(@NonNull View target, Path path) {
        return baseIdea(target, DEFAULT_DURATION).setPath(path);
    }

    public static IdeaAnimator alpha(@NonNull View target, float... values) {
        return floatIdea(target, DEFAULT_DURATION, "alpha", values);
    }

    public static IdeaAnimator alphaShow(@NonNull View target) {
        float startAlpha = target.getAlpha();
        IdeaAnimator idea = alpha(target, startAlpha, 1f);
        idea.setAllowStart(startAlpha != 1f);
        return idea;
    }

    public static IdeaAnimator alphaHide(@NonNull View target) {
        float startAlpha = target.getAlpha();
        IdeaAnimator idea = alpha(target, startAlpha, 0f);
        idea.setAllowStart(startAlpha != 0f);
        return idea;
    }

    public static IdeaAnimator bgColorfully(@NonNull final Object target, @NonNull String... colorString) {
        Object insteadTarget = target;
        if (!(target instanceof PropertyFactory)) {
            insteadTarget = new PropertyFactory<String>(target) {
                @Override
                public void setCustom(String value) {
                    if (target instanceof View) {
                        ((View)target).setBackgroundColor(Color.parseColor(value));
                    }
                }
            };
        }
        return baseIdea(insteadTarget, DEFAULT_DURATION * 10)
                .setPropertyName(PropertyFactory.PROPERTY_CUSTOM)
                .setObjectValues(new ArgbTypeEvaluator(), (Object) colorString);
    }

    public static IdeaAnimator bgColorfully(@NonNull View target, @NonNull int... colorValues) {
        return baseIdea(target, DEFAULT_DURATION * 10)
                .setPropertyName("backgroundColor")
                .setIntValues(colorValues)
                .setTypeEvaluator(new ArgbEvaluator());
    }

    public static IdeaAnimatorSet bounce(@NonNull View target, float high) {
        return bounce(target, high, IdeaUtil.VERTICAL);
    }

    public static IdeaAnimatorSet bounce(@NonNull View target, float high, @IdeaUtil.Orientation int orientation) {
        long duration = 1000;
        high = high < 0f ? 0f : high;
        String propertyName = orientation == IdeaUtil.HORIZONTAL ? "translationX" : "translationY";
        return IdeaAnimatorSetManager.sequentially(
                floatIdea(target, duration / 12, propertyName, 0f, -high)
                        .setInterpolator(new DecelerateInterpolator()),
                floatIdea(target, duration / 4, propertyName, -high, 0f)
                        .setInterpolator(new BounceInterpolator()));
    }

    public static IdeaAnimator breathe(@NonNull View target) {
        float startAlpha = target.getAlpha();
        float cAlpha = startAlpha == 1f ? 0f : 1f;
        return baseIdea(target, DEFAULT_DURATION * 2)
                .setRepeat(IdeaUtil.INFINITE, IdeaUtil.MODE_REVERSE)
                .setPropertyName("alpha")
                .setFloatValues(startAlpha, cAlpha, startAlpha)
                .setInterpolator(new LinearInterpolator());
    }

    public static IdeaAnimator[] domino(@NonNull View[] targets, @IdeaUtil.Direction int direction) {
        IdeaAnimator[] ideas = new IdeaAnimator[targets.length];
        long[] durations = new long[ideas.length];
        float[] degrees = new float[ideas.length];

        int index = checkCollision(targets, degrees);
        for (int i = 0; i < durations.length; i++) {
            durations[i] = DEFAULT_DURATION;
        }

        for (int i = 0; i < targets.length; i++) {
            if (i > index) {
                degrees[i] = 0f;
            }

            ideas[i] = IdeaAnimatorManager
                    .rolling(targets[i], direction, degrees[i]).setDuration(durations[i])
                    .setStartDelay((durations[i] * i) / 2);
        }

        return ideas;
    }

    public static IdeaAnimator door(@NonNull View target, @IdeaUtil.Direction int direction) {
        float toValue;
        float height = (float)target.getHeight();
        float width = (float)target.getWidth();
        float rotateX = target.getRotationX();
        float rotateY = target.getRotationY();
        String type = (direction == IdeaUtil.LEFT || direction == IdeaUtil.RIGHT) ? "y" : "x";

        switch (direction) {
            case IdeaUtil.LEFT:
                toValue = rotateY == 0f ? -180f : 0f;
                return doorMove(target, 0f, height / 2f, toValue, type);
            case IdeaUtil.TOP:
                toValue = rotateX == 0f ? 180f : 0f;
                return doorMove(target, width / 2f, 0f, toValue, type);
            case IdeaUtil.RIGHT:
                toValue = rotateY == 0f ? 180f : 0f;
                return doorMove(target, width, height / 2f, toValue, type);
            case IdeaUtil.BOTTOM:
                toValue = rotateX == 0f ? -180f : 0f;
                return doorMove(target, width / 2f, height, toValue, type);
        }
        throw new IllegalStateException("Invalid direction!");
    }

    public static IdeaAnimator flicker(@NonNull View target) {
        int fromVisible = target.getVisibility();
        int toVisible = fromVisible == View.VISIBLE ? View.INVISIBLE : View.VISIBLE;
        return new IdeaAnimator()
                .setDuration(DEFAULT_DURATION)
                .setIntValues(fromVisible, toVisible)
                .setRepeat(IdeaUtil.INFINITE, IdeaUtil.MODE_REVERSE)
                .setInterpolator(new LinearInterpolator())
                .addUpdateListener(animation -> {
                    float fraction = animation.getAnimatedFraction();
                    if (fraction < 0.5f) {
                        target.setVisibility(fromVisible);
                    } else {
                        target.setVisibility(toVisible);
                    }
                });
    }

    public static IdeaAnimatorSet heartBeats(@NonNull View target, int level) {
        long duration = 800;
        level = level > 10 ? 10 : level;
        level = level <= 0 ? 1 : level;
        float beatsLevel = (float)level * 0.2f;
        return IdeaAnimatorSetManager.together(
                floatIdea(target, duration, "scaleX", 1f, 1f, beatsLevel, 1f),
                floatIdea(target, duration, "scaleY", 1f, 1f, beatsLevel, 1f))
                .setRepeat(IdeaUtil.INFINITE, IdeaUtil.MODE_RESTART)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(duration);
    }

    /**
     * ！！！parse a math path of animator. It maybe simply to make a complex path of caller.
     * @param target The object of execute animator
     * @param math The formula of path just like "y = kx + b"
     * @return {@link IdeaAnimator} The Object which call this.
     */
    public static IdeaAnimator mathPath(@NonNull View target, String math) {
        IdeaAnimator idea = baseIdea(target, DEFAULT_DURATION);
        math = math.replace(" ", "").toLowerCase().substring(2);

        float k = 1f;
        float b = 0f;
        char[] array = math.toCharArray();
        return idea;
    }

    public static IdeaAnimator rolling(@NonNull View target, @IdeaUtil.Direction int direction, float degress) {
        return roll(target, degress, direction);
    }

    public static IdeaAnimator rolling(@NonNull View target, @IdeaUtil.Direction int direction, int circle) {
        float height = (float)target.getHeight();
        float width = (float)target.getWidth();
        long duration = circle * 1000;

        target.setRotation(0f);

        IdeaAnimator idea = roll(target, 360f * circle, direction);
        if (idea == null) {
            return null;
        }

        idea.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            private final float alreadyMoveX = target.getTranslationX();
            private final float alreadyMoveY = target.getTranslationY();
            private final float alreadyRotate = target.getRotation();
            private final float[][] rules = new float[][] {{0f, 0f}, {width, 0f}, {width, height}, {0f, height}};
            private final float[][] pivots = checkPivots(rules, direction);
            private float[] translate = new float[] {alreadyMoveX, alreadyMoveY};

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (target.getRotation() % 90f != 0f) {
                    return ;
                }

                int process = (int)(target.getRotation()- alreadyRotate) / 90;
                switch (process % 4) {
                    case 1:
                        translate[0] += height;
                        translate[1] = alreadyMoveY + height;
                        break;
                    case 2:
                        translate[0] += 2f * width;
                        translate[1] = alreadyMoveY + height;
                        break;
                    case 3:
                        translate[0] += height;
                        translate[1] = alreadyMoveY + 0f;
                        break;
                }

                if (process > 0) {
                    fixTargetParams(process, translate, circle);
                }
            }

            private void fixTargetParams(int process, float[] translate, int circle) {
                if (process > 0) {
                    int sequence = ((process % 4) == 0 ? 4 : process % 4) - 1;
                    target.setPivotX(pivots[sequence][0]);
                    target.setPivotY(pivots[sequence][1]);
                }

                if ((int)(target.getRotation() - alreadyRotate) != (360 * circle)) {
                    target.setTranslationX(translate[0]);
                    target.setTranslationY(translate[1]);
                }
            }
        }).setDuration(duration);

        return idea;
    }

    public static IdeaAnimator rotate(@NonNull View target, float... values) {
        return floatIdea(target, DEFAULT_DURATION, "rotation", values);
    }

    public static IdeaAnimator rotateX(@NonNull View target, float... values) {
        return floatIdea(target, DEFAULT_DURATION, "rotationX", values);
    }

    public static IdeaAnimator rotateY(@NonNull View target, float... values) {
        return floatIdea(target, DEFAULT_DURATION, "rotationY", values);
    }

    public static IdeaAnimatorSet scale(@NonNull View target, float... values) {
        return IdeaAnimatorSetManager.together(
                floatIdea(target, DEFAULT_DURATION, "scaleX", values),
                floatIdea(target, DEFAULT_DURATION, "scaleY", values));
    }

    public static IdeaAnimator scaleX(@NonNull View target, float... values) {
        return floatIdea(target, DEFAULT_DURATION, "scaleX", values);
    }

    public static IdeaAnimator scaleY(@NonNull View target, float... values) {
        return floatIdea(target, DEFAULT_DURATION, "scaleY", values);
    }

    public static IdeaAnimator shake(@NonNull View target, @IdeaUtil.Orientation int orientation, int level) {
        boolean isHor = orientation == IdeaUtil.HORIZONTAL;
        float shakeValue = (level > 100f ? 10f : (float)level) * 10f;
        String propertyName = isHor ? "translationX" : "transLationY";
        float[] values = new float[] {0f, shakeValue, -shakeValue, shakeValue, -shakeValue, 0f};

        return baseIdea(target, 200)
                .setPropertyName(propertyName)
                .setFloatValues(values)
                .setInterpolator(new LinearInterpolator());
    }

    public static IdeaAnimator swinging(@NonNull View target, float toAngle) {
        return rotate(target, 300, 0f, toAngle, -toAngle, 0f)
                .setRepeat(1, IdeaUtil.MODE_RESTART);
    }

    public static IdeaAnimator textColorfully(@NonNull Object target, @NonNull int... colorValues) {
        Object insteadTarget = target;
        if (!(target instanceof PropertyFactory)) {
            insteadTarget = new PropertyFactory<String>(target) {
                @Override
                public void setCustom(String value) {
                    if (target instanceof TextView) {
                        ((TextView)target).setTextColor(Color.parseColor(value));
                    }
                }
            };
        }
        return baseIdea(insteadTarget, DEFAULT_DURATION * 10)
                .setTypeEvaluator(new ArgbEvaluator())
                .setIntValues(colorValues);
    }

    public static IdeaAnimator textColorfully(@NonNull Object target, @NonNull String... colorString) {
        Object insteadTarget = target;
        if (!(target instanceof PropertyFactory)) {
            insteadTarget = new PropertyFactory<String>(target) {
                @Override
                public void setCustom(String value) {
                    if (target instanceof TextView) {
                        ((TextView)target).setTextColor(Color.parseColor(value));
                    }
                }
            };
        }
        return baseIdea(insteadTarget, DEFAULT_DURATION * 10)
                .setPropertyName(PropertyFactory.PROPERTY_CUSTOM)
                .setObjectValues(new ArgbTypeEvaluator(), (Object) colorString);
    }

    public static IdeaAnimator translate(@NonNull View target, float fromX, float fromY, float toX, float toY) {
        Path path = new Path();
        path.moveTo(fromX, fromY);
        path.lineTo(toX, toY);
        return baseIdea(target, DEFAULT_DURATION).setPath(path);
    }

    public static IdeaAnimator translateRelative(@NonNull View target, float relativeX, float relativeY) {
        int[] pos = new int[2];
        target.getLocationOnScreen(pos);
        return translate(target, pos[0], pos[1], pos[0] + relativeX, pos[1] + relativeY);
    }

    public static IdeaAnimator translateX(@NonNull View target, float... values) {
        return floatIdea(target, DEFAULT_DURATION, "translationX", values);
    }

    public static IdeaAnimator translateY(@NonNull View target, float... values) {
        return floatIdea(target, DEFAULT_DURATION, "translationY", values);
    }

    private static IdeaAnimator floatIdea(@NonNull Object o, long duration, @NonNull String property, float... values) {
        if (values == null || values.length == 0) {
            values = new float[] {0f, 360f};
        } else if (values.length == 1) {
            values = new float[] {0f, values[0]};
        }
        return baseIdea(o, duration).setPropertyName(property).setFloatValues(values);
    }

    private static IdeaAnimator baseIdea(@NonNull Object o, long duration) {
        return new IdeaAnimator(o).setDuration(duration);
    }

    private static IdeaAnimator doorMove(@NonNull View target, float pivotX, float pivotY, float toValue, String type) {
        target.setPivotX(pivotX);
        target.setPivotY(pivotY);
        boolean isHorizontal = type.equals("x");
        float fromValue = isHorizontal ? target.getRotationX() : target.getRotationY();
        return isHorizontal ? rotateX(target, fromValue, toValue) : rotateY(target, fromValue, toValue);
    }

    private static IdeaAnimator roll(@NonNull View target, float degrees, int direction) {
        float height = (float)target.getHeight();
        float width = (float)target.getWidth();
        if (height == 0f || width == 0f) {
            Log.e(TAG, "Fail to execute rolling. Target view is not ready.");
            return null;
        }

        float pivotX = (direction == IdeaUtil.LEFT || direction == IdeaUtil.BOTTOM) ? 0f : width;
        float pivotY = direction == IdeaUtil.TOP ? 0f : height;
        target.setPivotX(pivotX);
        target.setPivotY(pivotY);

        float fromValue = target.getRotation();
        float toValue = direction == IdeaUtil.LEFT ? fromValue - degrees : fromValue + degrees;
        return rotate(target, fromValue, toValue)
                .setInterpolator(new LinearInterpolator());
    }

    private static float[][] checkPivots(float[][] rules, int direction) {
        switch (direction) {
            case IdeaUtil.LEFT:
                return rules;
            case IdeaUtil.TOP:
                return new float[][] {rules[0], rules[3], rules[1], rules[2]};
            case IdeaUtil.RIGHT:
                return new float[][] {rules[1], rules[0], rules[3], rules[2]};
            case IdeaUtil.BOTTOM:
                return new float[][] {rules[2], rules[1], rules[0], rules[3]};
            default:
                throw new IllegalStateException("Invalid direction of rolling animtor!");
        }
    }

    private static int checkCollision(@NonNull View[] targets, float[] degrees) {
        int index = 1;
        for (int i = 0; i < targets.length; i++) {
            int v0_w = targets[i].getWidth();
            int v0_h = targets[i].getHeight();
            int[] v0_pos = new int[2];
            int[] v1_pos = new int[2];
            targets[i].getLocationOnScreen(v0_pos);
            if (i + 1 < targets.length) {
                targets[i + 1].getLocationOnScreen(v1_pos);
            } else {
                return targets.length - 1;
            }

            float v0_c_x = v0_pos[0] + v0_w;
            float v0_c_y = v0_pos[1] + v0_h;
            float v0_b_x = v1_pos[0];
            float v1_h = targets[i + 1].getHeight();

            float len_trueLen = v0_b_x - v0_c_x;
            if (v1_pos[0] - v0_c_x < v0_h) {
                float y_b0 = v0_c_y - (float)Math.sqrt(v0_h * v0_h - Math.pow(v0_b_x - v0_c_x, 2));
                if (v1_pos[1] < y_b0 && (y_b0 < v1_pos[1] + v1_h / 2f)) {
                    degrees[i] = 90f - (float)(180f * Math.acos(len_trueLen / v0_h) / Math.PI);
                    index++;
                } else {
                    Log.e(TAG, "The one can not be collision next one." + i);
                }
            }
        }

        return index;
    }

    @Override
    public void release() {
        if (animatorList != null) {
            for (IdeaAnimator idea : animatorList) {
                idea.release();
            }
            animatorList.clear();
            animatorList = null;
        }

        manager = null;
    }
}