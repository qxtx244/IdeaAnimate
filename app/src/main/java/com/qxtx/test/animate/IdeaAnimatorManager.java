package com.qxtx.test.animate;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class IdeaAnimatorManager implements IManager<IdeaAnimator> {
    public static final String TAG = "IdeaAnimatorManager";
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
        for (IdeaAnimator idea : animatorList) {
            if (idea.getTag().equals(tag)) {
                animatorList.remove(idea);
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