package org.qxtx.idea.animate.vector;

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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @CreateDate 2019/02/14 14:26.
 * @Author QXTX-GOSPELL
 *
 * IdeaSvgView在同一时间仅支持一个动画的播放，如果多个动画在同一时间段被要求播放，则只播放第一个动画，其他动画不被执行；
 * 例外的是，裁剪动画可与svg动画同时执行
 *
 * 支持的keyword（包括小写）：M L Q C H V A S T Z
 *
 * String格式路径要求：
 *   1、字符串以【m/M】开始；
 *   2、两条路径之间必须至少有一个【空格】隔开，数值之间以【英文半角“,”】隔开；
 *   3、必须以【z/Z】结尾；
 *   4、支持多条闭合路径组成的svg图形；
 *   5、允许任意添加空格。
 *
 * LinkedHashMap<String,float[]>格式路径要求：
 *   1、每个键值对为一条路径的完整数据，key为【keyword(+其它数据)】的字符串，value为【float[]数组】；
 *   2、以key为【m/M(+其它数据)】的键值对开始；
 *   3、最后一个键值对的key必须为【z/Z(+其它数据)】，此键值对的value不能为【null】。
 *
 *   备注：view的tag用来标记view此时的图形状态
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
    private ValueAnimator animator;

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

        //Center the svg(but it look like work bad)
        canvas.translate(getWidth() / 2f, getHeight() / 2f);

        if (path != null && (mode != MODE_TRIM || isFillPath)) {
            paint.setStyle(isFillPath ? Paint.Style.FILL : Paint.Style.STROKE);
            paint.setColor(isFillPath ? fillColor : lineColor);
            canvas.drawPath(path, paint);
        }

        //Extra1：trim dst
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

    /**
     * pullAll() only can be make a deep-copy with base-value but object.
     * @return This LinkedHashMap was a deep-copy from {@link #startSvg} for protect this class member
     */
    public LinkedHashMap<String, float[]> getSvgMap() {
        return deepCopyMap(startSvg);
    }

    public String getSvgString() {
        return Map2String(startSvg);
    }

    public Path getPath() {
        return path;
    }

    public void show(@NonNull String svgPath, boolean isFillPath) {
        if (animator != null) {
            Log.e(TAG, "Need to show a svg but animation is running, so stop the animation!");
            animator.cancel();
            animator = null;
        }

        endSvg = null;
        firstPointer = null;
        mode = MODE_SVG;

        this.isFillPath = isFillPath;
        if (isFillPath) {
            paint.setColor(fillColor);
        } else {
            paint.setColor(lineColor);
        }

        startSvg = String2Map(svgPath);
        path = createPath(startSvg);
        postInvalidate();
    }

    public void show(@NonNull String svgPath) {
        show(svgPath, false);
    }

    /**
     * @param toSvg It is data string like as "M0,0 L3,4 L5,6 z".
     */
    public void startAnimation(@NonNull String toSvg) {
        if (animator != null) {
            Log.e(TAG, "IdeaSvgView only can run one animate in the same time! wait for current animation?");
            return ;
        }

        endSvg = String2Map(toSvg);
        svgAnimation();
    }

    /**
     * @param toSvg It is data string like as "{"M0":{0,0}, "L1":{3,4}, "L2":{"5,6"}, "z3":{}}".
     */
    public void startAnimation(LinkedHashMap<String, float[]> toSvg) {
        if (animator != null) {
            Log.e(TAG, "IdeaSvgView only can run one animate in the same time! wait for current animation?");
            return ;
        }

        endSvg = endSvg == null ? new LinkedHashMap<>() : endSvg;
        endSvg.clear();
        endSvg = deepCopyMap(toSvg);
//        endSvg.putAll(toSvg); //Should to take a deep-copy

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
            values = svgMap.get(key);
            char svgKey = key.charAt(0);
            switch (svgKey) {
                case 'M':
                    path.moveTo(values[0], values[1]);
                    break;
                case 'm':
                    path.rMoveTo(values[0], values[1]);
                    break;
                case 'L':
                    path.lineTo(values[0], values[1]);
                    break;
                case 'l':
                    path.rLineTo(values[0], values[1]);
                    break;
                case 'C':
                case 'S':
                    path.cubicTo(values[0], values[1], values[2], values[3], values[4], values[5]);
                    break;
                case 'c':
                case 's':
                    path.rCubicTo(values[0], values[1], values[2], values[3], values[4], values[5]);
                    break;
                case 'Q':
                case 'T':
                    path.quadTo(values[0], values[1], values[2], values[3]);
                    break;
                case 'q':
                case 't':
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
                    path.lineTo(values[0], 0f);
                    break;
                case 'h':
                    path.rLineTo(values[0], 0f);
                    break;
                case 'V':
                    path.lineTo(0f, values[1]);
                    break;
                case 'v':
                    path.rLineTo(0f, values[1]);
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

        if (map.size() == 0 && firstPointer == null) {
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

    public LinkedHashMap<String, float[]>  String2Map(String svgData) {
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
    
    public String Map2String(LinkedHashMap<String, float[]> svgData) {
        StringBuilder curPath = new StringBuilder();

        for (String key : svgData.keySet()) {
            float[] values = svgData.get(key);
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

    private void svgAnimation() {
        if (endSvg == null || (endSvg.size() != startSvg.size())) {
            Log.e(TAG, "Unable to start animation, different the keyword of fromPath and toPath");
            return ;
        }

        mode = MODE_SVG;
        LinkedHashMap<String, float[]> newSvg = new LinkedHashMap<>();
        animator = ValueAnimator.ofFloat(0f, 1f).setDuration(duration);
        animator.addUpdateListener(animation -> {
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
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                onEnd();
            }
            @Override
            public void onAnimationCancel(Animator animation) {
                onEnd();
            }

            private void onEnd() {
                startSvg.clear();
                startSvg = deepCopyMap(endSvg);
//                startSvg.putAll(endSvg); //Should to make a deep-copy
                endSvg.clear();
                animator = null;
            }
        });
        animator.start();
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

        //Svg map path -> String[] path，每一条闭合的路径
        String svgPath = Map2String(startSvg);
        int startPos = 0;
        for (int i = 0; i < svgPath.length(); i++) {
            if (svgPath.charAt(i) == 'z' || svgPath.charAt(i) == 'Z') {
                String dstPath = svgPath.substring(startPos, i + 1);
                subPath.add(dstPath);
                startPos = i + 1;
            }
        }

        //String路径 -> HashMap路径
        //由于之前已经调整好描点位置，因此不需要再考虑起始描点的问题
        firstPointer = new float[2];
        List<LinkedHashMap<String, float[]>> maps = new ArrayList<>();
        for (int i = 0; i < subPath.size(); i++) {
            String data = subPath.get(i);
            maps.add(String2Map(data));
        }

        /*
         * 需要将起始描点类型从【m】转换为【M】
         * 如果起始为‘m’，则替换此键的值为【上一个坐标值】 + 【下一个坐标值】，
         */
        for (int i = 0; i < maps.size(); i++) {
            LinkedHashMap<String, float[]> curMap = maps.get(i);
            float[] firstValue = (float[])curMap.values().toArray()[0];
            String firstK = (String)curMap.keySet().toArray()[0];
            if (firstK.contains("m")) {
                float[] lastValue = (float[])maps.get(i - 1).values().toArray()[0];
                curMap.put(firstK, new float[] {lastValue[0] + firstValue[0], lastValue[1] + firstValue[1]});
            }
        }

        //HashMap -> Path
        Path[] paths = new Path[subPath.size()];
        for (int i = 0; i < maps.size(); i++) {
            paths[i] = createPath(maps.get(i));
        }

        return paths;
    }

    private LinkedHashMap<String, float[]> deepCopyMap(LinkedHashMap<String, float[]> fromMap) {
        LinkedHashMap<String, float[]> map = new LinkedHashMap<>();
        Iterator<String> iteratorK = fromMap.keySet().iterator();
        Iterator<float[]> iteratorV = fromMap.values().iterator();
        while (iteratorK.hasNext()) {
            String key = iteratorK.next();
            float[] v = iteratorV.next();
            float[] values = new float[v.length];
            System.arraycopy(v, 0, values, 0, v.length);
            map.put(key, values);
        }

        return map;
    }
}
