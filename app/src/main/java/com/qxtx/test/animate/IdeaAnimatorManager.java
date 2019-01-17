package com.qxtx.test.animate;

import android.graphics.Path;
import android.support.annotation.NonNull;

import junit.framework.Assert;

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

    /**
     * ！！！总是返回第一个匹配tag的animate，因此存在多个相同的tag的时候，可能会无法得到想要的结果。
     */
    @Override
    public IdeaAnimator get(@NonNull String tag) {
        for (IdeaAnimator idea : animatorList) {
            if (idea.getTag().equals(tag)) {
                return idea;
            }
        }
        return null;
    }

    public static IdeaAnimator animatorPath(@NonNull Object target, Path path, long duration) {
        return baseIdea(target, duration).setPath(path);
    }

    public static IdeaAnimator linearPath(@NonNull Object target,
                                          float fromX, float toX, float fromY, float toY,
                                          long duration) {
        Path path = new Path();
        path.moveTo(fromX, fromY);
        path.lineTo(toX, toY);
        return baseIdea(target, duration).setPath(path);
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
        IdeaAnimatorSet ideaSet = new IdeaAnimatorSet();
        ideaSet.startTogether(0,
                floatIdea(target, duration, "scaleX", values)
                , floatIdea(target, duration, "scaleY", values));
        IdeaAnimatorSetManager.getInstance().add(ideaSet);
        return ideaSet;
    }

    public static IdeaAnimator scaleX(@NonNull Object target, long duration, float... values) {
        return floatIdea(target, duration, "scaleX", values);
    }

    public static IdeaAnimator scaleY(@NonNull Object target, long duration, float... values) {
        return floatIdea(target, duration, "scaleY", values);
    }

    private static IdeaAnimator floatIdea(@NonNull Object target, long duration, @NonNull String property, float... values) {
        return baseIdea(target, duration)
                .setPropertyName(property)
                .setFloatValues(values);
    }

    private static IdeaAnimator baseIdea(@NonNull Object target, long duration) {
        IdeaAnimator idea = new IdeaAnimator(target).setDuration(duration);
        IdeaAnimatorManager.getInstance().add(idea);
        return idea;
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