package org.qxtx.idea.animate.animator;

import android.support.annotation.NonNull;

import org.qxtx.idea.animate.IManager;

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

    public static IdeaAnimatorSet repeat(int repeat) {
        return null;
    }

    private static IdeaAnimatorSet baseSet() {
        return new IdeaAnimatorSet();
    }

    @Override
    public void add(IdeaAnimatorSet ideaSet) {
        setList.add(ideaSet);
    }

    @Override
    public void remove(IdeaAnimatorSet ideaSet) {
        setList.remove(ideaSet);
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
