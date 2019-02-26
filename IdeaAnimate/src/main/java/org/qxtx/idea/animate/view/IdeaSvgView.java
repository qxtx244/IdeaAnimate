package org.qxtx.idea.animate.view;

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

import org.qxtx.idea.animate.IdeaUtil;
import org.qxtx.idea.animate.svg.IdeaSvgManager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @CreateDate 2019/02/14 14:26.
 * @Author QXTX-GOSPELL
 *
 * A custom view for svg.
 *  You can use {@link IdeaSvgManager} to take vector animation easily.
 *
 * @see IdeaSvgManager
 *
 * IdeaSvgView在同一时间仅支持一个动画的播放，如果多个动画在同一时间段被要求播放，则只播放第一个动画，其他动画不被执行；
 * 例外的是，裁剪动画可与svg动画同时执行
 *
 * 支持的keyword（包括小写）：M L Q C H V S T Z A(A路径暂时使用直线代替)
 *
 * String格式路径要求：
 *   1、分隔符可以为【,】或【空格】；
 *   2、字符串以【m/M】开始；
 *   3、每个keyword字符必须使用【分隔符】和前一个keyword的数值隔开；
 *   4、必须以【z/Z】结尾；
 *   5、支持多条闭合路径组成的svg图形；
 *   示例：
 *   "M0 0 L50 0 L50 10 L0 10,z M0 20 L50 20 L50,30 L0 30 Z "
 *
 * LinkedHashMap< String,float[]>格式路径要求：
 *   1、每个键值对为一条路径的完整数据，key为【keyword(+其它数据)】的字符串，value为【float[]数组】；
 *   2、以key为【m/M(+其它数据)】的键值对开始；
 *   3、最后一个键值对的key必须为【z/Z(+其它数据)】，此键值对的value不能为【null】。
 *
 * 备注：view的tag用来标记view此时的svg状态
 *
 * 后续需要：
 *   1、问题：多色路径模式下，执行缩放动画和形变动画都会导致多色效果消失（这些方法会重置模式）；
 *   2、允许为多个闭合路径填充不同颜色
 *   3、允许图案画笔
 *   4、svg立体化（复制路径并且布尔运算得到阴影部分区域？然后填充阴影色，并且可考虑侵倾斜)
 *   5、遮罩动画？（利用布尔运算）
 */
public class IdeaSvgView extends android.support.v7.widget.AppCompatImageView {
    private static final String TAG = "IdeaSvgPathAnimate";

    private static final int MODE_NORMAL = 0;
    private static final int MODE_TRIM = 1;

    private static final String DEFAULT_COLOR = "#1E90FF";
    private static final long DEFAULT_DURATION = 500;
    private static final float DEFAULT_STROKE_WIDTH = 3f;

    private String tag;

    private int mode;

    /* svg data */
    private LinkedHashMap<String, float[]> startSvg;
    private LinkedHashMap<String, float[]> endSvg;

    /* value of translate to the canvas to set svg in the view center. */
    private float[] centerPos;

    /* always to split path as subPath array for draw colorfully. */
    private Path[] path;
    /* make path colorfully to split some dst while path is single close-path. */
    private Path[] dstPath;

    /* path array for different mode. */
    private Path[] trimPath;

    /* Settable params. */
    private Paint paint;
    private long duration;
    private int[] lineColor;
    private int[] fillColor;
    private float strokeWidth;
    private Paint.Style drawStyle;

    /* Animation */
    private ValueAnimator animator;
    private RectF pathRectF;

    //Important value for MODE_TRIM.
    private float[] lastPointer;

    private PathMeasure pathMeasure;

    private List<LinkedHashMap<String, float[]>> subPathMap;
//    private long timeCounter = 0;
//    private long timeDrawOnce = 0;

    public IdeaSvgView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public IdeaSvgView(Context context, @NonNull String svgData, boolean isFillPath, int... colors) {
        super(context);
        init();

        colors = colors == null ? new int[] {Color.parseColor(DEFAULT_COLOR)} : colors;
        int[] color = Arrays.copyOf(colors, colors.length); //deep-copy to forbidden to change view data by outside
        if (isFillPath) {
            fillColor = color;
            drawStyle = Paint.Style.FILL;
        } else {
            lineColor = color;
            drawStyle = Paint.Style.STROKE;
        }
        showSvg(svgData, drawStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        timeDrawOnce = SystemClock.currentThreadTimeMillis();

        super.onDraw(canvas);

        if (path == null) {
            return ;
        }

        /* Translate canvas to set svg in the view center. */
        canvas.translate(centerPos[0], centerPos[1]);

        /* if not the MODE_NORMAL, only set true to isFillPath can draw path once, or draw path once whatever. */
        boolean notLine = drawStyle != IdeaUtil.PAINT_LINE;
        if (notLine) {
            paint.setStyle(Paint.Style.FILL);
            drawPaths(path, canvas, fillColor);
        }

        /* draw path as line. */
        if (drawStyle == IdeaUtil.PAINT_FILL_AND_LINE || mode == MODE_NORMAL) {
            paint.setStyle(Paint.Style.STROKE);
            Path[] curPath = path.length == 1 ? dstPath : path;
            drawPaths(curPath, canvas, lineColor);
        }

        //Extra mode：trim dst
        switch (mode) {
            case MODE_TRIM:
                drawPaths(trimPath, canvas, lineColor);
                break;
        }

//        Log.e(TAG, "draw time use: " + (SystemClock.currentThreadTimeMillis() - timeDrawOnce) + " ms");
    }

    /**
     * TOOL 1: Check subPath number from the fully path.
     * @return counter of subPath, -1 means the svg data invalid.
     */
    public int checkSubPathNum(String svgData) {
        if (!checkSvgData(svgData)) {
            Log.e(TAG, "Invalid svg data.");
            return -1;
        }

        int counter = 0;
        for (int i = 0; i < svgData.length(); i++) {
            if (svgData.charAt(i) == 'z' || svgData.charAt(i) == 'Z') {
                counter++;
            }
        }

        return counter;
    }

    /**
     * TOOL 2: Check whether the svg data is valid. You can call this before show a svg.
     * @return svg data is valid if true, or invalid.
     */
    public static boolean checkSvgData(String svgData) {
        if (svgData == null) {
            Log.e(TAG, "Svg data was not found.");
            return false;
        }

        svgData = svgData.trim();

        /* check whether the svg data is start with 'M' or 'm' */
        boolean startWithM = svgData.startsWith("m") || svgData.startsWith("M");
        if (!startWithM) {
            Log.e(TAG, "Invalid svg data. Not the start with 'm' or 'M'.");
            return false;
        }

        /* check whether the svg data is end with 'z' or 'Z' */
        boolean endWithZ = svgData.endsWith("z") || svgData.endsWith("Z");
        if (!endWithZ) {
            Log.e(TAG, "Invalid svg data. Not the end with 'z' or 'Z'.");
            return false;
        }

        /* check whether all the svg data is character valid. */
        boolean isAllCharValid = false;
        for (int i = 0; i < svgData.length(); i++) {
            String s = svgData.charAt(i) + "";
            isAllCharValid = IdeaUtil.VALID_CHAR.contains(s);
            if (!isAllCharValid) {
                break;
            }
        }
        if (!isAllCharValid) {
            Log.e(TAG, "Invalid svg data. Find some invalid character.");
            return false;
        }

        /* check whether all the value array is size enough. */
        boolean isValueEnough = true;
        String keyword = IdeaUtil.VALID_CHAR.substring(0, 20);
        final int[] divNum = new int[] {1, 1, 1, 1, 5, 5, 3, 3, 0, 0, 0, 0, 0, 0, 5, 5, 3, 3, 6, 6};
        for (int i = 0; i < svgData.length(); i++) {
            String curKeyword = svgData.charAt(i) + "";
            String nextKeyword;
            if (keyword.contains(curKeyword)) {
                int keywordValuesNum = divNum[keyword.indexOf(curKeyword)];
                for (int j = i + 1; j < svgData.length(); j++) {
                    nextKeyword = svgData.charAt(j) + "";
                    if (keyword.contains(nextKeyword)) {
                        //开始计算两者之间的分隔符个数
                        int divCounter = 0;
                        for (int k = i; k < j; k++) {
                            char div = svgData.charAt(k);
                            if (div == ' ' || div == ',') {
                                divCounter++;
                            }
                        }
                        if (divCounter < keywordValuesNum) {
                            isValueEnough = false;
                            break;
                        }
                        break;
                    }
                }
            }
        }

        if (!isValueEnough) {
            Log.e(TAG, "Invalid svg data. wrong value array size.");
            return false;
        }

        return true;
    }

    /**
     * TOOL 3: Get svg data.
     * @return object type of {@link LinkedHashMap} and with a deep-copy
     */
    public LinkedHashMap<String, float[]> getSvgMap() {
        return deepCopy(startSvg);
    }

    /**
     * TOOL 4: Get svg data.
     * @return svg data that type of String
     */
    public String getSvgString() {
        return map2String(startSvg);
    }

    public long getDuration() {
        return duration;
    }

    public int[] getFillColor() {
        return fillColor;
    }

    public int[] getLineColor() {
        return lineColor;
    }

    /**
     * TOOL 5: Check whether the view is playing animation.
     * @return true means view is playing animation, or not
     */
    public boolean isAnimRunning() {
        return animator != null && !animator.isPaused() && !animator.isRunning() && !animator.isStarted();
    }

    public void showSvg(@NonNull String svgData) {
        showSvg(svgData, drawStyle);
    }

    /**
     * Show a svg immediately without animation. It will clear current svg.
     * @param svgData svg data
     * @param drawStyle see {@link Paint.Style}
     *
     * @see org.qxtx.idea.animate.IdeaUtil#PAINT_LINE
     * @see org.qxtx.idea.animate.IdeaUtil#PAINT_FILL
     * @see org.qxtx.idea.animate.IdeaUtil#PAINT_FILL_AND_LINE
     */
    public void showSvg(@NonNull String svgData, Paint.Style drawStyle) {
        if (!checkSvgData(svgData)) {
            Log.e(TAG, "Invalid svg data, show svg refused.");
            return ;
        }

        this.drawStyle = drawStyle;
        startSvg = string2Map(svgData);
        path = splitPath(startSvg);

        /* get translate value of canvas. */
        getCanvasTranslate(startSvg);

        stopAnimate(true);

        postInvalidate();
    }

    /**
     * It will change old svg to new svg with animation, and the new svg data type is LinkedHashMap<String, float[]>.
     * @param toSvg new svg data, example as "{"M0":{0,0}, "L1":{3,4}, "L2":{"5,6"}, "z3":{}}"
     */
    public void showWithAnim(@NonNull LinkedHashMap<String, float[]> toSvg) {
        showWithAnim(map2String(toSvg));
    }

    /**
     * It will change old svg to new svg with animation, and the new svg data type is String.
     * @param toSvg new svg data format like "M0,0l3 4,L5,6 z"
     */
    public void showWithAnim(@NonNull String toSvg) {
        if (!checkSvgData(toSvg)) {
            Log.e(TAG, "Invalid svg data, animate refused.");
            return ;
        }
        if (isAnimRunning()) {
            Log.e(TAG, "IdeaSvgView can only run one animation in the same time! Wait for this current animation?");
           return ;
        }

//        svgData = toSvg;
        endSvg = string2Map(toSvg);
        svgAnimation();
    }

    public void startTrimAnim() {
        startTrimAnim(false);
    }

    /**
     * Trim path that len min of 0 and max of the whole path.
     * @param isReverse len of trim from whole path to 0 if true, or reverse
     */
    public void startTrimAnim(boolean isReverse) {
        trimAnimation(-1, isReverse);
    }

    /**
     * Trim one dst and move at the path with animation
     * @param dstLen trim len of path
     */
    public void startTrimAnim(int dstLen) {
        trimAnimation(dstLen, false);
    }

    /**
     * Set duration of a animation, must be positive.
     */
    public IdeaSvgView setDuration(long duration) {
        if (duration < 0) {
            Log.e(TAG, "Duration must be positive! set refused.");
        }
        this.duration = duration;
        return this;
    }

    /**
     * Set color of path fill. When more than one of fill color, it may fill path colorfully.
     */
    public IdeaSvgView setFillColor(@NonNull int... fillColor) {
        if (fillColor.length > 0) {
            this.fillColor = Arrays.copyOf(fillColor, fillColor.length);
        }
        return this;
    }

    /**
     * Set color of path. When more than one of line color, it may draw path colorfully.
     */
    public IdeaSvgView setLineColor(@NonNull int... lineColor) {
        if (lineColor.length > 0) {
            this.lineColor = Arrays.copyOf(lineColor, lineColor.length);
        }
        return this;
    }

    /**
     * Set stroke width of paint, must be positive.
     */
    public IdeaSvgView setStrokeWidth(float strokeWidth) {
        if (strokeWidth > 0f) {
            this.strokeWidth = strokeWidth;
        }
        return this;
    }

    /** TOOL 6: Split path to the simple path that everyone is a close-path. */
    public Path[] splitPath(LinkedHashMap<String, float[]> pathMap) {
        if (pathMap == null) {
            return null;
        }

        if (subPathMap == null) {
            subPathMap = new ArrayList<>();
        } else {
            subPathMap.clear();
        }

        int startPos = 0;
        String fullPath = map2String(pathMap);
        for (int i = 0; i < fullPath.length(); i++) {
            if (fullPath.charAt(i) == 'z' || fullPath.charAt(i) == 'Z') {
                String subPath = fullPath.substring(startPos, i + 1);
                subPathMap.add(string2Map(subPath));
                startPos = i + 1;
            }
        }

        int subPathNum = subPathMap.size();

        /* help to divide every close-path from the svg path. */
        if (lastPointer == null) {
            lastPointer = new float[2];
        } else {
            lastPointer[0] = 0f;
            lastPointer[1] = 0f;
        }
        saveLastPointer(subPathMap.get(0));

        /* first 'm' value -> 'M' value. */
        Path[] subPaths = new Path[subPathNum];
        for (int i = 0; i < subPathNum; i++) {
            LinkedHashMap<String, float[]> curMap = subPathMap.get(i);

            String firstK = curMap.keySet().iterator().next();
            float[] firstV = curMap.values().iterator().next();
            if (firstK.charAt(0) == 'm') {
                //although it was ugly for java style, but it can create much less object.
                //better code? => curMap.put(firstK, new float[] {firstV[0] + lastPointer[0], firstV[1] + lastPointer[1]});
                float[] v = curMap.get(firstK);
                v[0] = firstV[0] + lastPointer[0];
                v[1] = firstV[1] + lastPointer[1];
            }

            /* start with 1 because it already take once before this looper. */
            if (i > 0) {
                saveLastPointer(curMap);
            }

            subPaths[i] = new Path();
            createPath(curMap, subPaths[i]);
        }

        /* it must be split more dst to set path colorfully. */
        int colorNum = lineColor.length;
        if (subPaths.length == 1 && colorNum != 1) {
            if (dstPath ==null) {
                dstPath = new Path[colorNum];
                for (int i = 0; i < dstPath.length; i++) {
                    dstPath[i] = new Path();
                }
            }

            if (pathMeasure == null) {
                pathMeasure = new PathMeasure();
            }
            pathMeasure.setPath(subPaths[0], false);
            float unitLen = pathMeasure.getLength() / colorNum;
            float startD = 0f;
            for (int i = 0; i < dstPath.length; i++) {
                dstPath[i].reset();
                dstPath[i].rewind();
//                dstPath[i] = new Path();
                pathMeasure.getSegment(startD, startD + unitLen, dstPath[i], true);
                startD += unitLen;
            }
        }

        return subPaths;
    }

    /**
     * It is better choose to use {@link #stopAnimateSafely()} instead of this call
     *  if you wish work well whatever. Also you can call this if it is useful to call this
     *  or you really want to try it.
     *
     * @param forceStop cancel animation if true, It will be interrupt animation immediately if true
     *                  and maybe cause something happen that you're unexpected.
     *                  End animation if false, and it will set animation to the final status.
     */
    public void stopAnimate(boolean forceStop) {
        if (!isAnimRunning()) {
            return ;
        }

        if (forceStop) {
            animator.cancel();
            animator = null;
        } else {
            animator.end();
        }
    }

    /**
     * It is a better choose than {@link #stopAnimate(boolean)} while safe has the highest priority.
     */
    public void stopAnimateSafely() {
        stopAnimate(false);
    }

    /**
     * TOOL 7: Convert data type to String.
     * @param data data type of LinkedHashMap
     * @return svg data that type of {@link String}
     */
    public String map2String(@NonNull LinkedHashMap<String, float[]> data) {
        StringBuilder curPath = new StringBuilder();
        for (String key : data.keySet()) {
            float[] values = data.get(key);
            curPath.append(key.charAt(0));
            for (float value : values) {
                curPath.append(value).append(" ");
            }
        }
        return curPath.toString();
    }

    /**
     * TOOL 8: Convert data type to {@link LinkedHashMap}.
     * @param data data that type of string
     * @return object of {@link LinkedHashMap}
     */
    public LinkedHashMap<String, float[]> string2Map(@NonNull String data) {
        data = data.trim()
        .replace("z", " z")
        .replace("Z", " Z")
        .replace("  ", " ");

        String keywords = IdeaUtil.VALID_CHAR.substring(0, 20);
        final int[] divNum = new int[] {2, 2, 2, 2, 6, 6, 4, 4, 1, 1, 1, 1, 0, 0, 6, 6, 4, 4, 7, 7};
        LinkedHashMap<String, float[]> map = new LinkedHashMap<>();

        int endIndex;
        for (int i = 0; i < data.length(); i = endIndex + 1) {
            char svgKey = data.charAt(i);
            int arraySize;

            int index = keywords.indexOf(svgKey);
            if (index == -1) {
                arraySize = -1;
            } else {
                arraySize = divNum[index];
            }
            endIndex = saveSvg(arraySize, data, i, map);
        }

        return map;
    }

    /**
     * Extra animation. Scale svg with animation.
     * @param scale must be positive, in float
     */
    public void scale(float scale) {
        if (startSvg == null) {
            Log.e(TAG, "Svg not found, please call showSvg() to set a svg first. Scale refused.");
            return ;
        }
        if (scale < 0f) {
            Log.e(TAG, "Value of scale must be positive! scale refused.");
            return ;
        }

        LinkedHashMap<String, float[]> newSvg = new LinkedHashMap<>();
        Iterator<float[]> iteratorV = startSvg.values().iterator();
        for (String key : startSvg.keySet()) {
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
     * Create path with data type of {@link LinkedHashMap}.
     * @param svgMap svg data
     * @param path the path who the result save to
     *
     * @see #splitPath(LinkedHashMap)
     * @see #getCanvasTranslate(LinkedHashMap)
     * @see #svgAnimation()
     */
    private void createPath(@NonNull LinkedHashMap<String, float[]> svgMap, @NonNull Path path) {
        path.reset();
        path.rewind();

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
                case 'A': //Use lineTo() instead of arcTo() because i'm fail to make arcTo()
                    path.lineTo(values[values.length - 2], values[values.length - 1]);
                    break;
                case 'a': //Use rLineTo() instead of arcTo() because i'm fail to make arcTo()
                    path.rLineTo(values[values.length - 2], values[values.length - 1]);
                    break;
                case 'H':
                    path.lineTo(values[0], 0f);
                    break;
                case 'h':
                    path.rLineTo(values[0], 0f);
                    break;
                case 'V':
                    path.lineTo(0f, values[0]);
                    break;
                case 'v':
                    path.rLineTo(0f, values[0]);
                    break;
            }
        }
    }

    /**
     * Make deep-copy of object type of {@link LinkedHashMap}.
     * @return the object type of {@link LinkedHashMap}
     *
     * @see #getSvgMap()
     * @see #svgAnimation()
     */
    private LinkedHashMap<String, float[]> deepCopy(@NonNull LinkedHashMap<String, float[]> fromMap) {
        LinkedHashMap<String, float[]> map = new LinkedHashMap<>();
        Iterator<String> iteratorK = fromMap.keySet().iterator();
        Iterator<float[]> iteratorV = fromMap.values().iterator();
        while (iteratorK.hasNext()) {
            float[] v = iteratorV.next();
            map.put(iteratorK.next(), Arrays.copyOf(v, v.length));
        }
        return map;
    }

    /** @see #onDraw(Canvas). */
    private void drawPaths(@NonNull Path[] paths, @NonNull Canvas canvas, @NonNull int[] colors) {
        int color;
        //it maybe cause exception as canvas is null. I don't know why it happen.
        for (int i = 0; i < paths.length; i++) {
            color = i >= colors.length ? colors[0] : colors[i];
            paint.setColor(color);
            canvas.drawPath(paths[i], paint);
        }
    }

    private void init() {
        mode = MODE_NORMAL;
        duration = DEFAULT_DURATION;
        strokeWidth = DEFAULT_STROKE_WIDTH;
        lineColor = new int[] {Color.parseColor(DEFAULT_COLOR)};
        fillColor = new int[] {Color.parseColor(DEFAULT_COLOR)};

        centerPos = new float[2];

        drawStyle = IdeaUtil.PAINT_LINE;
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(lineColor[0]);
        paint.setStrokeWidth(strokeWidth);

        IdeaSvgManager manager = IdeaSvgManager.getInstance();
        this.tag = tag == null ? manager.getCount() + "" : tag;
        manager.add(this);
    }

    /**
     * parse value array from one pointer
     * @param data svg data
     * @param valueSave the value array which values of a subPath save to
     * @param startIndex start index of subPath's value, it has been skip the keyword
     * @return end index of this subPath
     *
     * @see #saveSvg(int, String, int, LinkedHashMap)
     */
    private int parseValueArray(@NonNull String data, @NonNull float[] valueSave, int startIndex) {
        int arraySize = valueSave.length;
        int endIndex = 0;
        for (int i = 0; i < arraySize; i++) {
            int divSpace = data.indexOf(" ", startIndex);
            int divComma = data.indexOf(",", startIndex);
            endIndex = Math.min(divSpace, divComma);
            if (divSpace == -1) {
                endIndex = divComma;
            } else if (divComma == -1) {
                endIndex = divSpace;
            }

            //if didn't find any divider, do nothing and return current index.
            if (endIndex == -1) { //end of data or something error.
                return startIndex;
            } else if (endIndex == startIndex) { //number value was not found between startIndex and endIndex.
                if (endIndex + 1 == data.length()) {
                    return endIndex;
                }
                startIndex = endIndex + 1; //skip the divider
                i--; //loop again that not add i
                continue;
            }

            String value = data.substring(startIndex, endIndex);
            valueSave[i] = Float.parseFloat(value.replace(" ", ""));
            startIndex = endIndex + 1;
        }
        return endIndex;
    }

    /**
     * Save svg data to {@param map} is type of {@link LinkedHashMap}.
     * @param arraySize subPath's value count
     * @param svgData svg data
     * @param startIndex start index of subPath's value array
     * @param saveMap which map is svg data save to
     * @return start index of next subPath
     *
     * @see #string2Map(String)
     */
    private int saveSvg(int arraySize, @NonNull String svgData, int startIndex, @NonNull LinkedHashMap<String, float[]> saveMap) {
        char keyword = svgData.charAt(startIndex);
        String key = keyword + "" + saveMap.size();
        int valueStartIndex = startIndex + 1;
        int endIndex = startIndex;

        //0 means take a 'z'/'Z' and -1 means something need to be skip.
        if ((keyword == 'z' || keyword == 'Z') && arraySize == 0) {
            saveMap.put(key, new float[0]);
            return endIndex;
        } else if (arraySize == -1) {
            return endIndex;
        }

        //get value array.
        float[] values = new float[arraySize];
        endIndex = parseValueArray(svgData, values, valueStartIndex);

        saveMap.put(key, values);
        return endIndex;
    }

    /**
     * Record the last pointer of every close-subPath.
     *
     * @see #splitPath(LinkedHashMap)
     */
    private void saveLastPointer(@NonNull LinkedHashMap<String, float[]> pathMap) {
        Iterator<String> iteratorK = pathMap.keySet().iterator();
        for (int i = 0; i < pathMap.size(); i++) {
            String key = iteratorK.next();
            float[] values = pathMap.get(key);
            char keyword = key.charAt(0);
            if (values.length == 0) {
                //do nothing
            } else if (Character.isUpperCase(keyword)) {
                if (values.length == 1) {
                    lastPointer[0] = keyword == 'H' ? values[0] : lastPointer[0];
                    lastPointer[1] = keyword == 'V' ? values[0] : lastPointer[1];
                } else {
                    lastPointer[0] = values[values.length - 2];
                    lastPointer[1] = values[values.length - 1];
                }
            } else if (Character.isLowerCase(keyword)) {
                if (values.length == 1) {
                    lastPointer[0] += keyword == 'h' ? values[0] : lastPointer[0];
                    lastPointer[1] += keyword == 'v' ? values[0] : lastPointer[1];
                } else {
                    //it was already change the start pointer's value as the split path if subPath start with 'm'
                    // but it's keyword is still 'm', so it must be take as 'M'
                    if (i == 0 && keyword == 'm') {
                        System.arraycopy(values, 0, lastPointer, 0, 2);
                    } else {
                        lastPointer[0] += values[values.length - 2];
                        lastPointer[1] += values[values.length - 1];
                    }
                }
            }
        }
    }

    /**
     * Translate canvas to set path in the view center.
     *
     * @see #showSvg(String, Paint.Style)
     */
    private void getCanvasTranslate(LinkedHashMap<String, float[]> svgMap) {
        Path measurePath;
        if (path != null && path.length == 1) {
            measurePath = path[0];
        } else {
            measurePath = new Path();
            createPath(svgMap, measurePath);
        }

        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;

        if (pathRectF == null) {
            pathRectF = new RectF();
        }

        measurePath.computeBounds(pathRectF, true);
        centerPos[0] = centerX - pathRectF.centerX();
        centerPos[1] = centerY - pathRectF.centerY();
    }

    /**
     * Enable the animation to show svg.
     *
     * @see #showWithAnim
     */
    private void svgAnimation() {
        /* It's still not a hard check because it maybe the different keyword even though they are has the same size. */
        if (startSvg == null || endSvg == null || (endSvg.size() != startSvg.size())) {
            Log.e(TAG, "Unable to start animation, different the keyword of fromPath and toPath");
            return ;
        }

        mode = MODE_NORMAL;

        LinkedHashMap<String, float[]> newSvg = new LinkedHashMap<>();
        animator = ValueAnimator.ofFloat(0f, 1f).setDuration(duration);
        animator.addUpdateListener(animation -> {
//            timeCounter = SystemClock.currentThreadTimeMillis();

            newSvg.clear();
            float fraction = animation.getAnimatedFraction();

            Iterator<String> iteratorK = startSvg.keySet().iterator();
            Iterator<float[]> fromValue = startSvg.values().iterator();
            Iterator<float[]> toValue = endSvg.values().iterator();
            while (iteratorK.hasNext()) {
                float[] from = fromValue.next();
                float[] to = toValue.next();
                if (from.length != to.length) {
                    Log.e(TAG, "Different value num between array from and to.");
                    onAnimationEnd();
                }

                float[] newValue = new float[from.length];
                for (int i = 0; i < newValue.length; i++) {
                    newValue[i] = from[i] + (to[i] - from[i]) * fraction;
                }
                newSvg.put(iteratorK.next(), newValue);
            }

            /* 这里如果只存在一条path，则将失去多色路径的作用，所以变化过程中只使用一个路径，在最后一帧时才重新配置多色路径. */
            getCanvasTranslate(newSvg);
            path = splitPath(newSvg);

            postInvalidate();

//            Log.e(TAG, "value once time use: " + (SystemClock.currentThreadTimeMillis() - timeCounter) + " ms"
//                  + ", counter= ");
        });
        animator.addListener(new ListenerAdapter(this));
        animator.start();
    }

    /**
     * The svg maybe contains multi close-path, so we have to check it to take animation fully.
     * If dstLen not the -1, is take a dst animation, or take a fully trim animation.
     * If dstLen is not the positive, make second param invalid.
     * @param dstLen length of trim path, in pixel
     * @param isReverse false is change from to dst to fully, or change from fully t dst
     *
     * @see #startTrimAnim
     */
    private void trimAnimation(float dstLen, boolean isReverse) {
        if (isAnimRunning()) {
            Log.e(TAG, "IdeaSvgView is playing animation now, wait for this animation?");
            return ;
        }

        mode = MODE_TRIM;

        Path[] paths;
        if (path == null) {
            paths = splitPath(startSvg);
        } else {
            paths = new Path[path.length];
            for (int i = 0; i < paths.length; i++) {
                paths[i] = new Path();
                paths[i].set(path[i]);
            }
        }

        trimPath = new Path[paths.length];
        if (pathMeasure == null) {
            pathMeasure = new PathMeasure();
        }

        animator = ValueAnimator.ofFloat(0f, 1f).setDuration(duration);
        animator.addUpdateListener(animator -> {
            float fraction = animator.getAnimatedFraction();
            for (int i = 0; i < paths.length; i++) {
                Path dst = new Path();
                /* Order to official document, it can be make animation workaround even sdk version number
                  below to 19 and display on the hardware-accelerated canvas. */
                dst.rLineTo(0f, 0f);

                pathMeasure.setPath(paths[i], false);
                float pathLen = pathMeasure.getLength();
                float curDstLen = dstLen > pathLen ? pathLen : dstLen;

                float startD, endD;
                if (curDstLen <= 0) { //fully trim
                    startD = 0f;
                    endD = (isReverse ? (1f - fraction) : fraction) * pathLen;
                } else {
                    startD = pathLen * fraction;
                    endD = startD + curDstLen;
                }

                if ((startD == 0f && endD == 0f) //(startD == 0f && endD == 0f): trimPath must be not null in the start.
                        || pathMeasure.getSegment(startD, endD, dst, true)) {
                    trimPath[i] = dst;
                }
            }
            postInvalidate();
        });
        animator.addListener(new ListenerAdapter(this));
        animator.start();
    }

    /** @see ListenerAdapter */
    void onAnimEnd() {
        if (mode != MODE_TRIM) {
            startSvg = deepCopy(endSvg);
            endSvg.clear();
        }
        animator = null;
    }

    private static class ListenerAdapter extends AnimatorListenerAdapter {
        private WeakReference<IdeaSvgView> ideaSvgView;

        ListenerAdapter(IdeaSvgView ideaSvgView) {
            this.ideaSvgView = new WeakReference<>(ideaSvgView);
        }
        @Override
        public void onAnimationEnd(Animator animation) {
            if (ideaSvgView != null && ideaSvgView.get() != null) {
                ideaSvgView.get().onAnimEnd();
            }
        }
        @Override
        public void onAnimationCancel(Animator animation) {
            if (ideaSvgView != null && ideaSvgView.get() != null) {
                ideaSvgView.get().onAnimEnd();
            }
        }
    }
}
