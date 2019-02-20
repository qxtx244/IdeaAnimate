package org.qxtx.idea.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import org.qxtx.idea.animate.vector.IdeaSvgManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @CreateDate 2019/02/14 14:26.
 * @Author QXTX-GOSPELL
 */

public class IdeaSvgView extends View {
    private static final String TAG = "IdeaSvgPathAnimate";

    private static final String DEFAULT_COLOR = "#1E90FF";
    private static final long DEFAULT_DURATION = 500;
    private static final float DEFAULT_STROKE_WIDTH = 3f;
    private static final int MODE_SVG = 0;
    private static final int MODE_TRIM = 1;

    private String tag;
    private LinkedHashMap<String, float[]> startSvg;
    private LinkedHashMap<String, float[]> endSvg;
    float[] firstPointer;
    private int mode;
    private Path path;
    private Path[] trimPath;
    private Paint paint;
    private boolean isFillPath;
    private long duration;
    private int lineColor;
    private int fillColor;
    private float strokeWidth;

    public IdeaSvgView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public IdeaSvgView(Context context, String svgPath, int color, boolean isFillPath) {
        super(context);
        init();

        if (isFillPath) {
            fillColor = color;
        } else {
            lineColor = color;
        }

        show(svgPath, isFillPath);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //需要考虑居中，修正图形的首个描点偏移
        canvas.translate(getWidth() / 2f, getHeight() / 2f);

        if (path != null && (mode != MODE_TRIM || isFillPath)) {
            paint.setStyle(isFillPath ? Paint.Style.FILL : Paint.Style.STROKE);
            paint.setColor(isFillPath ? fillColor : lineColor);
            canvas.drawPath(path, paint);
        }

        //额外绘制1：边缘路径动画
        if (mode == MODE_TRIM && trimPath != null) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(lineColor);
            for (Path aTrimPath : trimPath) {
                try {
                    canvas.drawPath(aTrimPath, paint);
                } catch (Exception e) {
                    Log.e(TAG, "Take a unknown error but not to broken down the application, haha.");
                }
            }
        }
    }

    public LinkedHashMap<String, float[]> getSvgMap() {
        return startSvg;
    }

    public String getSvgString() {
        StringBuilder curPath = new StringBuilder();

        for (String key : startSvg.keySet()) {
            float[] values = startSvg.get(key);
            curPath.append(key.charAt(0));
            for (int i = 0; i < values.length; i++) {
                curPath.append(values[i]);
                if (i != values.length - 1) {
                    curPath.append(",");
                } else {
                    curPath.append(" ");
                }
            }
        }

        return curPath.toString();
    }

    public Path getPath() {
        return path;
    }

    public void show(@NonNull String svgPath, boolean isFillPath) {
        this.isFillPath = isFillPath;
        if (isFillPath) {
            paint.setColor(fillColor);
        } else {
            paint.setColor(lineColor);
        }

        mode = MODE_SVG;
        startSvg = saveSvg(svgPath);
        path = createPath(startSvg);
        postInvalidate();
    }

    public void show(@NonNull String svgPath) {
        show(svgPath, false);
    }

    /**
     * @param toSvg It is data string like as "M0,0 L3, 4 L5, 6z".
     */
    public void startAnimation(@NonNull String toSvg) {
        endSvg = saveSvg(toSvg);
        svgAnimation();
    }

    /**
     * @param toSvg It is data string like as "{"M0":{0,0}, "L1":{3,4}, "L2":{"5,6"}, "z3":{}}".
     */
    public void startAnimation(LinkedHashMap<String, float[]> toSvg) {
        endSvg = endSvg == null ? new LinkedHashMap<>() : endSvg;
        endSvg.putAll(toSvg);
        svgAnimation();
    }

    public void startTrimAnimation(int dstLen) {
        trimAnimation(dstLen, false);
    }

    public void startTrimAnimation(boolean isReverse) {
        trimAnimation(-1, isReverse);
    }

    public void startTrimAnimation() {
        startTrimAnimation(false);
    }

    public IdeaSvgView setDuration(long duration) {
        this.duration = duration;
        return this;
    }

    public IdeaSvgView setFillColor(int fillColor) {
        this.fillColor = fillColor;
        return this;
    }

    public IdeaSvgView setLineColor(int lineColor) {
        this.lineColor = lineColor;
        return this;
    }

    public IdeaSvgView setPaint(Paint paint) {
        this.paint = paint;
        return this;
    }

    public IdeaSvgView setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
        return this;
    }

    private Path createPath(LinkedHashMap<String, float[]> svgMap) {
        Path path = new Path();
        float[] values;
        for (String key : svgMap.keySet()) {
            char svgKey = key.charAt(0);
            switch (svgKey) {
                case 'M':
                    values = svgMap.get(key);
                    path.moveTo(values[0], values[1]);
                    break;
                case 'm':
                    values = svgMap.get(key);
                    path.rMoveTo(values[0], values[1]);
                    break;
                case 'L':
                    values = svgMap.get(key);
                    path.lineTo(values[0], values[1]);
                    break;
                case 'l':
                    values = svgMap.get(key);
                    path.rLineTo(values[0], values[1]);
                    break;
                case 'C':
                case 'S':
                    values = svgMap.get(key);
                    path.cubicTo(values[0], values[1], values[2], values[3], values[4], values[5]);
                    break;
                case 'c':
                case 's':
                    values = svgMap.get(key);
                    path.rCubicTo(values[0], values[1], values[2], values[3], values[4], values[5]);
                    break;
                case 'Q':
                case 'T':
                    values = svgMap.get(key);
                    path.quadTo(values[0], values[1], values[2], values[3]);
                    break;
                case 'q':
                case 't':
                    values = svgMap.get(key);
                    path.rQuadTo(values[0], values[1], values[2], values[3]);
                    break;
                case 'Z':
                case 'z':
                    path.close();
                    break;
                case 'A':
//                    path.arcTo();
                    break;
                case 'H':
                case 'V':
                    values = svgMap.get(key);
                    path.lineTo(values[0], values[1]);
                    break;
                case 'h':
                case 'v':
                    values = svgMap.get(key);
                    path.rLineTo(values[0], values[1]);
                    break;
            }
        }

        return path;
    }

    private int doSave(int arraySize, String svgData, int startIndex, LinkedHashMap<String, float[]> map) {
        int endIndex;
        char svgkey = svgData.charAt(startIndex - 1);
        String key = svgkey + "" + map.size();

        //Path end or something skip.
        if (arraySize == 0) {
            map.put(key, new float[0]);
            return startIndex;
        } else if (arraySize == -1) {
            return startIndex;
        }

        float[] values = new float[arraySize];
        endIndex = parseValues(svgData, values, startIndex);

        if (map.size() == 0) {
            firstPointer = new float[2];
            System.arraycopy(values, 0, firstPointer, 0, values.length);
        }

        //Move pointer to view start pos.
        if (Character.isUpperCase(svgkey)) {
            for (int i = 0; i < values.length; i++) {
                values[i] -= firstPointer[i % 2];
            }
        }

        map.put(key, values);
        return endIndex;
    }

    private void init() {
        duration = DEFAULT_DURATION;
        strokeWidth = DEFAULT_STROKE_WIDTH;
        lineColor = Color.parseColor(DEFAULT_COLOR);
        fillColor = Color.parseColor(DEFAULT_COLOR);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(lineColor);
        paint.setStrokeWidth(strokeWidth);

        IdeaSvgManager manager = IdeaSvgManager.getInstance();
        this.tag = tag == null ? manager.getCount() + "" : tag;
        manager.add(this);
    }

    private int parseValues(String data, float[] values, int startIndex) {
        int endIndex = 0;
        for (int i = 0; i < values.length; i++) {
            String regex = (i != values.length - 1) ? "," : " ";
            endIndex = data.indexOf(regex, startIndex);
            values[i] = Float.parseFloat(data.substring(startIndex, endIndex).replace(" ", ""));
            startIndex = endIndex + 1;
        }
        return endIndex;
    }

    private LinkedHashMap<String, float[]> saveSvg(String svgData) {
        //也许使用正则负担也不大
        svgData = svgData
                .trim()
                .replace("z", " z")
                .replace("Z", " Z")
                .replace("  ", " ");

        LinkedHashMap<String, float[]> map = new LinkedHashMap<>();
        int endIndex;
        for (int i = 0; i < svgData.length(); i = endIndex + 1) {
            char svgKey = svgData.charAt(i);
            int arraySize;
            switch (svgKey) {
                case 'M':
                case 'm':
                    arraySize = 2;
                    break;
                case 'L':
                case 'l':
                    arraySize = 2;
                    break;
                case 'C':
                case 'c':
                case 'S':
                case 's':
                    arraySize = 6;
                    break;
                case 'Q':
                case 'q':
                case 'T':
                case 't':
                    arraySize = 4;
                    break;
                case 'Z':
                case 'z':
                    arraySize = 0;
                    break;
                case 'A':
                case 'H':
                case 'h':
                case 'V':
                case 'v':
                    arraySize = 1;
                    break;
                default:
                    arraySize = -1;
                    break;
            }
            endIndex = doSave(arraySize, svgData, i + 1, map);
        }

        return map;
    }

    private void svgAnimation() {
        if (endSvg == null || (endSvg.size() != startSvg.size())) {
            Log.e(TAG, "Unable to start animation, different the keyword of fromPath and toPath");
            return ;
        }

        mode = MODE_SVG;
        LinkedHashMap<String, float[]> newSvg = new LinkedHashMap<>();
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f, 1f).setDuration(duration);
        valueAnimator.addUpdateListener(animation -> {
            float fraction = animation.getAnimatedFraction();
            newSvg.clear();
            Iterator<String> key = startSvg.keySet().iterator();
            Iterator<float[]> fromValue = startSvg.values().iterator();
            Iterator<float[]> toValue = endSvg.values().iterator();
            while (key.hasNext()) {
                float[] from = fromValue.next();
                float[] to = toValue.next();
                float[] newValue = new float[from.length];
                for (int i = 0; i < newValue.length; i++) {
                    newValue[i] = from[i] + (to[i] - from[i]) * fraction;
                }
                newSvg.put(key.next(), newValue);
            }

            path = createPath(newSvg);
            postInvalidate();
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                onEnd();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                onEnd();
            }

            private void onEnd() {
                startSvg.clear();
                startSvg.putAll(endSvg);
                endSvg.clear();
                setClickable(true);
            }
        });
        valueAnimator.start();
        setClickable(false);
    }

    /**
     * The svg maybe contains multi close-path, so we have to check it to take animation fully.
     * @param dstLen
     * @param isReverse
     */
    private void trimAnimation(int dstLen, boolean isReverse) {
        mode = MODE_TRIM;

        //The svg may be the multi close-path.
        Path[] paths = splitPath();
        trimPath = new Path[paths.length];
        PathMeasure pathMeasure = new PathMeasure();
        ValueAnimator animators = ValueAnimator.ofFloat(0f, 1f).setDuration(duration);
        animators.addUpdateListener(animator -> {
            for (int i = 0; i < paths.length; i++) {
                Path dst = new Path();
                /* Order to official document, it can be make animation workaround even sdk version number
                  below to 19 and display on the hardware-accelerated canvas. */
                dst.rLineTo(0f, 0f);

                pathMeasure.setPath(paths[i], false);
                float fraction = animator.getAnimatedFraction();
                float pathLen = pathMeasure.getLength();
                float startD, endD;
                if (dstLen <= 0) { //fully trim
                    startD = 0f;
                    endD = (isReverse ? (1f - fraction) : fraction) * pathLen;
                } else {
                    startD = pathLen * fraction;
                    endD = startD + dstLen;
                }

                if ((startD == 0f && endD == 0f)
                        || pathMeasure.getSegment(startD, endD, dst, true)) {
                    trimPath[i] = dst;
                }
            }
            postInvalidate();
        });
        animators.start();
    }

    private Path[] splitPath() {
        List<String> subPath = new ArrayList<>();

        String svgPath = getSvgString();
        int startPos = 0;
        for (int i = 0; i < svgPath.length(); i++) {
            if (svgPath.charAt(i) == 'z' || svgPath.charAt(i) == 'Z') {
                subPath.add(svgPath.substring(startPos, i + 1));
                startPos = i + 1;
            }
        }

        Path[] paths = new Path[subPath.size()];
        for (int i = 0; i < subPath.size(); i++) {
            String data = subPath.get(i);
            paths[i] = createPath(saveSvg(data));
        }

        return paths;
    }
}
