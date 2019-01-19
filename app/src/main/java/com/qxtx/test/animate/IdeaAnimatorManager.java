package com.qxtx.test.animate;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Path;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import junit.framework.Assert;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class IdeaAnimatorManager implements IManager<IdeaAnimator> {
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

    public List<IdeaAnimator> get(@NonNull Object target) {
        List<IdeaAnimator> ideas = new ArrayList<>();
        try {
            for (int i = 0; i < animatorList.size(); i++) {
                IdeaAnimator idea = animatorList.get(i);
                if (idea.target.get() == target) {
                    ideas.add(idea);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ideas;
    }

    public static IdeaAnimator animatorPath(@NonNull Object target, Path path, long duration) {
        checkIsView(target);
        return baseIdea(target, duration).setPath(path);
    }

    public static IdeaAnimator animatorLinearPath(@NonNull Object target,
                                          float fromX, float toX, float fromY, float toY,
                                          long duration) {
        checkIsView(target);
        Path path = new Path();
        path.moveTo(fromX, fromY);
        path.lineTo(toX, toY);
        return baseIdea(target, duration).setPath(path);
    }

    /**
     * ！！！parse a math path of animator. It maybe simply to make a complex path of caller.
     * @param target The object of execute animator
     * @param math The formula of path include variable "x" and result "y"
     * @return {@link IdeaAnimator} The Object which call this.
     */
    public static IdeaAnimator mathematicalPath(@NonNull Object target, String math) {
        IdeaAnimator idea = null;

        //Do nothing now

        return idea;
    }

    public static IdeaAnimator rotateCenter(@NonNull Object target, long duration, float... values) {
        return floatIdea(target, duration, "rotation", values);
    }

    public static IdeaAnimator rotateX(@NonNull Object target, long duration, float... values) {
        return floatIdea(target, duration, "rotationX", values);
    }

    public static IdeaAnimator rotateY(@NonNull Object target, long duration, float... values) {
        return floatIdea(target, duration, "rotationY", values);
    }

    public static IdeaAnimator translationX(@NonNull Object target, long duration, float... values) {
        return floatIdea(target, duration, "translationX", values);
    }

    public static IdeaAnimator translationY(@NonNull Object target, long duration, float... values) {
        return floatIdea(target, duration, "translationY", values);
    }

    public static IdeaAnimatorSet scaleCenter(@NonNull Object target, long duration, float... values) {
        return IdeaAnimatorSetManager.together(
                floatIdea(target, duration, "scaleX", values),
                floatIdea(target, duration, "scaleY", values));
    }

    public static IdeaAnimator scaleX(@NonNull Object target, long duration, float... values) {
        return floatIdea(target, duration, "scaleX", values);
    }

    public static IdeaAnimator scaleY(@NonNull Object target, long duration, float... values) {
        return floatIdea(target, duration, "scaleY", values);
    }

    public static IdeaAnimator alpha(@NonNull Object target, long duration, float... values) {
        return floatIdea(target, duration, "alpha", values);
    }

    public static IdeaAnimator alphaShow(@NonNull Object target, long duration) {
        return floatIdea(target, duration, "alpha", 0f, 1f);
    }

    public static IdeaAnimator alphaHide(@NonNull Object target, long duration) {
        float startAlpha = target instanceof View ? ((View) target).getAlpha() : 0f;
        return floatIdea(target, duration, "alpha", startAlpha, 0f);
    }

    public static IdeaAnimator breath(@NonNull Object target, long duration, int repeat) {
        checkIsView(target);
        long time = duration < 1000 ? 1000 : duration;
        float startAlpha = ((View) target).getAlpha();
        int count = repeat < 0 ? IdeaUtil.INFINITE : repeat;
        return baseIdea(target, time)
                .setRepeat(count, IdeaUtil.MODE_REVERSE)
                .setPropertyName("alpha")
                .setFloatValues(startAlpha, 1f - startAlpha, startAlpha);
    }

    public static IdeaAnimator shake(@NonNull Object target, long duration, @IdeaUtil.Orientation int orientation, int level) {
        checkIsView(target);
        boolean isHor = orientation == IdeaUtil.HORIZONTAL;
        float shakeValue = (level > 10 ? 10f : (float)level) * 10f;
        String propertyName = isHor ? "translationX" : "transLationY";
        float[] values = new float[] {0f, shakeValue, -shakeValue,
                shakeValue, -shakeValue, shakeValue, -shakeValue, 0f};

        return baseIdea(target, duration)
                .setPropertyName(propertyName)
                .setFloatValues(values)
                .setInterpolator(new LinearInterpolator());
    }

    public static IdeaAnimatorSet heartBeats(@NonNull Object target, long duration, int level) {
        long dur = duration < 500 ? 500 : duration;
        level = level > 10 ? 10 : level;
        level = level <= 0 ? 1 : level;
        float beatsLevel = (float)level * 0.2f;
        return scaleCenter(target, dur, 1f, beatsLevel, 1f);
    }

    private static IdeaAnimator floatIdea(@NonNull Object target, long duration, @NonNull String property, float... values) {
        checkIsView(target);
        return baseIdea(target, duration).setPropertyName(property).setFloatValues(values);
    }

    private static IdeaAnimator baseIdea(@NonNull Object target, long duration) {
        IdeaAnimator idea = new IdeaAnimator(target).setDuration(duration);
        IdeaAnimatorManager.getInstance().add(idea);
        return idea;
    }

    private static void checkIsView(Object target) {
        boolean isView = target instanceof View;
        if (!isView) {
            Log.e(TAG, "Object is not a view, failed to execute this animator.");
        }
        Assert.assertTrue("Object is not a view, failed to execute this animator.", isView);
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