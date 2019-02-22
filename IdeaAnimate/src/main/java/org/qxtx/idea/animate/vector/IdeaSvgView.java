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
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;

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

public class IdeaSvgView extends android.support.v7.widget.AppCompatImageView {
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

    private RectF rectF;

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

        showSvg(svgPath, isFillPath);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        /* Center the svg, it looks work in well now. Move the path but not the canvas. */
        movePathToCenter();

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
     * Get svg path.
     * @return svg path that type of {@link Path}
     */
    public Path getPath() {
        return path;
    }

    /**
     * Get svg data.
     * Warning: pullAll() only can be make a deep-copy with base-value but object.
     * @return A LinkedHashMap of deep-copy from {@link #startSvg}
     */
    public LinkedHashMap<String, float[]> getSvgMap() {
        return deepCopy(startSvg);
    }

    /**
     * Get svg data.
     * @return svg data that type of {@link String}
     */
    public String getSvgString() {
        return map2String(startSvg);
    }

    /**
     * @return duration of animation.
     */
    public long getDuration() {
        return duration;
    }

    public int getFillColor() {
        return fillColor;
    }

    public int getLineColor() {
        return lineColor;
    }

    /**
     * @return true means view is playing aniamtion, or not
     */
    public boolean isAnimRunning() {
        return animator != null;
    }

    /**
     * Show a svg immediately without animation. It will clear current svg.
     * @param svgPath svg data
     * @param isFillPath fill color to path if true, or draw path only
     */
    public void showSvg(@NonNull String svgPath, boolean isFillPath) {
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

        startSvg = string2Map(svgPath);
        path = createPath(startSvg);
        RectF rectF = new RectF();
        path.computeBounds(rectF, true);
        postInvalidate();
    }

    public void showSvg(@NonNull String svgPath) {
        showSvg(svgPath, false);
    }

    /**
     * Extra animation. Scale svg with animation.
     * @param scale must be positive, in float
     */
    public void scale(float scale) {
        if (scale < 0f) {
            Log.e(TAG, "Value of scale must be positive.");
            return ;
        }

        LinkedHashMap<String, float[]> svg = getSvgMap();
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

        showWithAnim(newSvg);
    }

    /**
     * It will change old svg to new svg with animation, and the new svg data type is String.
     * @param toSvg new svg data, example as "M0,0 L3,4 L5,6 z"
     */
    public void showWithAnim(@NonNull String toSvg) {
        if (animator != null) {
            Log.e(TAG, "IdeaSvgView only can run one animate in the same time! wait for current animation?");
            return ;
        }

        endSvg = string2Map(toSvg);
        svgAnimation();
    }

    /**
     * It will change old svg to new svg with animation, and the new svg data type is LinkedHashMap<String, float[]>.
     * @param toSvg new svg data, example as "{"M0":{0,0}, "L1":{3,4}, "L2":{"5,6"}, "z3":{}}"
     */
    public void showWithAnim(LinkedHashMap<String, float[]> toSvg) {
        if (animator != null) {
            Log.e(TAG, "IdeaSvgView only can run one animate in the same time! wait for current animation?");
            return ;
        }

        endSvg = endSvg == null ? new LinkedHashMap<>() : endSvg;
        endSvg.clear();
        endSvg = deepCopy(toSvg);
//        endSvg.putAll(toSvg); //Should to take a deep-copy

        svgAnimation();
    }

    /**
     * Trim one dst and move at the path with animation
     * @param dstLen trim len of path
     */
    public void startTrimAnim(int dstLen) {
        trimAnimation(dstLen, false);
    }

    /**
     * Trim path that len min of 0 and max of the whole path.
     * @param isReverse len of trim from whole path to 0 if true, or reverse
     */
    public void startTrimAnim(boolean isReverse) {
        trimAnimation(-1, isReverse);
    }

    public void startTrimAnim() {
        startTrimAnim(false);
    }

    /**
     * Set duration of a animation.
     */
    public IdeaSvgView setDuration(long duration) {
        this.duration = duration;
        return this;
    }

    /**
     * Set color of path fill.
     */
    public IdeaSvgView setFillColor(int fillColor) {
        this.fillColor = fillColor;
        return this;
    }

    /**
     * Set color of path.
     */
    public IdeaSvgView setLineColor(int lineColor) {
        this.lineColor = lineColor;
        return this;
    }

    /**
     * Set a paint.
     */
    public IdeaSvgView setPaint(Paint paint) {
        this.paint = paint;
        return this;
    }

    /**
     * Set stroke width of paint, must be positive
     */
    public IdeaSvgView setStrokeWidth(float strokeWidth) {
        if (strokeWidth > 0f) {
            this.strokeWidth = strokeWidth;
        }
        return this;
    }

    /**
     * Convert svg data type from string to LinkedHashMap<String, float[]>.
     * @param svgData svg data string
     * @return svg data that type of {@link LinkedHashMap}
     */
    public LinkedHashMap<String, float[]> string2Map(String svgData) {
        //也许使用正则负担也不大
        svgData = svgData.trim()
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

    /**
     * Convert svg data type from LinkedHashMap<String,float[]> to string.
     * @param svgData svg data map
     * @return svg data that type of {@link String}
     */
    public String map2String(LinkedHashMap<String, float[]> svgData) {
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

    /**
     * Move path to the view center.
     */
    private void movePathToCenter() {
        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;
        if (path != null) {
            if (rectF == null) {
                rectF = new RectF();
            }
            path.computeBounds(rectF, true);
            centerX = centerX - rectF.centerX();
            centerY = centerY - rectF.centerY();
            path.offset(centerX, centerY);
        }
    }

    /**
     * Create path with data type of {@link LinkedHashMap}.
     * @param svgMap svg data
     * @return the object about svg and type of {@link Path}
     */
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

    /**
     * Make deep-copy of object type of {@link LinkedHashMap}.
     * @param fromMap svg data
     * @return svg data type of {@link LinkedHashMap}
     */
    private LinkedHashMap<String, float[]> deepCopy(LinkedHashMap<String, float[]> fromMap) {
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

    /**
     * Split path to the simple path that everyone is a close-path.
     */
    private Path[] splitPath() {
        List<String> subPath = new ArrayList<>();

        //Svg map path -> String[] path，每一条闭合的路径
        String svgPath = map2String(startSvg);
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
            maps.add(string2Map(data));
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
                startSvg = deepCopy(endSvg);
//                startSvg.putAll(endSvg); //Should to make a deep-copy
                endSvg.clear();
                animator = null;
            }
        });

        animator.start();
    }

    /**
     * The svg maybe contains multi close-path, so we have to check it to take animation fully.
     * If dstLen not the -1, is take a dst animation, or take a fully trim animation.
     * If dstLen is not the positive, make second param invalid.
     * @param dstLen length of trim path, in pixel
     * @param isReverse false is change from to dst to fully, or change from fully t dst
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
}
