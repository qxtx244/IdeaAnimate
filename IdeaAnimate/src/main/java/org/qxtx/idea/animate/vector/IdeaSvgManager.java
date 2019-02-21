package org.qxtx.idea.animate.vector;

import android.animation.ValueAnimator;
import android.renderscript.Sampler;
import android.support.annotation.NonNull;
import android.util.Log;

import org.qxtx.idea.animate.IManager;
import org.qxtx.idea.animate.IdeaUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @CreateDate 2019/02/18 11:05.
 * @Author QXTX-GOSPELL
 */

public class IdeaSvgManager implements IManager<IdeaSvgView> {
    public static final String TAG = "IdeaSvgManager";
    private static final long DEFAULT_DURATION = 500;
    private static final long DEFAULT_DELAY = 1000;
    private static final String DEFAULT_COLOR = "#1E90FF";
    private static final float DEFAULT_STROKE_WIDTH = 3f;
    public static IdeaSvgManager manager;
    public List<IdeaSvgView> list;

    private IdeaSvgManager() {
        list = new ArrayList<>();
    }

    public static IdeaSvgManager getInstance() {
        if (manager == null) {
            synchronized (IdeaSvgManager.class) {
                if (manager == null) {
                    manager = new IdeaSvgManager();
                }
            }
        }
        return manager;
    }

    public static void show(@NonNull IdeaSvgView target, String svgPath) {
        show(target, svgPath, false);
    }

    public static void show(@NonNull IdeaSvgView target, String svgPath, boolean isReverse) {
        target.show(svgPath, isReverse);
    }

    public static void arrows(@NonNull IdeaSvgView target) {
        target.show(IdeaUtil.SVG_PAR, true);
        target.setTag("menu");
        target.setOnClickListener(v -> {
            String tag = target.getTag().toString();
            if (!tag.endsWith("arrows")) {
                target.setTag(target.getTag() + "arrows");
                target.startAnimation(IdeaUtil.SVG_ARROWS);
            } else {
                target.setTag(tag.substring(0, tag.length() - 6));
                target.startAnimation(IdeaUtil.SVG_PAR);
            }
        });
    }

    public static void scale(@NonNull IdeaSvgView target, float scale) {
        if (scale < 0f) {
            Log.e(TAG, "Value of scale must be postive.");
            return ;
        }

        LinkedHashMap<String, float[]> svg = target.getSvgMap();
        LinkedHashMap<String, float[]> newSvg = new LinkedHashMap<>();
        Iterator<float[]> iteratorV = svg.values().iterator();
        for (String key : svg.keySet()) {
            float[] values = iteratorV.next();
            float[] newValues = new float[values.length];
            for (int i = 0; i < values.length; i++) {
                newValues[i] = values[i] * scale;
            }
            newSvg.put(key, newValues);
        }

        target.startAnimation(newSvg);
    }

    public static void trimDst(@NonNull IdeaSvgView target, int dstLen) {
        target.startTrimAnimation(dstLen);
    }

    public static void trimFully(@NonNull IdeaSvgView target, boolean isReverse) {
        if (isReverse) {
            target.startTrimAnimation(true);
        } else {
            target.startTrimAnimation(false);
        }
    }

    public static void zero2Nine(@NonNull IdeaSvgView target, int num) {
        zero2Nine(target, num, true);
    }

    /**
     * 缩放某一条闭合路径直至0，即可让它消失
     * @param target
     * @param num
     * @param useAnimate
     */
    public static void zero2Nine(@NonNull IdeaSvgView target, int num, boolean useAnimate) {
//        target.setTag("numberMode" + num + target.getTag());
        switch (num) {
            case 0:
                break;
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                break;
            case 6:
                break;
            case 7:
                break;
            case 8:
                break;
            case 9:
                break;
        }
    }

    @Override
    public void add(IdeaSvgView idea) {
        list.add(idea);
    }

    @Override
    public void remove(IdeaSvgView idea) {
        list.remove(idea);
    }

    @Override
    public void remove(@NonNull String tag) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getTag().equals(tag)) {
                list.remove(i);
                i--;
            }
        }
    }

    @Override
    public void remove(int index) {
        list.remove(index);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public List<IdeaSvgView> getAnimateList() {
        return list;
    }

    @Override
    public List<IdeaSvgView> get(@NonNull String tag) {
        List<IdeaSvgView> ideas = new ArrayList<>();
        for (IdeaSvgView i : ideas) {
            if (i.getTag().equals(tag)) {
                ideas.add(i);
            }
        }
        return ideas;
    }

    @Override
    public void release() {
        if (list != null) {
            list.clear();
        }
        list = null;
        manager = null;
    }
}
