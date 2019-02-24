package org.qxtx.idea.animate.vector;

import android.graphics.Color;
import android.support.annotation.NonNull;

import org.qxtx.idea.animate.IManager;
import org.qxtx.idea.animate.IdeaUtil;
import org.qxtx.idea.animate.view.IdeaSvgView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @CreateDate 2019/02/18 11:05.
 * @Author QXTX-GOSPELL
 *
 * A class for take vector animate easily.
 *
 * @see IdeaSvgView
 * @see org.qxtx.idea.animate.IdeaUtil
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

    public static boolean checkSvgData(String svgData) {
        return IdeaSvgView.checkSvgData(svgData);
    }

    public static void setDuration(@NonNull IdeaSvgView target, long duration) {
        target.setDuration(duration);
    }

    public static void setColor(@NonNull IdeaSvgView target, int lineColor, int fillColor) {
        target.setLineColor(lineColor).setFillColor(fillColor);
    }

    public static void setStrokeWidth(@NonNull IdeaSvgView target, int width) {
        target.setStrokeWidth(width);
    }

    public static void showSvg(@NonNull IdeaSvgView target, String svgPath) {
        showSvg(target, svgPath, false);
    }
    
    public static void showSvg(@NonNull IdeaSvgView target, String svgPath, boolean isFillPath) {
        target.showSvg(svgPath, isFillPath);
    }

    public static void showSvgWithColorful(@NonNull IdeaSvgView target, @NonNull String svgData, int[] lineColors) {
        target.showSvgWithColorful(svgData, lineColors);
    }

    public static void showSvgWithColorful(@NonNull IdeaSvgView target, @NonNull String svgData) {
        target.showSvgWithColorful(svgData, new int[] {Color.RED, Color.GREEN, Color.BLUE});
    }

    public static void showWithAnim(@NonNull IdeaSvgView target, String toSvg) {
        target.showWithAnim(toSvg);
    }

    public static void showWithAnim(@NonNull IdeaSvgView target, LinkedHashMap<String, float[]> toSvg) {
        target.showWithAnim(toSvg);
    }

    /**
     * Change from menu and arrows with click. It dependent the target's target.
     */
    public static void par2arrowsAnim(@NonNull IdeaSvgView target) {
        target.showSvg(IdeaUtil.SVG_PAR, true);
        target.setTag("svg_par");
        target.setOnClickListener(v -> {
            String tag = target.getTag().toString();
            if (tag.equals("svg_par")) {
                target.showWithAnim(IdeaUtil.SVG_ARROWS);
                target.setTag("svg_arrows");
            } else {
                target.showWithAnim(IdeaUtil.SVG_PAR);
                target.setTag("svg_par");
            }
        });
    }

    public static void scaleAnim(@NonNull IdeaSvgView target, float scale) {
        target.scale(scale);
    }

    /**
     * Trim path for one dst that dstLen move in the path.
     * @param dstLen len of dst path
     */
    public static void trimDstAnim(@NonNull IdeaSvgView target, int dstLen) {
        target.startTrimAnim(dstLen);
    }

    /**
     * Trim path from one dst to fully path with animation.
     * @param isReverse True is make trim dst len change from 0 to fully path len, or reverse
     */
    public static void trimFullyAnim(@NonNull IdeaSvgView target, boolean isReverse) {
        if (isReverse) {
            target.startTrimAnim(true);
        } else {
            target.startTrimAnim(false);
        }
    }

    /**
     * Number change on 0~9 with animation.
     * @param num The num change to.
     */
    public static void numAnim(@NonNull IdeaSvgView target, int num) {
        LinkedHashMap<String, float[]> map = target.string2Map(IdeaUtil.SVG_NUMBER_8);
        int[] dstIndex = IdeaUtil.NUMBER_CHOOSE[num];
        changeNumber(dstIndex, map);
        target.showWithAnim(map);
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

    private static void changeNumber(int[] dstIndex, LinkedHashMap<String, float[]> map) {
        int index = 0;
        Iterator<String> iteratorK = map.keySet().iterator();
        while (iteratorK.hasNext()) {
            String key = iteratorK.next();
            char keyword = key.charAt(0);
            if (keyword == 'z' || keyword == 'Z') {
                index++;
            }

            boolean findDst = false;
            for (int i : dstIndex) {
                if (i == index) {
                    findDst = true;
                    break;
                }
            }
            if (findDst) {
                float[] value = map.get(key);
                System.arraycopy(new float[value.length], 0, value, 0, value.length);
            }
        }
    }
}
