package org.qxtx.idea.animate.vector;

import android.support.annotation.NonNull;
import android.util.Log;

import org.qxtx.idea.animate.IManager;
import org.qxtx.idea.view.IdeaSvgView;

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

    public static final String SVG_HEART = "M0,0 c-1.955,0,-3.83,1.268,-4.5,3 c-0.67,-1.732,-2.547,-3,-4.5,-3 " +
            "C-11.543,0,-13.5,1.932,-13.5,4.5 c0,3.53,3.793,6.257,9,11.5 c5.207,-5.242,9,-7.97,9,-11.5 " +
            "C4.5,1.932,2.543,0,0,0z";

    public static final String SVG_PAR = "M0,0 L50,0 L50,10 L0,10 Z " +
            "M0,20 L50,20 L50,30 L0,30 Z " +
            "M0,40 L50,40 L50,50 L0,50 Z";

    public static final String SVG_ARROWS = "M5,35 L40,0 L47.072,7.072 L12.072,42.072 Z " +
            "M10,30 L60,30 L60,40 L10,40 Z " +
            "M12.072,27.928 L47.072,62.928 L40,70 L5,35 Z";

    public static void show(@NonNull IdeaSvgView target, String svgPath) {
        show(target, svgPath, false);
    }

    public static void show(@NonNull IdeaSvgView target, String svgPath, boolean isReverse) {
        target.show(svgPath, isReverse);
    }

    public static void arrows(@NonNull IdeaSvgView target) {
        target.show(SVG_PAR, true);
        target.setTag("menu");
        target.setOnClickListener(v -> {
            String tag = target.getTag().toString();
            if (!tag.endsWith("arrows")) {
                target.setTag(target.getTag() + "arrows");
                target.startAnimation(SVG_ARROWS);
            } else {
                target.setTag(tag.substring(0, tag.length() - 6));
                target.startAnimation(SVG_PAR);
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
