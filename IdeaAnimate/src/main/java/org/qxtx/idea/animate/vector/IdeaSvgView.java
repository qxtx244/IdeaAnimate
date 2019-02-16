package org.qxtx.idea.animate.vector;

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
import android.view.View;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * @CreateDate 2019/02/14 14:26.
 * @Author QXTX-GOSPELL
 */

public class IdeaSvgView extends View {
    private static final String TAG = "IdeaSvgPathAnimate";

    private static final String DEFAULT_COLOR = "#1E90FF";
    private static final long DEFAULT_DURATION = 800;
    private LinkedHashMap<String, float[]> startSvg;
    private LinkedHashMap<String, float[]> endSvg;
    private float[] firstPointer;
    private Path path;
    private Paint paint;
    private int color;
    private boolean isFillPath;
    private long duration;

    public IdeaSvgView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public IdeaSvgView(Context context, String svgPath, int color, boolean isFillPath) {
        super(context);
        init();
        init(svgPath, color, isFillPath);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (path != null) {
            Paint.Style useStyle = isFillPath ? Paint.Style.FILL_AND_STROKE : Paint.Style.STROKE;
            paint.setStyle(useStyle);
            //需要考虑居中，修正图形的首个描点偏移
            canvas.translate(getWidth() / 2f - firstPointer[0], getHeight() / 2f - firstPointer[1]);
            canvas.drawPath(path, paint);
        }
    }

    private void init() {
        paint = new Paint();
        paint.setStrokeWidth(5f);
        paint.setColor(Color.parseColor(DEFAULT_COLOR));
        paint.setAntiAlias(true);
    }

    public void init(@NonNull String fromData, int color, boolean isFillPath) {
        this.color = color;
        this.isFillPath = isFillPath;
        paint.setColor(color);
        startSvg = saveSvg(fromData);
        path = createPath(startSvg);
        postInvalidate();
    }

    public void init(String svgPath, boolean isFillPath) {
        init(svgPath, Color.parseColor(DEFAULT_COLOR), isFillPath);
    }

    /**
     * Start to make animate to svg convert.
     * @param toSvg
     */
    public void startSvgAnimate(String toSvg) {
        endSvg = new LinkedHashMap<>();
        endSvg = saveSvg(toSvg);
        //make new SVG
        LinkedHashMap<String, float[]> newSvg = new LinkedHashMap<>();

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f, 1f).setDuration(1000);
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
        valueAnimator.start();
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
            switch (svgKey) {
                case 'M':
                case 'm':
                    endIndex = doSave(2, svgData, i + 1, map);
                    break;
                case 'C':
                case 'c':
                case 'S':
                case 's':
                    endIndex = doSave(6, svgData, i + 1, map);
                    break;
                case 'Q':
                case 'q':
                case 'T':
                case 't':
                    endIndex = doSave(4, svgData, i + 1, map);
                    break;
                case 'Z':
                case 'z':
                    endIndex = i + 1;
                    map.put(svgKey + "" + map.size(), new float[0]);
                    break;
                case 'A':
                case 'H':
                case 'h':
                case 'V':
                case 'v':
                default:
                    endIndex = doSave(1, svgData, i + 1, map);
                    break;
            }
        }

        return map;
    }

    private Path createPath(LinkedHashMap<String, float[]> svgMap) {
        char lastKey;
        firstPointer = new float[2];
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
            lastKey = svgKey;
        }

        return path;
    }

    private int doSave(int arraySize, String svgData, int startIndex, LinkedHashMap<String, float[]> map) {
        String type = svgData.charAt(startIndex - 1) + "" + map.size();
        float[] values = new float[arraySize];
        int endIndex = parseValues(svgData, values, startIndex);
        map.put(type, values);

        if (map.size() == 1) {
            firstPointer = new float[values.length];
            System.arraycopy(values, 0, firstPointer, 0, values.length);
        }

        return endIndex;
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
}
