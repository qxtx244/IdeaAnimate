/*
 * @CreateDate 2019/01/16 14:00.
 * @Author QXTX-GOSPELL
 */

package com.qxtx.test.animate;

import android.animation.ArgbEvaluator;
import android.animation.TypeEvaluator;

/**
 * Color smooth transition. It must be to use the params that take a specific flavor just like as
 *  "123456","#123456", "12345678" or "#12345678". It will return value type of String. There are another
 *  implementation by android.jar. See {@link ArgbEvaluator}. You can use new ArgbEvaluator() while
 *  animator use value of color that type is int.
 **/
public final class ArgbTypeEvaluator implements TypeEvaluator {
    private final int[] currentColors = {-1, -1, -1};
    private final int[] startColors = new int[3];
    private final int[] endColors = new int[3];
    private final int[] deltaColors = new int[3];
    private int colorDelta = 0;

    @Override
    public Object evaluate(float fraction, Object startValue, Object endValue) {
        String startColorValue = (String) startValue;
        String endColorValue = (String) endValue;

        //分离ARGB四个值
        parseValue(startColorValue, endColorValue);

        for (int i = 0; i < currentColors.length; i++) {
            currentColors[i] = currentColors[i] == -1 ? startColors[i] : currentColors[i];  // 初始化颜色的值
            deltaColors[i] = Math.abs(startColors[i] - endColors[i]);  // 计算初始颜色和结束颜色之间的差值
        }
        colorDelta = deltaColors[0] + deltaColors[1] + deltaColors[2];

        //？？？
        if (currentColors[0] != endColors[0]) {
            currentColors[0] = getCurrentColor(startColors[0], endColors[0], colorDelta, 0, fraction);
        } else if (currentColors[1] != endColors[1]) {
            currentColors[1] = getCurrentColor(startColors[1], endColors[1], colorDelta, deltaColors[0], fraction);
        } else if (currentColors[2] != endColors[2]) {
            currentColors[2] = getCurrentColor(startColors[2], endColors[2], colorDelta, deltaColors[0] + deltaColors[1], fraction);
        }

        // 将计算出的当前颜色的值组装返回
        return "#" + getHexString(currentColors[0]) + getHexString(currentColors[1]) + getHexString(currentColors[2]);
    }

    /**
     * 根据fraction值来计算当前的颜色。
     */
    private int getCurrentColor(int startColors, int endColors, int colorDiff, int offset, float fraction) {
        int currentColors;
        if (startColors > endColors) {
            currentColors = (int) (startColors - (fraction * colorDiff - offset));
            if (currentColors < endColors) {
                currentColors = endColors;
            }
        } else {
            currentColors = (int) (startColors + (fraction * colorDiff - offset));
            if (currentColors > endColors) {
                currentColors = endColors;
            }
        }
        return currentColors;
    }

    /**
     * 将10进制颜色值转换成16进制。
     */
    private String getHexString(int value) {
        String hexString = Integer.toHexString(value);
        return  hexString.length() == 1 ? "0" + hexString : hexString;
    }

    /**
     * 得到的颜色等效值可能有几种情况：
     * "123456"  "#123456" "#12345678"  "12345678"
     * 全部转换为RGB格式
     */
    private void parseValue(String startValue, String endValue) {
        startValue = parseValueNum(startValue);
        endValue = parseValueNum(endValue);

        for (int i = 0; i < startColors.length; i += 2) {
            startColors[i] = Integer.parseInt(startValue.substring(i, i + 2), 16);
            endColors[i] = Integer.parseInt(endValue.substring(i, i + 2), 16);
        }
    }

    private String parseValueNum(String value) {
        switch (value.length()) {
            case 6:
                return value;
            case 7:
                return value.substring(1);
            case 8:
                return value.substring(3);
            case 9:
                return value.substring(2);
            default:
                return "ffffff";
        }
    }
}
