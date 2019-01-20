package com.qxtx.test.animate;

import android.animation.ArgbEvaluator;
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

import junit.framework.Assert;
import java.util.ArrayList;
import java.util.List;

public class IdeaAnimatorManager implements IManager<IdeaAnimator> {
    private static final long DEFAULT_DURATION = 500;
    private static final String TAG = "IdeaAnimatorManager";
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
    public void add(IdeaAnimator animator) {
        animatorList.add(animator);
    }

    @Override
    public void remove(IdeaAnimator animator) {
        animatorList.remove(animator);
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
                .setObjectValues(new ArgbTypeEvaluator(), colorString);
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
        String propertyName = orientation == IdeaUtil.HORIZONTAL ? "translationY" : "translationY";
        return IdeaAnimatorSetManager.sequentially(
                floatIdea(target, duration / 12, propertyName, 0f, -high)
                        .setInterpolator(new DecelerateInterpolator()),
                floatIdea(target, duration / 4, propertyName, -high, 0f)
                        .setInterpolator(new BounceInterpolator()));
    }

    public static IdeaAnimator breathe(@NonNull View target) {
        float startAlpha = target.getAlpha();
        float cAlpha = startAlpha == 1f ? 0f : 1f;
        return baseIdea(target, 1000)
                .setRepeat(IdeaUtil.INFINITE, IdeaUtil.MODE_REVERSE)
                .setPropertyName("alpha")
                .setFloatValues(startAlpha, cAlpha, startAlpha)
                .setInterpolator(new LinearInterpolator());
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
    public static IdeaAnimator mathematicalPath(@NonNull View target, String math) {
        IdeaAnimator idea = null;

        //Do nothing now

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
        float shakeValue = (level > 10 ? 10f : (float)level) * 10f;
        String propertyName = isHor ? "translationX" : "transLationY";
        float[] values = new float[] {0f, shakeValue, -shakeValue, shakeValue, -shakeValue, 0f};

        return baseIdea(target, 200)
                .setPropertyName(propertyName)
                .setFloatValues(values)
                .setInterpolator(new LinearInterpolator());
    }

    public static IdeaAnimator swinging(@NonNull View target, float angle) {
        return rotate(target, 300, 0f, angle, -angle, 0f)
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
                .setObjectValues(new ArgbTypeEvaluator(), colorString);
    }

    public static IdeaAnimator translate(@NonNull View target, float fromX, float fromY, float toX, float toY) {
        Path path = new Path();
        path.moveTo(fromX, fromY);
        path.lineTo(toX, toY);
        return baseIdea(target, DEFAULT_DURATION).setPath(path);
    }

    public static IdeaAnimator translateX(@NonNull View target, float... values) {
        return floatIdea(target, DEFAULT_DURATION, "translationX", values);
    }

    public static IdeaAnimator translateY(@NonNull View target, float... values) {
        return floatIdea(target, DEFAULT_DURATION, "translationY", values);
    }

    private static IdeaAnimator floatIdea(@NonNull Object o, long duration, @NonNull String property, float... values) {
        return baseIdea(o, duration).setPropertyName(property).setFloatValues(values);
    }

    private static IdeaAnimator baseIdea(@NonNull Object o, long duration) {
        return new IdeaAnimator(o).setDuration(duration);
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