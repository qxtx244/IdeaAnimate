package com.qxtx.idea.animate.animator;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.animation.Interpolator;

import com.qxtx.idea.animate.IdeaUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @CreateDate 2019/01/16 15:06.
 * @Author QXTX-GOSPELL
 *
 * A class for take animatorSet.
 *
 * @see IdeaAnimatorSetManager
 * @see IdeaUtil
 */

public class IdeaAnimatorSet {
    private static final String TAG = "IdeaAnimatorSet";
    private final String tag;
    private final Object target;
    private final AnimatorSet set;
    private int repeatCount;
    private int repeatMode;

    public IdeaAnimatorSet() {
        this(null, null);
    }

    public IdeaAnimatorSet(@Nullable AnimatorSet set) {
        this(set, null);
    }

    public IdeaAnimatorSet(@NonNull Object target) {
        this(target, null);
    }

    public IdeaAnimatorSet(@NonNull Object target,  String tag) {
        this.set = new AnimatorSet();
        this.tag = tag == null ? IdeaAnimatorSetManager.getInstance().getCount() + "" : tag;
        this.target = target;
        this.set.setTarget(target);
        this.repeatCount = -1;
        this.repeatMode = -1;
        IdeaAnimatorSetManager.getInstance().add(this);
    }

    public IdeaAnimatorSet(@Nullable AnimatorSet set, String tag) {
        this.set = set == null ? new AnimatorSet() : set;
        this.tag = tag == null ? IdeaAnimatorSetManager.getInstance().getCount() + "" : tag;
        this.target = null;
        this.repeatCount = -1;
        this.repeatMode = -1;
        IdeaAnimatorSetManager.getInstance().add(this);
    }

    public String getTag() {
        return tag;
    }

    public IdeaAnimator getChild(int index) {
        Animator animator = set.getChildAnimations().get(index);
        return new IdeaAnimator(animator);
    }

    public List<Animator> getAllChild() {
        return set.getChildAnimations();
    }

    public IdeaAnimatorSet addListener(Animator.AnimatorListener listener) {
        set.addListener(listener);
        return this;
    }

    public IdeaAnimatorSet addUpdateListener(Animator.AnimatorPauseListener listener) {
        set.addPauseListener(listener);
        return this;
    }

    public int getRepeatCount() {
        return repeatCount;
    }

    public int getRepeatMode() {
        return repeatMode;
    }

    /**
     * Start animators in the same time.
     * @param animations Arrays of animator
     */
    public IdeaAnimatorSet playTogether(IdeaAnimator... animations) {
        ValueAnimator[] animatorArray = convertType(animations);
        set.playTogether(animatorArray);
        return this;
    }

    /**
     * Start animators sequentially.
     * @param animations Arrays of animator
     */
    public IdeaAnimatorSet playSequentially(IdeaAnimator... animations) {
        ValueAnimator[] animatorArray = convertType(animations);
        set.playSequentially(animatorArray);
        return this;
    }

    public IdeaAnimatorSet setCurrentPlayTime(long playTime) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            set.setCurrentPlayTime(playTime);
        } else {
            Log.e(TAG, "AnimatorSet.setCurrentPlayTime(long) didn't work in sdk version number below 26.");
        }
        return this;
    }

    public IdeaAnimatorSet setDuration(long duration) {
        set.setDuration(duration);
        return this;
    }

    public IdeaAnimatorSet setInterpolator(Interpolator interpolator) {
        set.setInterpolator(interpolator);
        return this;
    }

    public IdeaAnimatorSet setStartDelay(long delay) {
        set.setStartDelay(delay);
        return this;
    }

    public void setupEndValues() {
        set.setupEndValues();
    }

    public IdeaAnimatorSet setRepeat(int repeatCount) {
        return setRepeat(repeatCount, IdeaUtil.MODE_RESTART);
    }

    public IdeaAnimatorSet setRepeat(int repeatCount, int repeatMode) {
        ArrayList<Animator> animators = set.getChildAnimations();
        for (Animator i : animators) {
            ((ValueAnimator)i).setRepeatCount(repeatCount);
            ((ValueAnimator)i).setRepeatMode(repeatMode);
        }
        this.repeatCount = repeatCount;
        this.repeatMode = repeatMode;
        return this;
    }

    public void setupStartValues() {
        set.setupStartValues();
    }

    public void pause() {
        set.pause();
    }

    public void reverse() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            set.reverse();
        } else {
            Log.e(TAG, "IdeaAnimatorSet#reverse() didn't work at sdk version number below 26");
        }
    }

    public void resume() {
        set.resume();
    }

    public void start() {
        set.start();
    }

    public IdeaAnimatorSet startCycle(Handler mainHandler, int count) {
        //Do nothing now.
        start();
        return this;
    }

    public IdeaAnimatorSet newCopy() {
        return new IdeaAnimatorSet(set.clone());
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
