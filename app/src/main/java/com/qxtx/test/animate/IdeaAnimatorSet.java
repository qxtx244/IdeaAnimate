package com.qxtx.test.animate;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.support.annotation.Nullable;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @CreateDate 2019/01/16 15:06.
 * @Author QXTX-GOSPELL
 */

public class IdeaAnimatorSet {
    private static final String TAG = "IdeaAnimatorSet";
    private final String tag;
    private final AnimatorSet set;

    public IdeaAnimatorSet() {
        this(null);
    }

    public IdeaAnimatorSet(@Nullable AnimatorSet set) {
        this(set, null);
    }

    public IdeaAnimatorSet(@Nullable AnimatorSet set, String tag) {
        this.set = set == null ? new AnimatorSet() : set;
        this.tag = tag == null ? IdeaAnimatorSetManager.getInstance().getCount() + "" : tag;
    }

    public String getTag() {
        return tag;
    }

    public IdeaAnimator get(int index) {
        Animator animator = set.getChildAnimations().get(index);
        IdeaAnimator idea = new IdeaAnimator(animator);
        return idea;
    }

    public List<Animator> get() {
        return set.getChildAnimations();
    }

    /**
     * Start animators in the same time.
     * @param delay Delay time of start
     * @param animations Arrays of animator
     */
    public void startTogether(long delay, IdeaAnimator... animations) {
        ValueAnimator[] animatorArray = convertType(animations);
        set.playTogether(animatorArray);
        set.setStartDelay(delay);
        set.start();
    }

    /**
     * Start animators sequentially.
     * @param delay Delay time of start
     * @param animations Arrays of animator
     */
    public void startSequentially(long delay, IdeaAnimator... animations) {
        ValueAnimator[] animatorArray = convertType(animations);
        set.playSequentially(animatorArray);
        set.setStartDelay(delay);
        set.start();
    }

    private ValueAnimator[] convertType(IdeaAnimator... animators) {
        boolean checkParam = animators != null && animators.length > 1;
        if (!checkParam) {
            return null;
        }

        ValueAnimator[] animatorArray = new ValueAnimator[animators.length];
        try {
            for (int i = 0; i < animatorArray.length; i++) {
                Class<?> clazz = animators[i].getClass();
                    Field field = clazz.getDeclaredField("animator");
                    field.setAccessible(true);
                    animatorArray[i] = (ValueAnimator)field.get(animators[i]);
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return animatorArray;
    }

    public boolean isSetStarted() {
        return set.isStarted();
    }

    public boolean isSetRunning() {
        return set.isRunning();
    }

    public boolean isSetPaused() {
        return set.isPaused();
    }

    public void end() {
        set.end();
    }

    public void cancel() {
        set.cancel();
    }

    public void release() {
        set.cancel();
    }
}
