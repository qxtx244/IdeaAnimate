package com.qxtx.idea.svg;

import android.graphics.Paint;
import android.support.annotation.NonNull;

import junit.framework.Assert;

import com.qxtx.idea.IManager;
import com.qxtx.idea.IdeaUtil;
import com.qxtx.idea.view.IdeaSvgView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * CreatedDate   2019/02/18 11:05.
 * Author  QXTX-GOSPELL
 *
 * A class for take vector animate easily.
 *
 * @see IdeaSvgView
 * @see IdeaUtil
 */

public class IdeaSvgManager implements IManager<IdeaSvgView> {
    public static final String TAG = "IdeaSvgManager";
    private static final long DEFAULT_DURATION = 500;
    private static final long DEFAULT_DELAY = 1000;
    private static final String DEFAULT_COLOR = "#1E90FF";
    private static final float DEFAULT_STROKE_WIDTH = 3f;
    private static IdeaSvgManager manager;
    private List<IdeaSvgView> list;

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

    public static void setLineColor(@NonNull IdeaSvgView target, int... lineColors) {
        target.setLineColor(lineColors);
    }

    public static void setFillColor(@NonNull IdeaSvgView target, int... fillColors) {
        target.setLineColor(fillColors);
    }

    public static void setStrokeWidth(@NonNull IdeaSvgView target, int width) {
        target.setStrokeWidth(width);
    }

    public static void showSvg(@NonNull IdeaSvgView target, String svgPath) {
        showSvg(target, svgPath, IdeaUtil.PAINT_LINE);
    }
    
    public static void showSvg(@NonNull IdeaSvgView target, String svgPath, Paint.Style drawStyle) {
        target.showSvg(svgPath, drawStyle);
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
        target.showSvg(IdeaUtil.SVG_PAR, IdeaUtil.PAINT_FILL);
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
    public static void numAnim(@NonNull IdeaSvgView target, @IdeaUtil.SvgNumber int num) {
        Object tag = target.getTag();
        if (tag == null) {
            target.showSvg(IdeaUtil.SVG_NUMBER_8);
        } else {
            int numTag = Integer.parseInt((String) target.getTag());
            if (numTag < 0 || numTag > 9) {
                target.showSvg(IdeaUtil.SVG_NUMBER_8);
            }
        }

        target.setTag(num + "");

        LinkedHashMap<String, float[]> map = target.string2Map(IdeaUtil.SVG_NUMBER_8);
        int[] dstIndex = IdeaUtil.NUMBER_CHOOSE[num];
        changeNumber(target, dstIndex, map);
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

    private static void changeNumber(IdeaSvgView target, int[] dstIndex, LinkedHashMap<String, float[]> map) {
        int index = -1;
        Iterator<String> iteratorK = map.keySet().iterator();
        float[] firstValue = null;
        while (iteratorK.hasNext()) {
            String key = iteratorK.next();
            char keyword = key.charAt(0);
            if (keyword == 'm' || keyword == 'M') {
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
                if (keyword == 'm' || keyword == 'M') {
                    firstValue = Arrays.copyOf(value, value.length);
                }

                Assert.assertNotNull("Error! Start pointer was not found!", firstValue); //firstValue must be not null in this.
                for (int i = 0; i < value.length; i++) {
                    value[i] = firstValue[i % 2];
                }
//                System.arraycopy(new float[value.length], 0, value, 0, value.length);
            }
        }
    }
}
