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

import org.qxtx.idea.animate.vector.IdeaSvgManager;

import java.util.ArrayList;
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
 *   备注：view的tag用来标记view此时的svg状态
 *
 * 后续需要：
 *   1、线条允许多色（完成度30% 单一闭合路径的svg已经实现多色路径，多闭合路径的svg未实现）
 *   2、允许为多个闭合路径填充不同颜色
 *   3、允许图案画笔
 *   4、svg立体化（复制路径并且布尔运算得到阴影部分区域？然后填充阴影色，并且可考虑侵倾斜)
 *   5、遮罩动画？（利用布尔运算）
 */

public class IdeaSvgView extends android.support.v7.widget.AppCompatImageView {
    private static final String TAG = "IdeaSvgPathAnimate";

    private static final int MODE_NORMAL = 0;
    private static final int MODE_TRIM = 1;
    private static final int MODE_LINE_COLORFUL = 2;

    private static final String DEFAULT_COLOR = "#1E90FF";
    private static final long DEFAULT_DURATION = 500;
    private static final float DEFAULT_STROKE_WIDTH = 3f;

    private String tag;

    private LinkedHashMap<String, float[]> startSvg;
    private LinkedHashMap<String, float[]> endSvg;

    private int mode;

    private Path path;
    private Path[] trimPath;
    private Path[] colorfulPath;

    private Paint paint;
    private long duration;
    private int lineColor;
    private int fillColor;
    private float strokeWidth;
    private boolean isFillPath;

    private ValueAnimator animator;
    private RectF pathRectF;

    //MODE_TRIM
    private float[] lastPointer;

    //MODE_LINE_COLORFUL
    private int[] lineColors;

    public IdeaSvgView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public IdeaSvgView(Context context, @NonNull String svgData, int color, boolean isFillPath) {
        super(context);

        init();

        if (isFillPath) {
            fillColor = color;
        } else {
            lineColor = color;
        }

        showSvg(svgData, isFillPath);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        setPathToCenter(path, canvas);

        /*
         * 当不要求填充时，只有svg模式才允许绘制整个path
         * Draw whole path:
         * 1、MODE_NORMAL
         * 2、MODE_TRIM but is fill path
         * 3、
         */
        if (path != null
                && (isFillPath || mode == MODE_NORMAL)) {
            /* Center the svg, it looks work in well now. Move the path but not the canvas. */
            paint.setStyle(isFillPath ? Paint.Style.FILL : Paint.Style.STROKE);
            paint.setColor(isFillPath ? fillColor : lineColor);
            canvas.drawPath(path, paint);
        }

        //Extra1：trim dst
        switch (mode) {
            case MODE_TRIM:
                if (trimPath == null) {
                    break;
                }
                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(lineColor);
                //it maybe cause exception as canvas is null. I don't know why it happen.
                for (Path i : trimPath) {
                    canvas.drawPath(i, paint);
                }
                break;
            case MODE_LINE_COLORFUL:
                if (colorfulPath == null) {
                    break;
                }
                //it maybe cause exception as canvas is null. I don't know why it happen.
                paint.setStyle(Paint.Style.STROKE);
                for (int i = 0; i < colorfulPath.length; i++) {
                    paint.setColor(lineColors[i]);
                    canvas.drawPath(colorfulPath[i], paint);
                }

                break;
        }
    }

    /**
     * Check weather the svg data is valid. You can call this before show a svg.
     * @return svg data is valid if true, or invalid.
     */
    public static boolean checkSvgData(String svgData) {
        if (svgData == null) {
            Log.e(TAG, "Empty svg data.");
            return false;
        }

        svgData = svgData.trim();

        /* check weather the svg data is start with 'M' or 'm' */
        boolean startWithM = svgData.startsWith("m") || svgData.startsWith("M");
        if (!startWithM) {
            Log.e(TAG, "Invalid svg data. Not the start with 'm' or 'M'.");
            return false;
        }

        /* check weather the svg data is end with 'z' or 'Z' */
        boolean endWithZ = svgData.endsWith("z") || svgData.endsWith("Z");
        if (!endWithZ) {
            Log.e(TAG, "Invalid svg data. Not the end with 'z' or 'Z'.");
            return false;
        }

        /* check weather all the svg data is character valid. */
        boolean isAllValid = false;
        String validChar = "MmLlCcQqHhVvZzSsTtAa.0123456789,- ";
        for (int i = 0; i < svgData.length(); i++) {
            String s = svgData.charAt(i) + "";
            isAllValid = validChar.contains(s);
            if (!isAllValid) {
                break;
            }
        }

        if (!isAllValid) {
            Log.e(TAG, "Invalid svg data. Find some invalid character.");
            return false;
        }

        /* check weather all the value array is size enough. */
        boolean isValueEnough = true;
        String keyword = validChar.substring(0, 20);
        int[] divNum = new int[] {1, 1, 1, 1, 5, 5, 3, 3, 0, 0, 0, 0, 0, 0, 5, 5, 3, 3, 6, 6};
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
     * Get current svg path.
     * @return svg path that type of {@link Path}
     */
    public Path getPath() {
        return path;
    }

    /**
     * Get svg data.
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
        return animator != null && !animator.isStarted() && !animator.isRunning() && !animator.isPaused();
    }

    /**
     * Show a svg immediately without animation. It will clear current svg.
     * @param svgData svg data
     * @param isFillPath fill color to path if true, or draw path only
     */
    public void showSvg(@NonNull String svgData, boolean isFillPath) {
        if (!checkSvgData(svgData)) {
            Log.e(TAG, "Invalid svg data, show svg refused.");
            return ;
        }

        stopAnimate(true);

        endSvg = null;
        mode = MODE_NORMAL;

        this.isFillPath = isFillPath;
        if (isFillPath) {
            paint.setColor(fillColor);
        } else {
            paint.setColor(lineColor);
        }

        startSvg = string2Map(svgData);
        //DEBUG POINTER: Check the startSvg
        path = createPath(startSvg);
        postInvalidate();
    }

    public void showSvg(@NonNull String svgData) {
        showSvg(svgData, false);
    }

    /**
     * Draw svg colorfully of the svg path. It was didn't work with a svg made with multi close-path.
     * @param lineColors color array of svg path.
     */
    public void showSvgWithColorful(@NonNull String svgData, int[] lineColors) {
        if (!checkSvgData(svgData)) {
            Log.e(TAG, "Invalid svg data, show svg refused.");
            return ;
        }

        int count = 0;
        for (int i = 0; i < svgData.length(); i++) {
            if (svgData.charAt(i) == 'z' || svgData.charAt(i) == 'Z') {
                count++;
                if (count > 1) {
                    Log.e(TAG, "Fail to show colorfully for svg. It didn't work for a svg made with multi close-path.");
                    showSvg(svgData);
                    return ;
                }
            }
        }

        stopAnimate(true);

        mode = MODE_LINE_COLORFUL;

        endSvg = null;
        startSvg = string2Map(svgData);
        path = createPath(startSvg);

        int dstNum = lineColors.length;
        if (colorfulPath == null) {
            colorfulPath = new Path[dstNum];
        }

        this.lineColors = new int[dstNum];
        System.arraycopy(lineColors, 0, this.lineColors, 0, dstNum);

        PathMeasure pathMeasure = new PathMeasure();
        pathMeasure.setPath(path, false);
        float unitLen = pathMeasure.getLength() / dstNum;
        float startLen = 0f;
        for (int i = 0; i < dstNum; i++) {
            colorfulPath[i] = new Path();
            pathMeasure.getSegment(startLen, startLen + unitLen, colorfulPath[i], true);
            startLen += unitLen;
        }
        postInvalidate();
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
        if (!checkSvgData(toSvg)) {
            Log.e(TAG, "Invalid svg data, animate refused.");
            return ;
        }

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
    public void showWithAnim(@NonNull LinkedHashMap<String, float[]> toSvg) {
        showWithAnim(map2String(toSvg));

        //2019/02/24 call {@link #showWithAnim(String)} to do it.
//        if (checkSvgData(map2String(toSvg))) {
//            Log.e(TAG, "Invalid svg data, animate refused.");
//            return ;
//        }
//
//        if (animator != null) {
//            Log.e(TAG, "IdeaSvgView only can run one animate in the same time! wait for current animation?");
//            return ;
//        }
//
//        endSvg = deepCopy(toSvg);
//
//        svgAnimation();
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
     * Convert svg data type from string to LinkedHashMap<String, float[]>.
     * @param svgData svg data string
     * @return svg data that type of {@link LinkedHashMap}
     */
    public LinkedHashMap<String, float[]> string2Map(@NonNull String svgData) {
        svgData = svgData.trim()
        .replace("z", " z")
        .replace("Z", " Z")
        .replace("  ", " ");

        String keywords = "MmLlCcQqHhVvZzSsTtAa";
        int[] divNum = new int[] {2, 2, 2, 2, 6, 6, 4, 4, 1, 1, 1, 1, 0, 0, 6, 6, 4, 4, 7, 7};

        LinkedHashMap<String, float[]> map = new LinkedHashMap<>();
        int endIndex;
        for (int i = 0; i < svgData.length(); i = endIndex + 1) {
            char svgKey = svgData.charAt(i);
            int arraySize;
            arraySize = divNum[keywords.indexOf(svgKey)];
            //2019/02/24 how a shit code!
//            switch (svgKey) {
//                case 'M':
//                case 'm':
//                    arraySize = 2;
//                    break;
//                case 'L':
//                case 'l':
//                    arraySize = 2;
//                    break;
//                case 'C':
//                case 'c':
//                case 'S':
//                case 's':
//                    arraySize = 6;
//                    break;
//                case 'Q':
//                case 'q':
//                case 'T':
//                case 't':
//                    arraySize = 4;
//                    break;
//                case 'Z':
//                case 'z':
//                    arraySize = 0;
//                    break;
//                case 'A':
//                case 'a':
//                    arraySize = 7;
//                    break;
//                case 'H':
//                case 'h':
//                case 'V':
//                case 'v':
//                    arraySize = 1;
//                    break;
//                default:
//                    arraySize = -1;
//                    break;
//            }
            endIndex = saveSvg(arraySize, svgData, i, map);
        }

        return map;
    }

    /**
     * Convert svg data type from LinkedHashMap<String,float[]> to string.
     * @param svgData svg data map
     * @return svg data that type of {@link String}
     */
    public String map2String(@NonNull LinkedHashMap<String, float[]> svgData) {
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
     * Translate canvas to set path in the view center.
     */
    private void setPathToCenter(Path path, Canvas canvas) {
        if (path == null) {
            return ;
        }

        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;

        if (pathRectF == null) {
            pathRectF = new RectF();
        }

        path.computeBounds(pathRectF, true);
        centerX = centerX - pathRectF.centerX();
        centerY = centerY - pathRectF.centerY();
        //substitute path offset with translate canvas because it maybe hard that the former that
        //2019/02/24 move whole svg to the view center after split svg as multi subPath. What the shit code!
//        path.offset(centerX, centerY);

        //good job
        if (canvas != null) {
            canvas.translate(centerX, centerY);
        }
    }

    /**
     * Create path with data type of {@link LinkedHashMap}.
     * @param svgMap svg data
     * @return the object about svg and type of {@link Path}
     */
    private Path createPath(@NonNull LinkedHashMap<String, float[]> svgMap) {
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

            //move to saveLastPointer()
//            //save the last pointer
//            if (values.length == 0) {
//                //do nothing
//            } else if (Character.isUpperCase(svgKey)) {
//                if (values.length == 1) {
//                    lastPointer[0] = svgKey == 'H' ? values[0] : lastPointer[0];
//                    lastPointer[1] = svgKey == 'V' ? values[0] : lastPointer[1];
//                } else {
//                    lastPointer[0] = values[values.length - 2];
//                    lastPointer[1] = values[values.length - 1];
//                }
//            } else if (Character.isLowerCase(svgKey)) {
//                if (values.length == 1) {
//                    lastPointer[0] += svgKey == 'h' ? values[0] : lastPointer[0];
//                    lastPointer[1] += svgKey == 'v' ? values[0] : lastPointer[1];
//                } else {
//                    lastPointer[0] += values[values.length - 2];
//                    lastPointer[1] += values[values.length - 1];
//                }
//            }
        }
        return path;
    }

    /**
     * Make deep-copy of object type of {@link LinkedHashMap}.
     * @param fromMap svg data
     * @return svg data type of {@link LinkedHashMap}
     */
    private LinkedHashMap<String, float[]> deepCopy(@NonNull LinkedHashMap<String, float[]> fromMap) {
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

    /**
     * Save svg data to {@param map} is type of {@link LinkedHashMap}.
     * @param arraySize subPath's value count
     * @param svgData svg data
     * @param startIndex start index of subPath's value array
     * @param saveMap which map is svg data save to
     * @return start index of next subPath
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
//            Log.e(TAG, "Not the keyword, skip 1 char");
            return endIndex;
        }

        //get value array.
        float[] values = new float[arraySize];
        endIndex = parseValueArray(svgData, values, valueStartIndex);

        //2019/02/24 This code was so bad because it will let you work more but really unnecessary. How a shit code!
        //save the first pointer value, it is belong to the first subPath that keyword is 'm' or 'M'
//        if (saveMap.size() == 0 && firstPointer == null) {
//            firstPointer = new float[2];
//            System.arraycopy(values, 0, firstPointer, 0, values.length);
//        }
//        //move start pointer to (0,0). Only fix the value of pointer that keyword is upperCase char.
//        if (Character.isUpperCase(keyword)) {
//            for (int i = 0; i < values.length; i++) {
//                values[i] -= firstPointer[i % 2];
//            }
//        }

        saveMap.put(key, values);
        return endIndex;
    }

    /**
     * parse value array from string
     * @param data svg data
     * @param valueSave the value array which values of a subPath save to
     * @param startIndex start index of subPath's value, it has been skip the keyword
     * @return end index of this subPath
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

            //can't find any "," or " ", so do nothing and return current index.
            if (endIndex == -1) { //end of data or something error.
                return startIndex;
            } else if (endIndex == startIndex) { //number value was not found between startIndex and endIndex.
                if (endIndex + 1 == data.length()) {
                    return endIndex;
                }
                startIndex = endIndex + 1; //skip the divide flag of "," or " "
                i--; //loop again that not add i
                continue;
            }

            String value = data.substring(startIndex, endIndex);
            valueSave[i] = Float.parseFloat(value.replace(" ", ""));
            startIndex = endIndex + 1;
        }
        return endIndex;
    }

    private void saveLastPointer(@NonNull LinkedHashMap<String, float[]> pathMap) {
        /* help to divide every close-path from the svg path. */
        if (lastPointer == null) {
            lastPointer = new float[2];
        }

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
     * Split path to the simple path that everyone is a close-path.
     */
    private Path[] splitPath() {
        //LinkedHashMap data -> String data list
        int startPos = 0;
        String fullPath = map2String(startSvg);
        List<String> subPath = new ArrayList<>();
        for (int i = 0; i < fullPath.length(); i++) {
            if (fullPath.charAt(i) == 'z' || fullPath.charAt(i) == 'Z') {
                String dstPath = fullPath.substring(startPos, i + 1);
                subPath.add(dstPath);
                startPos = i + 1;
            }
        }

        //String data list -> LinkedHashMap data
        List<LinkedHashMap<String, float[]>> pathMaps = new ArrayList<>();
        for (int i = 0; i < subPath.size(); i++) {
            String data = subPath.get(i);
            pathMaps.add(string2Map(data));
        }

        /*
         * LinkedHashMap data -> Path
         * 需要将子路径的起始描点类型从【m】修正为【M】
         * get first subPath lastPointer
         */
        Path[] paths = new Path[subPath.size()];
        lastPointer = null;
        saveLastPointer(pathMaps.get(0));
        for (int i = 0; i < pathMaps.size(); i++) {
            LinkedHashMap<String, float[]> curMap = pathMaps.get(i);

            float[] firstValue = (float[])curMap.values().toArray()[0];
            String firstK = (String)curMap.keySet().toArray()[0];
            if (firstK.charAt(0) == 'm') {
                //although this code is work for there, it seems bad style as java code.(not to object-oriented)
//                float[] newValue = curMap.get(firstK);
//                newValue[0] = firstValue[0] + lastPointer[0];
//                newValue[1] = firstValue[1] + lastPointer[1];
                curMap.put(firstK, new float[] {firstValue[0] + lastPointer[0], firstValue[1] + lastPointer[1]});
            }

            if (i > 0) {
                saveLastPointer(curMap);
            }

            paths[i] = createPath(pathMaps.get(i));
        }

        return paths;
    }

    private void svgAnimation() {
        if (endSvg == null || (endSvg.size() != startSvg.size())) {
            Log.e(TAG, "Unable to start animation, different the keyword of fromPath and toPath");
            return ;
        }

        mode = MODE_NORMAL;
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
        //stop animation
        if (isAnimRunning()) {
            stopAnimate(false);
        }

        mode = MODE_TRIM;

        //The svg may be the multi close-path.
        Path[] paths = splitPath();
        trimPath = new Path[paths.length];
        PathMeasure pathRectF = new PathMeasure();
        ValueAnimator animators = ValueAnimator.ofFloat(0f, 1f).setDuration(duration);
        animators.addUpdateListener(animator -> {
            for (int i = 0; i < paths.length; i++) {
                Path dst = new Path();
                /* Order to official document, it can be make animation workaround even sdk version number
                  below to 19 and display on the hardware-accelerated canvas. */
                dst.rLineTo(0f, 0f);

                pathRectF.setPath(paths[i], false);
                float fraction = animator.getAnimatedFraction();
                float pathLen = pathRectF.getLength();
                float startD, endD;
                if (dstLen <= 0) { //fully trim
                    startD = 0f;
                    endD = (isReverse ? (1f - fraction) : fraction) * pathLen;
                } else {
                    startD = pathLen * fraction;
                    endD = startD + dstLen;
                }

                if ((startD == 0f && endD == 0f)
                        || pathRectF.getSegment(startD, endD, dst, true)) {
                    trimPath[i] = dst;
                }
            }
            postInvalidate();
        });
        animators.start();
    }
}
