package org.qxtx.idea.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;

import org.qxtx.idea.animate.vector.IdeaSvgManager;

import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * @CreateDate 2019/02/14 14:26.
 * @Author QXTX-GOSPELL
 */

public class IdeaSvgView extends SvgBaseView {
    private static final String TAG = "IdeaSvgPathAnimate";

    private static final String DEFAULT_COLOR = "#1E90FF";
    private static final long DEFAULT_DURATION = 500;
    private static final float DEFAULT_STROKE_WIDTH = 3f;

    private String tag;
    private LinkedHashMap<String, float[]> startSvg;
    private LinkedHashMap<String, float[]> endSvg;
    private float[] firstPointer;
    private Path path;
    private Paint paint;
    private boolean isFillPath;
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

        if (path != null) {
            Paint.Style useStyle = isFillPath ? Paint.Style.FILL_AND_STROKE : Paint.Style.STROKE;
            paint.setStyle(useStyle);
            //需要考虑居中，修正图形的首个描点偏移
            canvas.translate(getWidth() / 2f, getHeight() / 2f);
            canvas.drawPath(path, paint);
        }
    }

    public LinkedHashMap<String, float[]> getSvg() {
        return startSvg;
    }

    public void show(@NonNull String svgPath, boolean isFillPath) {
        this.isFillPath = isFillPath;
        if (isFillPath) {
            paint.setColor(fillColor);
        } else {
            paint.setColor(lineColor);
        }

        startSvg = saveSvg(svgPath);
        path = createPath(startSvg);
        postInvalidate();
    }

    public void show(@NonNull String svgPath) {
        show(svgPath, false);
    }

    /**
     *
     * @param toSvg It must be String or LinkedHashMap<String,float[]>. String type that is about with svg path just like
     *                "M0,0 L3, 4 L5, 6z".
     *               And LinkedHashMap<String, float[]> type that is about with svg key-value just like
     *               "{"M0":{0,0}, "L1":{3,4}, "L2":{"5,6"}, "z3":{}}"
     * @param duration
     * @param delay
     */
    public void startAnimation(@NonNull Object toSvg, long duration, long delay) {
        if (toSvg instanceof String) {
            endSvg = saveSvg((String)toSvg);
        } else if (toSvg instanceof LinkedHashMap) {
            endSvg = endSvg == null ? new LinkedHashMap<>() : endSvg;
            endSvg.putAll((LinkedHashMap<String, float[]>)toSvg);
        }

        if (endSvg == null || (endSvg.size() != startSvg.size())) {
            Log.e(TAG, "Unable to start animation, different the keyword of fromPath and toPath");
            return ;
        }

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
            postInvalidateDelayed(delay);
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                startSvg.clear();
                startSvg.putAll(endSvg);
                endSvg.clear();
                setClickable(true);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                startSvg.clear();
                startSvg.putAll(endSvg);
                endSvg.clear();
                setClickable(true);
            }
        });
        valueAnimator.start();
        setClickable(false);
    }

    public void startAnimation(@NonNull Object toSvg, long duration) {
        startAnimation(toSvg, duration, 0);
    }

    public void startAnimation(@NonNull Object toSvg) {
        startAnimation(toSvg, DEFAULT_DURATION);
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

        //Save the first pointer.
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
}
