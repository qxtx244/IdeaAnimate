package com.qxtx.test.animate;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @CreateDate 2019/01/17 10:52.
 * @Author QXTX-GOSPELL
 */

public class IdeaAnimatorSetManager implements IManager<IdeaAnimatorSet> {
    private static final String TAG = "IdeaAnimatorSetManager";
    private static IdeaAnimatorSetManager manager;
    private List<IdeaAnimatorSet> setList;

    private IdeaAnimatorSetManager() {
        setList = new ArrayList<>();
    }

    public static IdeaAnimatorSetManager getInstance() {
        if (manager == null) {
            synchronized (IdeaAnimatorSetManager.class) {
                if (manager == null) {
                    manager = new IdeaAnimatorSetManager();
                }
            }
        }
        return manager;
    }

    public static IdeaAnimatorSet together(IdeaAnimator... ideas) {
        return baseSet().playTogether(ideas);
    }

    public static IdeaAnimatorSet sequentially(IdeaAnimator... ideas) {
        return baseSet().playSequentially(ideas);
    }

    private static IdeaAnimatorSet baseSet() {
        IdeaAnimatorSet ideaSet = new IdeaAnimatorSet();
        IdeaAnimatorSetManager.getInstance().setList.add(ideaSet);
        return ideaSet;
    }

    @Override
    public void add(IdeaAnimatorSet animator) {
        setList.add(animator);
    }

    @Override
    public void remove(IdeaAnimatorSet animator) {
        setList.remove(animator);
    }

    @Override
    public void remove(@NonNull String tag) {
        for (int i = 0; i < setList.size(); i++) {
            if (setList.get(i).getTag().equals(tag)) {
                setList.remove(i);
                return ;
            }
        }
    }

    @Override
    public void remove(int index) {
        setList.remove(index);
    }

    @Override
    public int getCount() {
        return setList.size();
    }

    @Override
    public List<IdeaAnimatorSet> getAnimateList() {
        return setList;
    }

    @Override
    public List<IdeaAnimatorSet> get(@NonNull String tag) {
        List<IdeaAnimatorSet> ideaSets = new ArrayList<>();
        for (IdeaAnimatorSet ideaSet : setList) {
            if(ideaSet.getTag().equals(tag)) {
                ideaSets.add(ideaSet);
            }
        }
        return ideaSets;
    }

    @Override
    public void release() {
        for (int i = 0; i < setList.size(); i++) {
            setList.get(i).release();
        }
        setList.clear();
        setList = null;
        manager = null;
    }
}
