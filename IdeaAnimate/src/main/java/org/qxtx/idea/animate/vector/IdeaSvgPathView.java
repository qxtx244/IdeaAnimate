package org.qxtx.idea.animate.vector;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.Arrays;

/**
 * @CreateDate 2019/02/14 14:26.
 * @Author QXTX-GOSPELL
 */

public class IdeaSvgPathView extends View {
    private static final String TAG = "IdeaSvgPathAnimate";

    private static final String AUTO_COLOR = "#1E90FF";
    private Path fromPath;
    private Path toPath;
    private float[] firstPointer;
    private Paint paint;
    private int color;
    private boolean moveSvg;
    private boolean isFillPath;

    public IdeaSvgPathView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public IdeaSvgPathView(Context context, String svgPath, int color, boolean isFillPath) {
        super(context);
        init();
        init(svgPath, color, isFillPath);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Path usePath = moveSvg ? toPath : fromPath;
        if (usePath != null) {
            Paint.Style useStyle = isFillPath ? Paint.Style.FILL_AND_STROKE : Paint.Style.STROKE;
            paint.setStyle(useStyle);
            canvas.translate(getWidth() / 2f - firstPointer[0], getHeight() / 2f - firstPointer[1]);
            canvas.drawPath(usePath, paint);
        }
    }

    private void init() {
        paint = new Paint();
        paint.setStrokeWidth(5f);
        paint.setColor(Color.parseColor(AUTO_COLOR));
        paint.setAntiAlias(true);
        firstPointer = new float[2];
        moveSvg = false;
    }

    public void init(String svgPath, int color, boolean isFillPath) {
        this.color = color;
        this.isFillPath = isFillPath;
        paint.setColor(color);
        fromPath = parseSvgPath(svgPath);
        postInvalidate();
    }

    public void init(String svgPath, boolean isFillPath) {
        init(svgPath, Color.parseColor(AUTO_COLOR), isFillPath);
    }

    /**
     * Parse a string of svg path.
     * @param svgData
     */
    private Path parseSvgPath(String svgData) {
        Path path = new Path();
        int endIndex = 0;
        float[] values;

        svgData = svgData.trim();
        for (int i = 0; i < svgData.length(); i = endIndex + 1) {
            char type = svgData.charAt(i);
            switch (type) {
                case 'M':
                case 'm':
                case 'L':
                case 'l':
                    values = new float[2];
                    endIndex = parseValues(svgData, values, i + 1);
                    if (type == 'M') {
                        path.moveTo(values[0], values[1]);
                        firstPointer = Arrays.copyOf(values, values.length);
                    } else if (type == 'm') {
                        path.rMoveTo(values[0], values[1]);
                    } else if (type == 'L') {
                        path.lineTo(values[0], values[1]);
                    } else {
                        path.rLineTo(values[0], values[1]);
                    }
                    break;
                case 'Q':
                case 'q':
                    values = new float[4];
                    endIndex = parseValues(svgData, values, i + 1);
                    if (type == 'Q') {
                        path.quadTo(values[0], values[1], values[2], values[3]);
                    } else {
                        path.rQuadTo(values[0], values[1], values[2], values[3]);
                    }
                    break;
                case 'C':
                case 'c':
                    values = new float[6];
                    endIndex = parseValues(svgData, values, i + 1);
                    if (type == 'C') {
                        path.cubicTo(values[0], values[1], values[2], values[3], values[4], values[5]);
                    } else {
                        path.rCubicTo(values[0], values[1], values[2], values[3], values[4], values[5]);
                    }
                    break;
                case 'H':
                case 'h':
                case 'V':
                case 'v':
                    values = new float[1];
                    endIndex = parseValues(svgData, values, i + 1);
                    break;
                case 'Z':
                case 'z':
                    path.close();
                    endIndex = i + 1;
                    break;
            }
        }

        return path;
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
