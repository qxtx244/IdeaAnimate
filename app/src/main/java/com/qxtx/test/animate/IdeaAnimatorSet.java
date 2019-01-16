package com.qxtx.test.animate;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;

/**
 * @CreateDate 2019/01/16 15:06.
 * @Author QXTX-GOSPELL
 */

public class IdeaAnimatorSet {
    private AnimatorSet set;
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
        for (int i = 0; i < animatorArray.length; i++) {
            animatorArray[i] = animators[i].getAnimator();
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
}
