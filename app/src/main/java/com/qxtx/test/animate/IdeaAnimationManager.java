package com.qxtx.test.animate;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @CreateDate 2019/01/16 16:08.
 * @Author QXTX-GOSPELL
 */
public class IdeaAnimationManager implements IManager<IdeaAnimation> {
    public static final String TAG = "IdeaAnimationManager";
    private static IdeaAnimationManager manager;
    private List<IdeaAnimation> animationList;

    private IdeaAnimationManager() {
        animationList = new ArrayList<>();
    }

    /**
     * It must be call before create a new animate.
     * @return {@link IdeaAnimationManager} The object of this class
     */
    public static IdeaAnimationManager getInstance() {
        if (manager == null) {
            synchronized (IdeaAnimationManager.class) {
                if (manager == null) {
                    manager = new IdeaAnimationManager();
                }
            }
        }
        return manager;
    }

    @Override
    public void add(IdeaAnimation animator) {
        animationList.add(animator);
    }

    @Override
    public void remove(IdeaAnimation animator) {
        animationList.remove(animator);
    }

    @Override
    public void remove(@NonNull String tag) {
        for (IdeaAnimation idea : animationList) {
            if (idea.getTag().equals(tag)) {
                animationList.remove(idea);
            }
        }
    }

    @Override
    public void remove(int index) {
        animationList.remove(index);
    }

    @Override
    public int getCount() {
        return animationList.size();
    }

    @Override
    public List<IdeaAnimation> getAnimateList() {
        return animationList;
    }

    /**
     * ！！！总是返回第一个匹配tag的animate，因此存在多个相同的tag的时候，可能会无法得到想要的结果。
     */
    @Override
    public IdeaAnimation get(@NonNull String tag) {
        for (IdeaAnimation idea : animationList) {
            if (idea.getTag().equals(tag)) {
                return idea;
            }
        }
        return null;
    }

    @Override
    public void release() {
        if (animationList != null) {
            for (IdeaAnimation idea : animationList) {
                idea.release();
            }
            animationList.clear();
            animationList = null;
        }

        manager = null;
    }
}
